package com.trovian.service;

import com.trovian.dto.VeiculoDTO;
import com.trovian.entity.Cliente;
import com.trovian.entity.Equipamento;
import com.trovian.entity.Modelo;
import com.trovian.entity.Veiculo;
import com.trovian.repository.ClienteRepository;
import com.trovian.repository.EquipamentoRepository;
import com.trovian.repository.ModeloRepository;
import com.trovian.repository.VeiculoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class VeiculoService {

    private final VeiculoRepository veiculoRepository;
    private final ModeloRepository modeloRepository;
    private final ClienteRepository clienteRepository;
    private final EquipamentoRepository equipamentoRepository;

    private static final List<String> TIPOS_VEICULO_VALIDOS = Arrays.asList(
            "Moto", "Carro", "Onibus", "Caminhao", "Carreta", "Implemento"
    );

    private static final List<String> COMBUSTIVEIS_VALIDOS = Arrays.asList(
            "Gasolina", "Alcool", "Diesel"
    );

    @Transactional(readOnly = true)
    public Page<VeiculoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os veículos com paginação - Página: {}, Tamanho: {}",
                pageable.getPageNumber(), pageable.getPageSize());
        Page<Veiculo> veiculos = veiculoRepository.findAll(pageable);
        return veiculos.map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public VeiculoDTO findById(Long id) {
        log.info("Buscando veículo com ID: {}", id);
        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + id));
        return toDTO(veiculo);
    }

    @Transactional(readOnly = true)
    public Page<VeiculoDTO> findByClienteId(Long clienteId, Pageable pageable) {
        log.info("Buscando veículos do cliente com ID: {} - Página: {}, Tamanho: {}",
                clienteId, pageable.getPageNumber(), pageable.getPageSize());
        Page<Veiculo> veiculos = veiculoRepository.findByClienteId(clienteId, pageable);
        return veiculos.map(this::toDTO);
    }

    @Transactional
    public VeiculoDTO create(VeiculoDTO veiculoDTO) {
        log.info("Criando novo veículo com placa: {}", veiculoDTO.getPlaca());

        validateTipoVeiculo(veiculoDTO.getTipo());
        validateCombustivel(veiculoDTO.getCombustivel());

        Modelo modelo = modeloRepository.findById(veiculoDTO.getModeloId())
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + veiculoDTO.getModeloId()));

        Cliente cliente = clienteRepository.findById(veiculoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + veiculoDTO.getClienteId()));

        Veiculo veiculo = toEntity(veiculoDTO);
        veiculo.setModelo(modelo);
        veiculo.setCliente(cliente);

        // Equipamento é opcional
        if (veiculoDTO.getEquipamentoId() != null) {
            Equipamento equipamento = equipamentoRepository.findById(veiculoDTO.getEquipamentoId())
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com ID: " + veiculoDTO.getEquipamentoId()));
            veiculo.setEquipamento(equipamento);
        }

        Veiculo savedVeiculo = veiculoRepository.save(veiculo);
        log.info("Veículo criado com sucesso. ID: {}", savedVeiculo.getId());
        return toDTO(savedVeiculo);
    }

    @Transactional
    public VeiculoDTO update(Long id, VeiculoDTO veiculoDTO) {
        log.info("Atualizando veículo com ID: {}", id);

        Veiculo veiculo = veiculoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + id));

        validateTipoVeiculo(veiculoDTO.getTipo());
        validateCombustivel(veiculoDTO.getCombustivel());

        Modelo modelo = modeloRepository.findById(veiculoDTO.getModeloId())
                .orElseThrow(() -> new RuntimeException("Modelo não encontrado com ID: " + veiculoDTO.getModeloId()));

        Cliente cliente = clienteRepository.findById(veiculoDTO.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + veiculoDTO.getClienteId()));

        // Atualiza os campos
        veiculo.setAnoFabricacao(veiculoDTO.getAnoFabricacao());
        veiculo.setAnoModelo(veiculoDTO.getAnoModelo());
        veiculo.setChassi(veiculoDTO.getChassi());
        veiculo.setCor(veiculoDTO.getCor());
        veiculo.setObservacao(veiculoDTO.getObservacao());
        veiculo.setPlaca(veiculoDTO.getPlaca());
        veiculo.setVelocidadeMaxima(veiculoDTO.getVelocidadeMaxima());
        veiculo.setVelocidadeMaximaChuva(veiculoDTO.getVelocidadeMaximaChuva());
        veiculo.setVelocidadeMaximaDesaceleracao(veiculoDTO.getVelocidadeMaximaDesaceleracao());
        veiculo.setVelocidadeMaximaCurva(veiculoDTO.getVelocidadeMaximaCurva());
        veiculo.setRenavam(veiculoDTO.getRenavam());
        veiculo.setStatus(veiculoDTO.getStatus());
        veiculo.setTipo(veiculoDTO.getTipo());
        veiculo.setCapacidadeMaximaTracao(veiculoDTO.getCapacidadeMaximaTracao());
        veiculo.setUsaEntradaDigitalUm(veiculoDTO.getUsaEntradaDigitalUm());
        veiculo.setUsaEntradaDigitalDois(veiculoDTO.getUsaEntradaDigitalDois());
        veiculo.setUsaEntradaDigitalTres(veiculoDTO.getUsaEntradaDigitalTres());
        veiculo.setUsaEntradaDigitalQuatro(veiculoDTO.getUsaEntradaDigitalQuatro());
        veiculo.setExcessoVelocidade(veiculoDTO.getExcessoVelocidade());
        veiculo.setBateriaCarroBaixa(veiculoDTO.getBateriaCarroBaixa());
        veiculo.setFaltaEnergiaPrincipal(veiculoDTO.getFaltaEnergiaPrincipal());
        veiculo.setQuantidadeDiasSemTrasmissao(veiculoDTO.getQuantidadeDiasSemTrasmissao());
        veiculo.setSemComunicacao(veiculoDTO.getSemComunicacao());
        veiculo.setAtivaRotaNoMapa(veiculoDTO.getAtivaRotaNoMapa());
        veiculo.setAtivaValidacaoDeCerca(veiculoDTO.getAtivaValidacaoDeCerca());
        veiculo.setTrocaDeHorimetro(veiculoDTO.getTrocaDeHorimetro());
        veiculo.setRpmModoEconomicoMinimo(veiculoDTO.getRpmModoEconomicoMinimo());
        veiculo.setRpmModoEconomicoMaximo(veiculoDTO.getRpmModoEconomicoMaximo());
        veiculo.setRpmMaximo(veiculoDTO.getRpmMaximo());
        veiculo.setRpmInicioFaixaAzul(veiculoDTO.getRpmInicioFaixaAzul());
        veiculo.setRpmFimFaixaAzul(veiculoDTO.getRpmFimFaixaAzul());
        veiculo.setRpmInicioFaixaEconomica(veiculoDTO.getRpmInicioFaixaEconomica());
        veiculo.setRpmFimFaixaEconomica(veiculoDTO.getRpmFimFaixaEconomica());
        veiculo.setRpmInicioFaixaVerde(veiculoDTO.getRpmInicioFaixaVerde());
        veiculo.setRpmFimFaixaVerde(veiculoDTO.getRpmFimFaixaVerde());
        veiculo.setRpmInicioFaixaAmarela(veiculoDTO.getRpmInicioFaixaAmarela());
        veiculo.setRpmFimFaixaAmarela(veiculoDTO.getRpmFimFaixaAmarela());
        veiculo.setRpmInicioMarchaLenta(veiculoDTO.getRpmInicioMarchaLenta());
        veiculo.setRpmFimMarchaLenta(veiculoDTO.getRpmFimMarchaLenta());
        veiculo.setGeraEnderecoAutomatico(veiculoDTO.getGeraEnderecoAutomatico());
        veiculo.setValidarIbutton(veiculoDTO.getValidarIbutton());
        veiculo.setCargaMaxima(veiculoDTO.getCargaMaxima());
        veiculo.setCapacidadeTanque(veiculoDTO.getCapacidadeTanque());
        veiculo.setNumeroEixos(veiculoDTO.getNumeroEixos());
        veiculo.setTara(veiculoDTO.getTara());
        veiculo.setCombustivel(veiculoDTO.getCombustivel());
        veiculo.setValidarRota(veiculoDTO.getValidarRota());
        veiculo.setModelo(modelo);
        veiculo.setCliente(cliente);

        // Equipamento é opcional
        if (veiculoDTO.getEquipamentoId() != null) {
            Equipamento equipamento = equipamentoRepository.findById(veiculoDTO.getEquipamentoId())
                    .orElseThrow(() -> new RuntimeException("Equipamento não encontrado com ID: " + veiculoDTO.getEquipamentoId()));
            veiculo.setEquipamento(equipamento);
        } else {
            veiculo.setEquipamento(null);
        }

        Veiculo updatedVeiculo = veiculoRepository.save(veiculo);
        log.info("Veículo atualizado com sucesso. ID: {}", id);
        return toDTO(updatedVeiculo);
    }

    @Transactional
    public void delete(Long id) {
        log.info("Deletando veículo com ID: {}", id);

        if (!veiculoRepository.existsById(id)) {
            throw new RuntimeException("Veículo não encontrado com ID: " + id);
        }

        veiculoRepository.deleteById(id);
        log.info("Veículo deletado com sucesso. ID: {}", id);
    }

    private void validateTipoVeiculo(String tipo) {
        if (tipo != null && !tipo.trim().isEmpty()) {
            boolean isValid = TIPOS_VEICULO_VALIDOS.stream()
                    .anyMatch(t -> t.equalsIgnoreCase(tipo));
            if (!isValid) {
                throw new RuntimeException("Tipo de veículo inválido. Valores aceitos: " +
                        String.join(", ", TIPOS_VEICULO_VALIDOS));
            }
        }
    }

    private void validateCombustivel(String combustivel) {
        if (combustivel != null && !combustivel.trim().isEmpty()) {
            boolean isValid = COMBUSTIVEIS_VALIDOS.stream()
                    .anyMatch(c -> c.equalsIgnoreCase(combustivel));
            if (!isValid) {
                throw new RuntimeException("Tipo de combustível inválido. Valores aceitos: " +
                        String.join(", ", COMBUSTIVEIS_VALIDOS));
            }
        }
    }

    private VeiculoDTO toDTO(Veiculo veiculo) {
        VeiculoDTO dto = new VeiculoDTO();
        dto.setId(veiculo.getId());
        dto.setAnoFabricacao(veiculo.getAnoFabricacao());
        dto.setAnoModelo(veiculo.getAnoModelo());
        dto.setChassi(veiculo.getChassi());
        dto.setCor(veiculo.getCor());
        dto.setDataCadastro(veiculo.getDataCadastro());
        dto.setObservacao(veiculo.getObservacao());
        dto.setPlaca(veiculo.getPlaca());
        dto.setVelocidadeMaxima(veiculo.getVelocidadeMaxima());
        dto.setVelocidadeMaximaChuva(veiculo.getVelocidadeMaximaChuva());
        dto.setVelocidadeMaximaDesaceleracao(veiculo.getVelocidadeMaximaDesaceleracao());
        dto.setVelocidadeMaximaCurva(veiculo.getVelocidadeMaximaCurva());
        dto.setRenavam(veiculo.getRenavam());
        dto.setStatus(veiculo.getStatus());
        dto.setTipo(veiculo.getTipo());
        dto.setCapacidadeMaximaTracao(veiculo.getCapacidadeMaximaTracao());
        dto.setUsaEntradaDigitalUm(veiculo.getUsaEntradaDigitalUm());
        dto.setUsaEntradaDigitalDois(veiculo.getUsaEntradaDigitalDois());
        dto.setUsaEntradaDigitalTres(veiculo.getUsaEntradaDigitalTres());
        dto.setUsaEntradaDigitalQuatro(veiculo.getUsaEntradaDigitalQuatro());
        dto.setExcessoVelocidade(veiculo.getExcessoVelocidade());
        dto.setBateriaCarroBaixa(veiculo.getBateriaCarroBaixa());
        dto.setFaltaEnergiaPrincipal(veiculo.getFaltaEnergiaPrincipal());
        dto.setQuantidadeDiasSemTrasmissao(veiculo.getQuantidadeDiasSemTrasmissao());
        dto.setSemComunicacao(veiculo.getSemComunicacao());
        dto.setAtivaRotaNoMapa(veiculo.getAtivaRotaNoMapa());
        dto.setAtivaValidacaoDeCerca(veiculo.getAtivaValidacaoDeCerca());
        dto.setTrocaDeHorimetro(veiculo.getTrocaDeHorimetro());
        dto.setRpmModoEconomicoMinimo(veiculo.getRpmModoEconomicoMinimo());
        dto.setRpmModoEconomicoMaximo(veiculo.getRpmModoEconomicoMaximo());
        dto.setRpmMaximo(veiculo.getRpmMaximo());
        dto.setRpmInicioFaixaAzul(veiculo.getRpmInicioFaixaAzul());
        dto.setRpmFimFaixaAzul(veiculo.getRpmFimFaixaAzul());
        dto.setRpmInicioFaixaEconomica(veiculo.getRpmInicioFaixaEconomica());
        dto.setRpmFimFaixaEconomica(veiculo.getRpmFimFaixaEconomica());
        dto.setRpmInicioFaixaVerde(veiculo.getRpmInicioFaixaVerde());
        dto.setRpmFimFaixaVerde(veiculo.getRpmFimFaixaVerde());
        dto.setRpmInicioFaixaAmarela(veiculo.getRpmInicioFaixaAmarela());
        dto.setRpmFimFaixaAmarela(veiculo.getRpmFimFaixaAmarela());
        dto.setRpmInicioMarchaLenta(veiculo.getRpmInicioMarchaLenta());
        dto.setRpmFimMarchaLenta(veiculo.getRpmFimMarchaLenta());
        dto.setGeraEnderecoAutomatico(veiculo.getGeraEnderecoAutomatico());
        dto.setValidarIbutton(veiculo.getValidarIbutton());
        dto.setCargaMaxima(veiculo.getCargaMaxima());
        dto.setCapacidadeTanque(veiculo.getCapacidadeTanque());
        dto.setNumeroEixos(veiculo.getNumeroEixos());
        dto.setTara(veiculo.getTara());
        dto.setCombustivel(veiculo.getCombustivel());
        dto.setValidarRota(veiculo.getValidarRota());
        dto.setUpdatedAt(veiculo.getUpdatedAt());

        // Inclui informações do modelo
        if (veiculo.getModelo() != null) {
            dto.setModeloId(veiculo.getModelo().getId());
            dto.setModeloMarca(veiculo.getModelo().getMarca());
            dto.setModeloFabricante(veiculo.getModelo().getFabricante());
        }

        // Inclui informações do equipamento (se houver)
        if (veiculo.getEquipamento() != null) {
            dto.setEquipamentoId(veiculo.getEquipamento().getId());
            dto.setEquipamentoImei(veiculo.getEquipamento().getImei());
        }

        // Inclui informações do cliente
        if (veiculo.getCliente() != null) {
            dto.setClienteId(veiculo.getCliente().getId());
            dto.setClienteNome(veiculo.getCliente().getNome());
        }

        return dto;
    }

    private Veiculo toEntity(VeiculoDTO dto) {
        Veiculo veiculo = new Veiculo();
        veiculo.setAnoFabricacao(dto.getAnoFabricacao());
        veiculo.setAnoModelo(dto.getAnoModelo());
        veiculo.setChassi(dto.getChassi());
        veiculo.setCor(dto.getCor());
        veiculo.setObservacao(dto.getObservacao());
        veiculo.setPlaca(dto.getPlaca());
        veiculo.setVelocidadeMaxima(dto.getVelocidadeMaxima());
        veiculo.setVelocidadeMaximaChuva(dto.getVelocidadeMaximaChuva());
        veiculo.setVelocidadeMaximaDesaceleracao(dto.getVelocidadeMaximaDesaceleracao());
        veiculo.setVelocidadeMaximaCurva(dto.getVelocidadeMaximaCurva());
        veiculo.setRenavam(dto.getRenavam());
        veiculo.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        veiculo.setTipo(dto.getTipo());
        veiculo.setCapacidadeMaximaTracao(dto.getCapacidadeMaximaTracao());
        veiculo.setUsaEntradaDigitalUm(dto.getUsaEntradaDigitalUm());
        veiculo.setUsaEntradaDigitalDois(dto.getUsaEntradaDigitalDois());
        veiculo.setUsaEntradaDigitalTres(dto.getUsaEntradaDigitalTres());
        veiculo.setUsaEntradaDigitalQuatro(dto.getUsaEntradaDigitalQuatro());
        veiculo.setExcessoVelocidade(dto.getExcessoVelocidade());
        veiculo.setBateriaCarroBaixa(dto.getBateriaCarroBaixa());
        veiculo.setFaltaEnergiaPrincipal(dto.getFaltaEnergiaPrincipal());
        veiculo.setQuantidadeDiasSemTrasmissao(dto.getQuantidadeDiasSemTrasmissao());
        veiculo.setSemComunicacao(dto.getSemComunicacao());
        veiculo.setAtivaRotaNoMapa(dto.getAtivaRotaNoMapa());
        veiculo.setAtivaValidacaoDeCerca(dto.getAtivaValidacaoDeCerca());
        veiculo.setTrocaDeHorimetro(dto.getTrocaDeHorimetro());
        veiculo.setRpmModoEconomicoMinimo(dto.getRpmModoEconomicoMinimo());
        veiculo.setRpmModoEconomicoMaximo(dto.getRpmModoEconomicoMaximo());
        veiculo.setRpmMaximo(dto.getRpmMaximo());
        veiculo.setRpmInicioFaixaAzul(dto.getRpmInicioFaixaAzul());
        veiculo.setRpmFimFaixaAzul(dto.getRpmFimFaixaAzul());
        veiculo.setRpmInicioFaixaEconomica(dto.getRpmInicioFaixaEconomica());
        veiculo.setRpmFimFaixaEconomica(dto.getRpmFimFaixaEconomica());
        veiculo.setRpmInicioFaixaVerde(dto.getRpmInicioFaixaVerde());
        veiculo.setRpmFimFaixaVerde(dto.getRpmFimFaixaVerde());
        veiculo.setRpmInicioFaixaAmarela(dto.getRpmInicioFaixaAmarela());
        veiculo.setRpmFimFaixaAmarela(dto.getRpmFimFaixaAmarela());
        veiculo.setRpmInicioMarchaLenta(dto.getRpmInicioMarchaLenta());
        veiculo.setRpmFimMarchaLenta(dto.getRpmFimMarchaLenta());
        veiculo.setGeraEnderecoAutomatico(dto.getGeraEnderecoAutomatico());
        veiculo.setValidarIbutton(dto.getValidarIbutton());
        veiculo.setCargaMaxima(dto.getCargaMaxima());
        veiculo.setCapacidadeTanque(dto.getCapacidadeTanque());
        veiculo.setNumeroEixos(dto.getNumeroEixos());
        veiculo.setTara(dto.getTara());
        veiculo.setCombustivel(dto.getCombustivel());
        veiculo.setValidarRota(dto.getValidarRota());
        return veiculo;
    }
}
