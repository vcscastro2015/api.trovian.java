package com.trovian.service;

import com.trovian.dto.AgendamentoComandoVeiculoDTO;
import com.trovian.entity.AgendamentoComandoVeiculo;
import com.trovian.entity.Veiculo;
import com.trovian.repository.AgendamentoComandoVeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgendamentoComandoVeiculoService {

    private final AgendamentoComandoVeiculoRepository repository;
    private final VeiculoService veiculoService;

    public AgendamentoComandoVeiculoDTO criar(AgendamentoComandoVeiculoDTO dto) {
        AgendamentoComandoVeiculo entity = toEntity(dto);
        return toDTO(repository.save(entity));
    }

    public AgendamentoComandoVeiculoDTO editar(Long id, AgendamentoComandoVeiculoDTO dto) {
        AgendamentoComandoVeiculo existente = buscarEntidade(id);
        existente.setVeiculo(veiculoService.buscarVeiculo(dto.getVeiculoId()));
        existente.setTipoComando(dto.getTipoComando());
        existente.setTipoRecorrencia(dto.getTipoRecorrencia());
        existente.setHorario(dto.getHorario());
        existente.setDiaDomes(dto.getDiaDomes());
        existente.setDataEspecifica(dto.getDataEspecifica());
        existente.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : existente.getAtivo());
        return toDTO(repository.save(existente));
    }

    public void excluir(Long id) {
        buscarEntidade(id);
        repository.deleteById(id);
    }

    public AgendamentoComandoVeiculoDTO buscarPorId(Long id) {
        return toDTO(buscarEntidade(id));
    }

    public List<AgendamentoComandoVeiculoDTO> listarPorVeiculo(Long idVeiculo) {
        return repository.findByVeiculoIdAndAtivoTrue(idVeiculo)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    private AgendamentoComandoVeiculo buscarEntidade(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Agendamento não encontrado: " + id));
    }

    private AgendamentoComandoVeiculo toEntity(AgendamentoComandoVeiculoDTO dto) {
        Veiculo veiculo = veiculoService.buscarVeiculo(dto.getVeiculoId());
        AgendamentoComandoVeiculo entity = new AgendamentoComandoVeiculo();
        entity.setVeiculo(veiculo);
        entity.setTipoComando(dto.getTipoComando());
        entity.setTipoRecorrencia(dto.getTipoRecorrencia());
        entity.setHorario(dto.getHorario());
        entity.setDiaDomes(dto.getDiaDomes());
        entity.setDataEspecifica(dto.getDataEspecifica());
        entity.setAtivo(dto.getAtivo() != null ? dto.getAtivo() : Boolean.TRUE);
        return entity;
    }

    AgendamentoComandoVeiculoDTO toDTO(AgendamentoComandoVeiculo entity) {
        return AgendamentoComandoVeiculoDTO.builder()
                .id(entity.getId())
                .veiculoId(entity.getVeiculo().getId())
                .veiculoPlaca(entity.getVeiculo().getPlaca())
                .tipoComando(entity.getTipoComando())
                .tipoRecorrencia(entity.getTipoRecorrencia())
                .horario(entity.getHorario())
                .diaDomes(entity.getDiaDomes())
                .dataEspecifica(entity.getDataEspecifica())
                .ativo(entity.getAtivo())
                .ultimaExecucao(entity.getUltimaExecucao())
                .dataCadastro(entity.getDataCadastro())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
