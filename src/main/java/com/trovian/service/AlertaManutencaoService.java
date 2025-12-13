package com.trovian.service;

import com.trovian.dto.AlertaManutencaoDTO;
import com.trovian.entity.*;
import com.trovian.enums.PrioridadeAlerta;
import com.trovian.enums.StatusOrdemServico;
import com.trovian.enums.TipoAlerta;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertaManutencaoService {

    private final AlertaManutencaoRepository alertaManutencaoRepository;
    private final OrdemServicoRepository ordemServicoRepository;
    private final VeiculoRepository veiculoRepository;
    private final PecaRepository pecaRepository;

    private static final int KM_REVISAO_PREVENTIVA = 10000;
    private static final int KM_TROCA_PNEUS = 80000;

    @Transactional
    public AlertaManutencaoDTO create(AlertaManutencaoDTO dto) {
        log.info("Criando alerta de manutenção: {}", dto.getTitulo());
        AlertaManutencao alerta = toEntity(dto);
        AlertaManutencao saved = alertaManutencaoRepository.save(alerta);
        log.info("Alerta criado com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<AlertaManutencaoDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando alertas - página: {}, tamanho: {}", page, size);
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return alertaManutencaoRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public AlertaManutencaoDTO findById(Long id) {
        log.info("Buscando alerta por ID: {}", id);
        AlertaManutencao alerta = alertaManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + id));
        return toDTO(alerta);
    }

    @Transactional(readOnly = true)
    public List<AlertaManutencaoDTO> findAlertasNaoLidosNaoResolvidos() {
        return alertaManutencaoRepository.findAlertasNaoLidosNaoResolvidos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertaManutencaoDTO> findAlertasNaoResolvidosPorVeiculo(Long veiculoId) {
        return alertaManutencaoRepository.findAlertasNaoResolvidosPorVeiculo(veiculoId)
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AlertaManutencaoDTO> findAlertasCriticosNaoResolvidos() {
        return alertaManutencaoRepository.findAlertasCriticosNaoResolvidos()
            .stream()
            .map(this::toDTO)
            .collect(Collectors.toList());
    }

    @Transactional
    public AlertaManutencaoDTO marcarComoLido(Long id) {
        log.info("Marcando alerta como lido. ID: {}", id);
        AlertaManutencao alerta = alertaManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + id));
        alerta.marcarComoLido();
        AlertaManutencao updated = alertaManutencaoRepository.save(alerta);
        return toDTO(updated);
    }

    @Transactional
    public AlertaManutencaoDTO marcarComoResolvido(Long id, String observacao) {
        log.info("Marcando alerta como resolvido. ID: {}", id);
        AlertaManutencao alerta = alertaManutencaoRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Alerta não encontrado com ID: " + id));
        alerta.marcarComoResolvido(observacao);
        AlertaManutencao updated = alertaManutencaoRepository.save(alerta);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando alerta ID: {}", id);
        if (!alertaManutencaoRepository.existsById(id)) {
            throw new RuntimeException("Alerta não encontrado com ID: " + id);
        }
        alertaManutencaoRepository.deleteById(id);
        log.info("Alerta deletado com sucesso. ID: {}", id);
    }

    // ==================== REGRAS DE NEGÓCIO ====================

    @Transactional
    public void verificarManutencaoPreventivaAbastecimento(Long veiculoId, Integer kmAtual) {
        log.info("Verificando necessidade de manutenção preventiva ao abastecer veículo ID: {}, KM: {}", veiculoId, kmAtual);

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + veiculoId));

        // Buscar última manutenção preventiva do veículo
        List<OrdemServico> manutencoes = ordemServicoRepository
            .findByVeiculoIdAndStatusIn(veiculoId, Arrays.asList(StatusOrdemServico.CONCLUIDA));

        if (!manutencoes.isEmpty()) {
            OrdemServico ultimaManutencao = manutencoes.stream()
                .filter(os -> os.getKmVeiculo() != null)
                .max((os1, os2) -> os1.getKmVeiculo().compareTo(os2.getKmVeiculo()))
                .orElse(null);

            if (ultimaManutencao != null) {
                int kmDesdeUltimaRevisao = kmAtual - ultimaManutencao.getKmVeiculo();

                if (kmDesdeUltimaRevisao >= KM_REVISAO_PREVENTIVA) {
                    gerarAlertaRevisaoPreventiva(veiculo, kmAtual, kmDesdeUltimaRevisao);
                }
            }
        } else {
            // Sem histórico de manutenção
            gerarAlertaRevisaoPreventiva(veiculo, kmAtual, kmAtual);
        }
    }

    @Transactional
    public void verificarTrocaPneus(Long veiculoId, Integer kmAtual) {
        log.info("Verificando necessidade de troca de pneus para veículo ID: {}, KM: {}", veiculoId, kmAtual);

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + veiculoId));

        // Aqui você pode adicionar lógica para rastrear km dos pneus
        // Por simplicidade, vamos assumir que precisa trocar a cada 80000 km
        if (kmAtual >= KM_TROCA_PNEUS && (kmAtual % KM_TROCA_PNEUS) < 1000) {
            gerarAlertaTrocaPneus(veiculo, kmAtual);
        }
    }

    @Transactional
    public void verificarPecasEstoqueBaixo() {
        log.info("Verificando peças com estoque baixo");

        List<Peca> pecasEstoqueBaixo = pecaRepository.findPecasEstoqueBaixo();

        for (Peca peca : pecasEstoqueBaixo) {
            gerarAlertaEstoqueBaixo(peca);
        }
    }

    @Transactional
    public boolean validarVeiculoParaViagem(Long veiculoId) {
        log.info("Validando se veículo ID: {} pode realizar viagem", veiculoId);

        Veiculo veiculo = veiculoRepository.findById(veiculoId)
            .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + veiculoId));

        // Buscar ordens de serviço pendentes ou em andamento
        List<OrdemServico> ordemsPendentes = ordemServicoRepository
            .findByVeiculoIdAndStatusIn(veiculoId, Arrays.asList(
                StatusOrdemServico.ABERTA,
                StatusOrdemServico.EM_ANDAMENTO,
                StatusOrdemServico.AGUARDANDO_PECAS
            ));

        if (!ordemsPendentes.isEmpty()) {
            // Verificar se há alguma manutenção crítica
            boolean temManutencaoCritica = ordemsPendentes.stream()
                .anyMatch(os -> os.getStatus() == StatusOrdemServico.ABERTA ||
                               os.getStatus() == StatusOrdemServico.EM_ANDAMENTO);

            if (temManutencaoCritica) {
                gerarAlertaManutencaoCritica(veiculo, ordemsPendentes.get(0));
                return false;
            }

            gerarAlertaManutencaoPendente(veiculo, ordemsPendentes.size());
        }

        return true;
    }

    // ==================== MÉTODOS AUXILIARES PARA GERAR ALERTAS ====================

    private void gerarAlertaRevisaoPreventiva(Veiculo veiculo, Integer kmAtual, Integer kmDesdeUltimaRevisao) {
        AlertaManutencao alerta = new AlertaManutencao();
        alerta.setTipoAlerta(TipoAlerta.REVISAO_PREVENTIVA);
        alerta.setPrioridade(kmDesdeUltimaRevisao >= KM_REVISAO_PREVENTIVA + 5000
            ? PrioridadeAlerta.ALTA
            : PrioridadeAlerta.MEDIA);
        alerta.setVeiculo(veiculo);
        alerta.setTitulo("Revisão Preventiva Necessária");
        alerta.setMensagem(String.format(
            "O veículo %s rodou %d km desde a última revisão. Recomenda-se realizar manutenção preventiva.",
            veiculo.getPlaca(), kmDesdeUltimaRevisao
        ));
        alerta.setKmVeiculo(kmAtual);
        alertaManutencaoRepository.save(alerta);
        log.info("Alerta de revisão preventiva criado para veículo {}", veiculo.getPlaca());
    }

    private void gerarAlertaTrocaPneus(Veiculo veiculo, Integer kmAtual) {
        AlertaManutencao alerta = new AlertaManutencao();
        alerta.setTipoAlerta(TipoAlerta.TROCA_PNEUS);
        alerta.setPrioridade(PrioridadeAlerta.ALTA);
        alerta.setVeiculo(veiculo);
        alerta.setTitulo("Trocar Pneus");
        alerta.setMensagem(String.format(
            "O veículo %s atingiu %d km. Recomenda-se realizar a troca dos pneus.",
            veiculo.getPlaca(), kmAtual
        ));
        alerta.setKmVeiculo(kmAtual);
        alertaManutencaoRepository.save(alerta);
        log.info("Alerta de troca de pneus criado para veículo {}", veiculo.getPlaca());
    }

    private void gerarAlertaEstoqueBaixo(Peca peca) {
        // Verificar se já existe alerta não resolvido para esta peça
        List<AlertaManutencao> alertasExistentes = alertaManutencaoRepository.findAll().stream()
            .filter(a -> a.getPeca() != null && a.getPeca().getId().equals(peca.getId())
                     && !a.getResolvido() && a.getTipoAlerta() == TipoAlerta.ESTOQUE_BAIXO)
            .collect(Collectors.toList());

        if (alertasExistentes.isEmpty()) {
            AlertaManutencao alerta = new AlertaManutencao();
            alerta.setTipoAlerta(TipoAlerta.ESTOQUE_BAIXO);
            alerta.setPrioridade(peca.getEstoqueAtual() == 0 ? PrioridadeAlerta.CRITICA : PrioridadeAlerta.ALTA);
            alerta.setVeiculo(null); // Não vinculado a veículo específico
            alerta.setPeca(peca);
            alerta.setTitulo("Estoque Baixo");
            alerta.setMensagem(String.format(
                "Peça %s (%s) está com estoque baixo. Estoque atual: %d, Estoque mínimo: %d",
                peca.getCodigo(), peca.getDescricao(), peca.getEstoqueAtual(), peca.getEstoqueMinimo()
            ));
            alertaManutencaoRepository.save(alerta);
            log.info("Alerta de estoque baixo criado para peça {}", peca.getCodigo());
        }
    }

    private void gerarAlertaManutencaoCritica(Veiculo veiculo, OrdemServico ordemServico) {
        AlertaManutencao alerta = new AlertaManutencao();
        alerta.setTipoAlerta(TipoAlerta.MANUTENCAO_CRITICA);
        alerta.setPrioridade(PrioridadeAlerta.CRITICA);
        alerta.setVeiculo(veiculo);
        alerta.setOrdemServico(ordemServico);
        alerta.setTitulo("Manutenção Crítica - Bloqueio de Viagem");
        alerta.setMensagem(String.format(
            "O veículo %s possui ordem de serviço crítica (OS: %s) em andamento. Viagem bloqueada até conclusão da manutenção.",
            veiculo.getPlaca(), ordemServico.getNumeroOs()
        ));
        alertaManutencaoRepository.save(alerta);
        log.warn("Alerta de manutenção crítica criado para veículo {}", veiculo.getPlaca());
    }

    private void gerarAlertaManutencaoPendente(Veiculo veiculo, int quantidadeOrdens) {
        AlertaManutencao alerta = new AlertaManutencao();
        alerta.setTipoAlerta(TipoAlerta.MANUTENCAO_PENDENTE);
        alerta.setPrioridade(PrioridadeAlerta.MEDIA);
        alerta.setVeiculo(veiculo);
        alerta.setTitulo("Manutenções Pendentes");
        alerta.setMensagem(String.format(
            "O veículo %s possui %d ordem(ns) de serviço pendente(s). Verifique antes de iniciar nova viagem.",
            veiculo.getPlaca(), quantidadeOrdens
        ));
        alertaManutencaoRepository.save(alerta);
        log.info("Alerta de manutenção pendente criado para veículo {}", veiculo.getPlaca());
    }

    // ==================== CONVERSÃO DTO <-> ENTITY ====================

    private AlertaManutencaoDTO toDTO(AlertaManutencao entity) {
        AlertaManutencaoDTO dto = new AlertaManutencaoDTO();
        dto.setId(entity.getId());
        dto.setTipoAlerta(entity.getTipoAlerta());
        dto.setPrioridade(entity.getPrioridade());
        dto.setTitulo(entity.getTitulo());
        dto.setMensagem(entity.getMensagem());
        dto.setKmVeiculo(entity.getKmVeiculo());
        dto.setDataGeracao(entity.getDataGeracao());
        dto.setDataLeitura(entity.getDataLeitura());
        dto.setLido(entity.getLido());
        dto.setResolvido(entity.getResolvido());
        dto.setDataResolucao(entity.getDataResolucao());
        dto.setObservacaoResolucao(entity.getObservacaoResolucao());

        if (entity.getVeiculo() != null) {
            dto.setVeiculoId(entity.getVeiculo().getId());
            dto.setVeiculoPlaca(entity.getVeiculo().getPlaca());
        }

        if (entity.getOrdemServico() != null) {
            dto.setOrdemServicoId(entity.getOrdemServico().getId());
            dto.setOrdemServicoNumero(entity.getOrdemServico().getNumeroOs());
        }

        if (entity.getPeca() != null) {
            dto.setPecaId(entity.getPeca().getId());
            dto.setPecaCodigo(entity.getPeca().getCodigo());
        }

        return dto;
    }

    private AlertaManutencao toEntity(AlertaManutencaoDTO dto) {
        AlertaManutencao entity = new AlertaManutencao();
        entity.setTipoAlerta(dto.getTipoAlerta());
        entity.setPrioridade(dto.getPrioridade());
        entity.setTitulo(dto.getTitulo());
        entity.setMensagem(dto.getMensagem());
        entity.setKmVeiculo(dto.getKmVeiculo());

        if (dto.getVeiculoId() != null) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));
            entity.setVeiculo(veiculo);
        }

        if (dto.getOrdemServicoId() != null) {
            OrdemServico os = ordemServicoRepository.findById(dto.getOrdemServicoId())
                .orElse(null);
            entity.setOrdemServico(os);
        }

        if (dto.getPecaId() != null) {
            Peca peca = pecaRepository.findById(dto.getPecaId())
                .orElse(null);
            entity.setPeca(peca);
        }

        return entity;
    }
}
