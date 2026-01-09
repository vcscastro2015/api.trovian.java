package com.trovian.service;

import com.trovian.dto.ChecklistRealizadoDTO;
import com.trovian.dto.EstatisticasChecklistDTO;
import com.trovian.dto.OrdemServicoDTO;
import com.trovian.dto.RespostaItemChecklistDTO;
import com.trovian.entity.*;
import com.trovian.enums.StatusChecklist;
import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoManutencao;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChecklistRealizadoService {

    private final ChecklistRealizadoRepository checklistRealizadoRepository;
    private final ModeloChecklistRepository modeloChecklistRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final ViagemRepository viagemRepository;
    private final ItemModeloChecklistRepository itemModeloChecklistRepository;
    private final OrdemServicoService ordemServicoService;

    @Transactional(readOnly = true)
    public Page<ChecklistRealizadoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os checklists realizados com paginação");
        Page<ChecklistRealizado> checklists = checklistRealizadoRepository.findAll(pageable);
        return checklists.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ChecklistRealizadoDTO findById(UUID id) {
        log.info("Buscando checklist realizado com ID: {}", id);
        ChecklistRealizado checklist = checklistRealizadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist realizado não encontrado com ID: " + id));
        return toDTO(checklist);
    }

    @Transactional(readOnly = true)
    public Page<ChecklistRealizadoDTO> findByVeiculoId(Long veiculoId, Pageable pageable) {
        log.info("Buscando checklists do veículo com ID: {}", veiculoId);
        Page<ChecklistRealizado> checklists = checklistRealizadoRepository.findByVeiculoId(veiculoId, pageable);
        return checklists.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ChecklistRealizadoDTO> findByMotoristaId(Long motoristaId, Pageable pageable) {
        log.info("Buscando checklists do motorista com ID: {}", motoristaId);
        Page<ChecklistRealizado> checklists = checklistRealizadoRepository.findByMotoristaId(motoristaId, pageable);
        return checklists.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ChecklistRealizadoDTO> findByStatus(StatusChecklist status, Pageable pageable) {
        log.info("Buscando checklists com status: {}", status);
        Page<ChecklistRealizado> checklists = checklistRealizadoRepository.findByStatus(status, pageable);
        return checklists.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public EstatisticasChecklistDTO getEstatisticas() {
        log.info("Calculando estatísticas de checklists realizados");

        Long totalRealizados = checklistRealizadoRepository.count();
        Long aprovados = checklistRealizadoRepository.countByStatus(StatusChecklist.APROVADO);
        Long reprovados = checklistRealizadoRepository.countByStatus(StatusChecklist.REPROVADO);
        Long emAndamento = checklistRealizadoRepository.countByStatus(StatusChecklist.EM_ANDAMENTO);

        // Calcula percentual de conformidade
        // Conformidade = (aprovados / total de finalizados) * 100
        Long totalFinalizados = aprovados + reprovados;
        Double conformidadeGeral = 0.0;

        if (totalFinalizados > 0) {
            conformidadeGeral = (aprovados.doubleValue() / totalFinalizados.doubleValue()) * 100.0;
            // Arredonda para 2 casas decimais
            conformidadeGeral = Math.round(conformidadeGeral * 100.0) / 100.0;
        }

        EstatisticasChecklistDTO estatisticas = new EstatisticasChecklistDTO(
            totalRealizados,
            aprovados,
            reprovados,
            emAndamento,
            conformidadeGeral
        );

        log.info("Estatísticas calculadas: {}", estatisticas);
        return estatisticas;
    }

    @Transactional(readOnly = true)
    public ChecklistRealizadoDTO getUltimoChecklistAprovado(Long veiculoId) {
        log.info("Buscando último checklist aprovado do veículo com ID: {}", veiculoId);
        return checklistRealizadoRepository
                .findUltimoChecklistPorVeiculoEStatus(veiculoId, StatusChecklist.APROVADO)
                .map(this::toDTO)
                .orElse(null);
    }

    @Transactional
    public ChecklistRealizadoDTO create(ChecklistRealizadoDTO dto) {
        log.info("Criando novo checklist realizado");

        ModeloChecklist modelo = modeloChecklistRepository.findById(dto.getModeloChecklistId())
                .orElseThrow(() -> new RuntimeException("Modelo de checklist não encontrado com ID: " + dto.getModeloChecklistId()));

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + dto.getMotoristaId()));

        ChecklistRealizado checklist = toEntity(dto);
        checklist.setModeloChecklist(modelo);
        checklist.setVeiculo(veiculo);
        checklist.setMotorista(motorista);

        // Viagem é opcional
        if (dto.getViagemId() != null) {
            Viagem viagem = viagemRepository.findById(dto.getViagemId())
                    .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + dto.getViagemId()));
            checklist.setViagem(viagem);
        }

        // Gera número do checklist
        checklist.setNumeroChecklist(gerarNumeroChecklist());

        ChecklistRealizado savedChecklist = checklistRealizadoRepository.save(checklist);

        // Salva as respostas
        if (dto.getRespostas() != null && !dto.getRespostas().isEmpty()) {
            for (RespostaItemChecklistDTO respostaDTO : dto.getRespostas()) {
                RespostaItemChecklist resposta = toRespostaEntity(respostaDTO);
                resposta.setChecklistRealizado(savedChecklist);

                // Busca o item do modelo
                ItemModeloChecklist itemModelo = itemModeloChecklistRepository.findById(respostaDTO.getItemModeloId())
                        .orElseThrow(() -> new RuntimeException("Item do modelo não encontrado com ID: " + respostaDTO.getItemModeloId()));
                resposta.setItemModelo(itemModelo);

                savedChecklist.getRespostas().add(resposta);
            }
            savedChecklist = checklistRealizadoRepository.save(savedChecklist);
        }

        log.info("Checklist realizado criado com sucesso. ID: {}, Número: {}", savedChecklist.getId(), savedChecklist.getNumeroChecklist());
        return toDTO(savedChecklist);
    }

    @Transactional
    public ChecklistRealizadoDTO update(UUID id, ChecklistRealizadoDTO dto) {
        log.info("Atualizando checklist realizado com ID: {}", id);

        ChecklistRealizado checklist = checklistRealizadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist realizado não encontrado com ID: " + id));

        // Atualiza campos básicos
        checklist.setKmVeiculo(dto.getKmVeiculo());
        checklist.setStatus(dto.getStatus());
        checklist.setObservacoesGerais(dto.getObservacoesGerais());
        checklist.setLocalizacao(dto.getLocalizacao());
        checklist.setDataHoraConclusao(dto.getDataHoraConclusao());

        // Atualiza respostas
        if (dto.getRespostas() != null) {
            checklist.getRespostas().clear();
            for (RespostaItemChecklistDTO respostaDTO : dto.getRespostas()) {
                RespostaItemChecklist resposta = toRespostaEntity(respostaDTO);
                resposta.setChecklistRealizado(checklist);

                ItemModeloChecklist itemModelo = itemModeloChecklistRepository.findById(respostaDTO.getItemModeloId())
                        .orElseThrow(() -> new RuntimeException("Item do modelo não encontrado com ID: " + respostaDTO.getItemModeloId()));
                resposta.setItemModelo(itemModelo);

                checklist.getRespostas().add(resposta);
            }
        }

        ChecklistRealizado updatedChecklist = checklistRealizadoRepository.save(checklist);
        log.info("Checklist realizado atualizado com sucesso. ID: {}", id);
        return toDTO(updatedChecklist);
    }

    @Transactional
    public ChecklistRealizadoDTO finalizar(UUID id) {
        log.info("Finalizando checklist com ID: {}", id);

        ChecklistRealizado checklist = checklistRealizadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Checklist realizado não encontrado com ID: " + id));

        // Usa o método de negócio da entidade
        checklist.finalizar();

        ChecklistRealizado finalized = checklistRealizadoRepository.save(checklist);
        log.info("Checklist finalizado com status: {}", finalized.getStatus());

        // Se reprovado, gera ordem de serviço automática para itens que requerem atenção
        if (finalized.getStatus() == StatusChecklist.REPROVADO) {
            gerarOrdemServicoAutomatica(finalized);
        }

        return toDTO(finalized);
    }

    /**
     * Gera ordem de serviço automática para itens reprovados do checklist
     */
    private void gerarOrdemServicoAutomatica(ChecklistRealizado checklist) {
        log.info("Gerando ordem de serviço automática para checklist reprovado: {}", checklist.getId());

        List<RespostaItemChecklist> itensReprovados = checklist.getRespostas().stream()
                .filter(r -> r.getRequerAtencao() != null &&
                            r.getRequerAtencao() &&
                            "NAO_OK".equals(r.getResposta()))
                .collect(Collectors.toList());

        if (itensReprovados.isEmpty()) {
            log.warn("Checklist reprovado mas sem itens que requerem atenção");
            return;
        }

        try {
            // Monta descrição do problema com base nos itens reprovados
            StringBuilder descricaoProblema = new StringBuilder();
            descricaoProblema.append("Problemas detectados no Checklist: ")
                    .append(checklist.getNumeroChecklist())
                    .append("\n\nItens reprovados:\n");

            for (RespostaItemChecklist resposta : itensReprovados) {
                descricaoProblema.append("- ")
                        .append(resposta.getItemModelo().getDescricao());
                if (resposta.getObservacao() != null && !resposta.getObservacao().isEmpty()) {
                    descricaoProblema.append(": ").append(resposta.getObservacao());
                }
                descricaoProblema.append("\n");
            }

            if (checklist.getObservacoesGerais() != null && !checklist.getObservacoesGerais().isEmpty()) {
                descricaoProblema.append("\nObservações gerais: ")
                        .append(checklist.getObservacoesGerais());
            }

            // Cria DTO da ordem de serviço
            OrdemServicoDTO ordemServicoDTO = new OrdemServicoDTO();
            ordemServicoDTO.setNumeroOs(gerarNumeroOS());
            ordemServicoDTO.setVeiculoId(checklist.getVeiculo().getId());
            ordemServicoDTO.setMotoristaId(checklist.getMotorista().getId());
            ordemServicoDTO.setTipoManutencao(TipoManutencao.CORRETIVA);
            ordemServicoDTO.setKmVeiculo(checklist.getKmVeiculo());
            ordemServicoDTO.setDataAbertura(LocalDate.now());
            ordemServicoDTO.setStatus(StatusOrdemServico.ABERTA);
            ordemServicoDTO.setDescricaoProblema(descricaoProblema.toString());
            ordemServicoDTO.setObservacoes("Gerado automaticamente a partir do checklist " + checklist.getNumeroChecklist());

            // Cria a ordem de serviço
            OrdemServicoDTO ordemCriada = ordemServicoService.create(ordemServicoDTO);
            log.info("Ordem de serviço criada automaticamente: {} para o checklist: {}",
                    ordemCriada.getNumeroOs(), checklist.getNumeroChecklist());

        } catch (Exception e) {
            log.error("Erro ao criar ordem de serviço automática para checklist {}: {}",
                    checklist.getNumeroChecklist(), e.getMessage(), e);
            // Não propaga a exceção para não bloquear a finalização do checklist
        }
    }

    private String gerarNumeroOS() {
        int ano = LocalDate.now().getYear();
        long count = ordemServicoService.findAll(0, 1, "id", "DESC").getTotalElements() + 1;
        return String.format("OS-%d-%05d", ano, count);
    }

    @Transactional
    public void delete(UUID id) {
        log.info("Deletando checklist realizado com ID: {}", id);

        if (!checklistRealizadoRepository.existsById(id)) {
            throw new RuntimeException("Checklist realizado não encontrado com ID: " + id);
        }

        checklistRealizadoRepository.deleteById(id);
        log.info("Checklist realizado deletado com sucesso. ID: {}", id);
    }

    /**
     * Verifica se o veículo possui checklist obrigatório aprovado
     */
    @Transactional(readOnly = true)
    public boolean verificarChecklistObrigatorio(Long veiculoId) {
        log.info("Verificando checklist obrigatório do veículo com ID: {}", veiculoId);

        LocalDateTime hoje = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        List<ChecklistRealizado> checklists = checklistRealizadoRepository
                .findChecklistsRecentesPorVeiculo(veiculoId, hoje);

        boolean temChecklistAprovado = checklists.stream()
                .anyMatch(c -> c.getStatus() == StatusChecklist.APROVADO &&
                              c.getModeloChecklist().getObrigatorio());

        log.info("Veículo {} {} checklist obrigatório aprovado hoje",
                veiculoId, temChecklistAprovado ? "possui" : "não possui");

        return temChecklistAprovado;
    }

    private String gerarNumeroChecklist() {
        int ano = LocalDateTime.now().getYear();
        long count = checklistRealizadoRepository.count() + 1;
        return String.format("CK-%d-%05d", ano, count);
    }

    private ChecklistRealizadoDTO toDTO(ChecklistRealizado checklist) {
        ChecklistRealizadoDTO dto = new ChecklistRealizadoDTO();
        dto.setId(checklist.getId());
        dto.setNumeroChecklist(checklist.getNumeroChecklist());
        dto.setDataHoraInicio(checklist.getDataHoraInicio());
        dto.setDataHoraConclusao(checklist.getDataHoraConclusao());
        dto.setKmVeiculo(checklist.getKmVeiculo());
        dto.setStatus(checklist.getStatus());
        dto.setObservacoesGerais(checklist.getObservacoesGerais());
        dto.setLocalizacao(checklist.getLocalizacao());

        if (checklist.getModeloChecklist() != null) {
            dto.setModeloChecklistId(checklist.getModeloChecklist().getId());
            dto.setModeloChecklistNome(checklist.getModeloChecklist().getNome());
        }

        if (checklist.getVeiculo() != null) {
            dto.setVeiculoId(checklist.getVeiculo().getId());
            dto.setVeiculoPlaca(checklist.getVeiculo().getPlaca());
        }

        if (checklist.getMotorista() != null) {
            dto.setMotoristaId(checklist.getMotorista().getId());
            dto.setMotoristaNome(checklist.getMotorista().getNome());
        }

        if (checklist.getViagem() != null) {
            dto.setViagemId(checklist.getViagem().getId());
        }

        if (checklist.getRespostas() != null && !checklist.getRespostas().isEmpty()) {
            List<RespostaItemChecklistDTO> respostasDTO = checklist.getRespostas().stream()
                    .map(this::toRespostaDTO)
                    .collect(Collectors.toList());
            dto.setRespostas(respostasDTO);
        }

        return dto;
    }

    private ChecklistRealizado toEntity(ChecklistRealizadoDTO dto) {
        ChecklistRealizado checklist = new ChecklistRealizado();
        checklist.setDataHoraInicio(dto.getDataHoraInicio() != null ? dto.getDataHoraInicio() : LocalDateTime.now());
        checklist.setDataHoraConclusao(dto.getDataHoraConclusao());
        checklist.setKmVeiculo(dto.getKmVeiculo());
        checklist.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusChecklist.EM_ANDAMENTO);
        checklist.setObservacoesGerais(dto.getObservacoesGerais());
        checklist.setLocalizacao(dto.getLocalizacao());
        return checklist;
    }

    private RespostaItemChecklistDTO toRespostaDTO(RespostaItemChecklist resposta) {
        RespostaItemChecklistDTO dto = new RespostaItemChecklistDTO();
        dto.setId(resposta.getId());
        dto.setChecklistRealizadoId(resposta.getChecklistRealizado().getId());
        dto.setItemModeloId(resposta.getItemModelo().getId());
        dto.setItemModeloDescricao(resposta.getItemModelo().getDescricao());
        dto.setResposta(resposta.getResposta());
        dto.setObservacao(resposta.getObservacao());
        dto.setFotoUrl(resposta.getFotoUrl());
        dto.setRequerAtencao(resposta.getRequerAtencao());
        return dto;
    }

    private RespostaItemChecklist toRespostaEntity(RespostaItemChecklistDTO dto) {
        RespostaItemChecklist resposta = new RespostaItemChecklist();
        resposta.setResposta(dto.getResposta());
        resposta.setObservacao(dto.getObservacao());
        resposta.setFotoUrl(dto.getFotoUrl());
        resposta.setRequerAtencao(dto.getRequerAtencao() != null ? dto.getRequerAtencao() : false);
        return resposta;
    }
}
