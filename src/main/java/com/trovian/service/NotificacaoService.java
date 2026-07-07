package com.trovian.service;

import com.trovian.dto.NotificacaoDTO;
import com.trovian.dto.NotificacaoUpdateDTO;
import com.trovian.dto.dashboard.NotificacaoDashboardDTO;
import com.trovian.dto.dashboard.TendenciaDiariaDTO;
import com.trovian.entity.Motorista;
import com.trovian.entity.Notificacao;
import com.trovian.entity.Notificacao.TipoNotificacao;
import com.trovian.entity.Usuario;
import com.trovian.repository.NotificacaoRepository;
import com.trovian.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificacaoService {

    private final NotificacaoRepository notificacaoRepository;
    private final UsuarioRepository usuarioRepository;

    // Prefixos de referenciaTipo → categoria para o dashboard
    private static final Map<String, String> CATEGORIA_MAP = Map.ofEntries(
            Map.entry("CONTA_PAGAR", "FINANCEIRO"),
            Map.entry("CONTA_RECEBER", "FINANCEIRO"),
            Map.entry("RESUMO_FINANCEIRO", "FINANCEIRO"),
            Map.entry("PLANO_CLIENTE", "FINANCEIRO"),
            Map.entry("COMISSAO", "FINANCEIRO"),
            Map.entry("MANUTENCAO", "FROTA"),
            Map.entry("CNH", "FROTA"),
            Map.entry("CHECKLIST", "FROTA"),
            Map.entry("VEICULO", "FROTA"),
            Map.entry("FALHA", "FROTA"),
            Map.entry("VIAGEM", "OPERACIONAL"),
            Map.entry("ORDEM_SERVICO", "OPERACIONAL")
    );

    // -------------------------------------------------------------------------
    // Criação de boas-vindas (existentes)
    // -------------------------------------------------------------------------

    @Transactional
    public void criarNotificacaoBemVindo(Usuario usuario) {
        String mensagem = String.format(
                "Bem vindo %s a Trajetto. Por aqui você vai receber notificações de Motoristas e Veículos. " +
                "Você também pode digitar uma placa da sua frota e receber dados do veiculo.",
                usuario.getNome()
        );

        Notificacao notificacao = new Notificacao();
        notificacao.setUsuario(usuario);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(TipoNotificacao.MENSAGEM_BEM_VINDO);

        notificacaoRepository.save(notificacao);
    }

    @Transactional
    public void criarNotificacaoBemVindoMotorista(Motorista motorista) {
        String mensagem = String.format(
                "Bem vindo %s a Trajetto. Por aqui você vai receber notificações da base de operações. " +
                "Se você digitar Menu, você tera opções de trabalho.",
                motorista.getNome()
        );

        Notificacao notificacao = new Notificacao();
        notificacao.setMotorista(motorista);
        notificacao.setMensagem(mensagem);
        notificacao.setTipo(TipoNotificacao.MENSAGEM_BEM_VINDO);

        notificacaoRepository.save(notificacao);
    }

    // -------------------------------------------------------------------------
    // Leitura
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public NotificacaoDTO findById(Long id) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada com ID: " + id));
        return toDTO(notificacao);
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoDTO> findAll(Pageable pageable) {
        return notificacaoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoDTO> findByUsuarioLogado(String email, Pageable pageable) {
        Usuario usuario = getUsuarioByEmail(email);
        return notificacaoRepository.findByUsuarioIdOrderByDataCriacaoDesc(usuario.getId(), pageable)
                .map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<NotificacaoDTO> findByMotoristasDoUsuario(String email, Pageable pageable) {
        Usuario usuario = getUsuarioByEmail(email);
        Long clienteId = usuario.getCliente().getId();
        return notificacaoRepository.findByMotoristaClienteIdOrderByDataCriacaoDesc(clienteId, pageable)
                .map(this::toDTO);
    }

    // -------------------------------------------------------------------------
    // Atualização
    // -------------------------------------------------------------------------

    @Transactional
    public NotificacaoDTO atualizar(Long id, NotificacaoUpdateDTO dto) {
        Notificacao notificacao = notificacaoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notificação não encontrada com ID: " + id));

        if (dto.getStatus() != null) {
            notificacao.setStatus(dto.getStatus());
            if (dto.getStatus() == Notificacao.StatusNotificacao.ENVIADA && notificacao.getDataEnvio() == null) {
                notificacao.setDataEnvio(LocalDateTime.now());
            }
        }
        if (dto.getRespostaMotorista() != null) {
            notificacao.setRespostaMotorista(dto.getRespostaMotorista());
            if (notificacao.getDataResposta() == null) {
                notificacao.setDataResposta(LocalDateTime.now());
            }
            if (notificacao.getStatus() != Notificacao.StatusNotificacao.RESPONDIDA) {
                notificacao.setStatus(Notificacao.StatusNotificacao.RESPONDIDA);
            }
        }

        return toDTO(notificacaoRepository.save(notificacao));
    }

    // -------------------------------------------------------------------------
    // Dashboard
    // -------------------------------------------------------------------------

    @Transactional(readOnly = true)
    public NotificacaoDashboardDTO getDashboard(String email, int diasTendencia) {
        Usuario usuario = getUsuarioByEmail(email);
        Long usuarioId = usuario.getId();
        Long clienteId = usuario.getCliente().getId();

        Map<String, Long> porReferenciaTipo = new LinkedHashMap<>();
        Map<String, Long> porCategoria = new LinkedHashMap<>();
        long total = 0;

        List<Object[]> refTipoRows = notificacaoRepository.countByRefTipo(usuarioId, clienteId);
        for (Object[] row : refTipoRows) {
            String refTipo = row[0] != null ? row[0].toString() : "SEM_TIPO";
            long count = (Long) row[1];
            porReferenciaTipo.put(refTipo, count);
            total += count;
            porCategoria.merge(resolverCategoria(refTipo), count, Long::sum);
        }

        Map<String, Long> porStatus = new LinkedHashMap<>();
        long totalNaoLidas = 0;
        List<Object[]> statusRows = notificacaoRepository.countByStatus(usuarioId, clienteId);
        for (Object[] row : statusRows) {
            String statusKey = row[0].toString();
            long count = (Long) row[1];
            porStatus.put(statusKey, count);
            if ("PENDENTE".equals(statusKey) || "ENVIADA".equals(statusKey)) {
                totalNaoLidas += count;
            }
        }

        Map<String, Long> porTipo = new LinkedHashMap<>();
        List<Object[]> tipoRows = notificacaoRepository.countByTipo(usuarioId, clienteId);
        for (Object[] row : tipoRows) {
            porTipo.put(row[0].toString(), (Long) row[1]);
        }

        LocalDateTime dataInicio = LocalDateTime.now().minusDays(diasTendencia);
        List<TendenciaDiariaDTO> tendencia = notificacaoRepository
                .tendenciaDiaria(usuarioId, clienteId, dataInicio)
                .stream()
                .map(row -> new TendenciaDiariaDTO(row[0].toString(), (Long) row[1]))
                .collect(Collectors.toList());

        List<NotificacaoDTO> recentes = notificacaoRepository
                .findRecentes(usuarioId, clienteId, PageRequest.of(0, 10))
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());

        return NotificacaoDashboardDTO.builder()
                .total(total)
                .totalNaoLidas(totalNaoLidas)
                .porStatus(porStatus)
                .porTipo(porTipo)
                .porCategoria(porCategoria)
                .porReferenciaTipo(porReferenciaTipo)
                .recentes(recentes)
                .tendencia(tendencia)
                .build();
    }

    // -------------------------------------------------------------------------
    // Helpers privados
    // -------------------------------------------------------------------------

    private Usuario getUsuarioByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado: " + email));
    }

    private String resolverCategoria(String referenciaTipo) {
        if (referenciaTipo == null) return "OUTROS";
        for (Map.Entry<String, String> entry : CATEGORIA_MAP.entrySet()) {
            if (referenciaTipo.startsWith(entry.getKey())) {
                return entry.getValue();
            }
        }
        return "OUTROS";
    }

    private NotificacaoDTO toDTO(Notificacao n) {
        return NotificacaoDTO.builder()
                .id(n.getId())
                .motoristaId(n.getMotorista() != null ? n.getMotorista().getId() : null)
                .motoristaNome(n.getMotorista() != null ? n.getMotorista().getNome() : null)
                .usuarioId(n.getUsuario() != null ? n.getUsuario().getId() : null)
                .usuarioNome(n.getUsuario() != null ? n.getUsuario().getNome() : null)
                .mensagem(n.getMensagem())
                .tipo(n.getTipo())
                .status(n.getStatus())
                .dataCriacao(n.getDataCriacao())
                .dataEnvio(n.getDataEnvio())
                .tentativasEnvio(n.getTentativasEnvio())
                .erroEnvio(n.getErroEnvio())
                .respostaMotorista(n.getRespostaMotorista())
                .dataResposta(n.getDataResposta())
                .referenciaTipo(n.getReferenciaTipo())
                .referenciaId(n.getReferenciaId())
                .build();
    }
}
