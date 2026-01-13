package com.trovian.service;

import com.trovian.dto.relatorios.*;
import com.trovian.entity.relatorios.*;
import com.trovian.repository.RelatorioGeradoRepository;
import com.trovian.repository.RelatorioTemplateRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RelatorioService {

    private final RelatorioTemplateRepository templateRepository;
    private final RelatorioGeradoRepository geradoRepository;
    private final JdbcTemplate jdbcTemplate;
    private final ExportacaoService exportacaoService;

    @Transactional(readOnly = true)
    public List<RelatorioTemplateDTO> listarTemplates() {
        return templateRepository.findByAtivoTrue().stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<RelatorioTemplateDTO> listarTemplatesPorCategoria(CategoriaRelatorio categoria) {
        return templateRepository.findByCategoriaAndAtivoTrue(categoria).stream()
                .map(this::converterParaDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public RelatorioTemplateDTO buscarTemplatePorId(Long id) {
        RelatorioTemplate template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template não encontrado: " + id));
        return converterParaDTO(template);
    }

    @Transactional
    public byte[] gerarRelatorio(RelatorioRequestDTO request) {
        log.info("Gerando relatório - Template ID: {}, Formato: {}",
                request.getTemplateId(), request.getFormato());

        // 1. Buscar template
        RelatorioTemplate template = templateRepository.findById(request.getTemplateId())
                .orElseThrow(() -> new RuntimeException("Template não encontrado"));

        // 2. Validar parâmetros obrigatórios
        validarParametros(template, request.getParametros());

        // 3. Executar query com parâmetros
        RelatorioResultadoDTO resultado = executarQuery(template, request.getParametros());

        // 4. Gerar arquivo no formato solicitado
        byte[] arquivo = exportacaoService.exportar(
                resultado,
                request.getFormato(),
                request.getIncluirGraficos()
        );

        // 5. Registrar relatório gerado
        registrarRelatorioGerado(template, request, resultado.getTotalRegistros());

        return arquivo;
    }

    private RelatorioResultadoDTO executarQuery(RelatorioTemplate template, Map<String, Object> parametros) {
        // Montar SQL final
        String sql = montarSQL(template, parametros);

        log.debug("SQL gerado: {}", sql);

        // Executar query
        List<Map<String, Object>> dados = jdbcTemplate.queryForList(sql);

        // Extrair nomes das colunas
        List<String> colunas = template.getCampos().stream()
                .filter(CampoRelatorio::getVisivel)
                .sorted(Comparator.comparing(CampoRelatorio::getOrdem))
                .map(CampoRelatorio::getLabel)
                .collect(Collectors.toList());

        // Calcular totalizadores
        Map<String, Object> totalizadores = calcularTotalizadores(template, dados);

        return new RelatorioResultadoDTO(
                template.getNome(),
                colunas,
                dados,
                (long) dados.size(),
                totalizadores
        );
    }

    private String montarSQL(RelatorioTemplate template, Map<String, Object> parametros) {
        StringBuilder sql = new StringBuilder();

        // SELECT
        if (template.getQueryCampos() != null && !template.getQueryCampos().isEmpty()) {
            sql.append(template.getQueryCampos());
        } else {
            sql.append("SELECT * ");
        }

        // FROM e JOINs
        sql.append(" FROM ").append(template.getQueryBase());

        if (template.getQueryJoins() != null && !template.getQueryJoins().isEmpty()) {
            sql.append(" ").append(template.getQueryJoins());
        }

        // WHERE (aplicar parâmetros)
        if (!parametros.isEmpty()) {
            sql.append(" WHERE 1=1 ");
            parametros.forEach((key, value) -> {
                if (value != null) {
                    sql.append(" AND ").append(montarCondicao(key, value));
                }
            });
        }

        // GROUP BY
        if (template.getQueryGroupBy() != null && !template.getQueryGroupBy().isEmpty()) {
            sql.append(" ").append(template.getQueryGroupBy());
        }

        // ORDER BY
        if (template.getQueryOrderBy() != null && !template.getQueryOrderBy().isEmpty()) {
            sql.append(" ").append(template.getQueryOrderBy());
        }

        return sql.toString();
    }

    private String montarCondicao(String parametro, Object valor) {
        if (parametro.contains("data_inicio")) {
            return String.format("DATE(created_at) >= '%s'", valor);
        } else if (parametro.contains("data_fim")) {
            return String.format("DATE(created_at) <= '%s'", valor);
        } else if (parametro.endsWith("_id")) {
            return String.format("%s = %s", parametro, valor);
        } else if (parametro.contains("status")) {
            return String.format("%s = '%s'", parametro, valor);
        }
        return String.format("%s = '%s'", parametro, valor);
    }

    private Map<String, Object> calcularTotalizadores(RelatorioTemplate template, List<Map<String, Object>> dados) {
        Map<String, Object> totalizadores = new HashMap<>();

        template.getCampos().stream()
                .filter(CampoRelatorio::getTotalizavel)
                .forEach(campo -> {
                    if (campo.getTipo() == TipoCampo.MOEDA || campo.getTipo() == TipoCampo.DECIMAL) {
                        double total = dados.stream()
                                .mapToDouble(row -> {
                                    Object valor = row.get(campo.getNome());
                                    return valor != null ? Double.parseDouble(valor.toString()) : 0.0;
                                })
                                .sum();
                        totalizadores.put(campo.getNome() + "_total", total);
                    }
                });

        return totalizadores;
    }

    private void validarParametros(RelatorioTemplate template, Map<String, Object> parametros) {
        template.getParametros().stream()
                .filter(ParametroRelatorio::getObrigatorio)
                .forEach(param -> {
                    if (!parametros.containsKey(param.getNome()) || parametros.get(param.getNome()) == null) {
                        throw new RuntimeException("Parâmetro obrigatório não informado: " + param.getLabel());
                    }
                });
    }

    private void registrarRelatorioGerado(RelatorioTemplate template, RelatorioRequestDTO request, Long totalRegistros) {
        RelatorioGerado gerado = new RelatorioGerado();
        gerado.setTemplate(template);
        gerado.setFormato(request.getFormato());
        gerado.setNomeArquivo(gerarNomeArquivo(template, request.getFormato()));
        gerado.setParametrosUsados(request.getParametros().toString());
        gerado.setTotalRegistros(totalRegistros);
        // gerado.setUsuarioId(usuarioId); // Pegar do SecurityContext

        geradoRepository.save(gerado);
    }

    private String gerarNomeArquivo(RelatorioTemplate template, FormatoRelatorio formato) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String nomeBase = template.getNome().replaceAll("[^a-zA-Z0-9]", "_").toLowerCase();
        return String.format("%s_%s%s", nomeBase, timestamp, formato.getExtensao());
    }

    private RelatorioTemplateDTO converterParaDTO(RelatorioTemplate template) {
        RelatorioTemplateDTO dto = new RelatorioTemplateDTO();
        dto.setId(template.getId());
        dto.setNome(template.getNome());
        dto.setDescricao(template.getDescricao());
        dto.setCategoria(template.getCategoria());
        dto.setAtivo(template.getAtivo());

        List<ParametroRelatorioDTO> parametrosDTO = template.getParametros().stream()
                .map(this::converterParametroParaDTO)
                .collect(Collectors.toList());
        dto.setParametros(parametrosDTO);

        List<CampoRelatorioDTO> camposDTO = template.getCampos().stream()
                .map(this::converterCampoParaDTO)
                .collect(Collectors.toList());
        dto.setCampos(camposDTO);

        return dto;
    }

    private ParametroRelatorioDTO converterParametroParaDTO(ParametroRelatorio param) {
        ParametroRelatorioDTO dto = new ParametroRelatorioDTO();
        dto.setId(param.getId());
        dto.setNome(param.getNome());
        dto.setLabel(param.getLabel());
        dto.setTipo(param.getTipo());
        dto.setObrigatorio(param.getObrigatorio());
        dto.setValorPadrao(param.getValorPadrao());
        dto.setOpcoes(param.getOpcoes());
        dto.setOrdem(param.getOrdem());
        return dto;
    }

    private CampoRelatorioDTO converterCampoParaDTO(CampoRelatorio campo) {
        CampoRelatorioDTO dto = new CampoRelatorioDTO();
        dto.setId(campo.getId());
        dto.setNome(campo.getNome());
        dto.setLabel(campo.getLabel());
        dto.setTipo(campo.getTipo());
        dto.setVisivel(campo.getVisivel());
        dto.setTotalizavel(campo.getTotalizavel());
        dto.setFormato(campo.getFormato());
        dto.setOrdem(campo.getOrdem());
        dto.setLargura(campo.getLargura());
        return dto;
    }
}
