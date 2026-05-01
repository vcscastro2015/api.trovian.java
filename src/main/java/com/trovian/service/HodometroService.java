package com.trovian.service;

import com.trovian.dto.HodometroDTO;
import com.trovian.entity.Hodometro;
import com.trovian.entity.Veiculo;
import com.trovian.repository.HodometroRepository;
import com.trovian.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class HodometroService {

    private final HodometroRepository hodometroRepository;
    private final VeiculoRepository veiculoRepository;

    @Transactional(readOnly = true)
    public Page<HodometroDTO> findAll(Pageable pageable) {
        return hodometroRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public HodometroDTO findById(Integer id) {
        Hodometro hodometro = hodometroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hodômetro não encontrado com id: " + id));
        return toDTO(hodometro);
    }

    @Transactional(readOnly = true)
    public Page<HodometroDTO> findByVeiculoId(Long veiculoId, Pageable pageable) {
        return hodometroRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public List<HodometroDTO> findByVeiculoIdAndPeriodo(Long veiculoId, LocalDate dataInicial, LocalDate dataFinal) {
        Date inicio = Date.from(dataInicial.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fim = Date.from(dataFinal.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());
        return hodometroRepository
                .findByVeiculoIdAndDataCadastroBetweenOrderByDataCadastroDesc(veiculoId, inicio, fim)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public HodometroDTO create(HodometroDTO dto) {
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id: " + dto.getVeiculoId()));

        Hodometro hodometro = toEntity(dto);
        hodometro.setVeiculo(veiculo);

        return toDTO(hodometroRepository.save(hodometro));
    }

    @Transactional
    public HodometroDTO update(Integer id, HodometroDTO dto) {
        Hodometro hodometro = hodometroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hodômetro não encontrado com id: " + id));

        if (dto.getVeiculoId() != null && !dto.getVeiculoId().equals(hodometro.getVeiculo().getId())) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado com id: " + dto.getVeiculoId()));
            hodometro.setVeiculo(veiculo);
        }

        hodometro.setHodometro(dto.getHodometro());
        hodometro.setHodometroRastreador(dto.getHodometroRastreador());
        hodometro.setTipo(dto.getTipo());
        hodometro.setIdTransmissao(dto.getIdTransmissao());
        hodometro.setNomeMotorista(dto.getNomeMotorista());
        hodometro.setIdMotorista(dto.getIdMotorista());

        return toDTO(hodometroRepository.save(hodometro));
    }

    @Transactional
    public void delete(Integer id) {
        if (!hodometroRepository.existsById(id)) {
            throw new RuntimeException("Hodômetro não encontrado com id: " + id);
        }
        hodometroRepository.deleteById(id);
    }

    private HodometroDTO toDTO(Hodometro hodometro) {
        HodometroDTO dto = new HodometroDTO();
        dto.setId(hodometro.getId());
        dto.setHodometro(hodometro.getHodometro());
        dto.setHodometroRastreador(hodometro.getHodometroRastreador());
        dto.setTipo(hodometro.getTipo());
        dto.setIdTransmissao(hodometro.getIdTransmissao());
        dto.setNomeMotorista(hodometro.getNomeMotorista());
        dto.setIdMotorista(hodometro.getIdMotorista());
        dto.setDataCadastro(hodometro.getDataCadastro());
        dto.setDataAtualizacao(hodometro.getDataAtualizacao());

        if (hodometro.getVeiculo() != null) {
            dto.setVeiculoId(hodometro.getVeiculo().getId());
            dto.setVeiculoPlaca(hodometro.getVeiculo().getPlaca());
        }

        return dto;
    }

    private Hodometro toEntity(HodometroDTO dto) {
        Hodometro hodometro = new Hodometro();
        hodometro.setHodometro(dto.getHodometro());
        hodometro.setHodometroRastreador(dto.getHodometroRastreador());
        hodometro.setTipo(dto.getTipo());
        hodometro.setIdTransmissao(dto.getIdTransmissao());
        hodometro.setNomeMotorista(dto.getNomeMotorista());
        hodometro.setIdMotorista(dto.getIdMotorista());
        return hodometro;
    }
}
