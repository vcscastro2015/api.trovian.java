package com.trovian.service;

import com.trovian.dto.AbastecimentoDTO;
import com.trovian.dto.AbastecimentoWhatAppDTO;
import com.trovian.dto.DadosFilaDTO;
import com.trovian.entity.*;
import com.trovian.enums.TipoCombustivel;
import com.trovian.repository.*;
import com.trovian.util.TelefoneUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AbastecimentoService {

    private final AbastecimentoRepository abastecimentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final ClienteRepository clienteRepository;
    private final LocalRepository localRepository;
    private final RotaRepository rotaRepository;
    private final ImagemArquivoRepository arquivoRepository;

    @Autowired
    private ContaPagarService contaPagarService;

    /**
     * Lista todos os abastecimentos com paginação
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findAll(Pageable pageable) {
        log.info("Buscando todos os abastecimentos com paginação: {}", pageable);
        return abastecimentoRepository.findAll(pageable).map(this::toDTO);
    }

    /**
     * Busca abastecimento por ID
     */
    @Transactional(readOnly = true)
    public AbastecimentoDTO findById(Long id) {
        log.info("Buscando abastecimento por ID: {}", id);
        Abastecimento abastecimento = abastecimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado com ID: " + id));
        return toDTO(abastecimento);
    }

    /**
     * Busca abastecimentos por veículo
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        log.info("Buscando abastecimentos por veículo ID: {}", veiculoId);
        return abastecimentoRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    /**
     * Busca abastecimentos por motorista
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByMotorista(Long motoristaId, Pageable pageable) {
        log.info("Buscando abastecimentos por motorista ID: {}", motoristaId);
        return abastecimentoRepository.findByMotoristaId(motoristaId, pageable).map(this::toDTO);
    }

    /**
     * Busca abastecimentos por cliente
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.info("Buscando abastecimentos por cliente ID: {}", clienteId);
        return abastecimentoRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    /**
     * Busca abastecimentos por rota
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByRota(Long rotaId, Pageable pageable) {
        log.info("Buscando abastecimentos por rota ID: {}", rotaId);
        return abastecimentoRepository.findByRotaId(rotaId, pageable).map(this::toDTO);
    }

    /**
     * Busca abastecimentos por status
     */
    @Transactional(readOnly = true)
    public Page<AbastecimentoDTO> findByStatus(Boolean status, Pageable pageable) {
        log.info("Buscando abastecimentos por status: {}", status);
        return abastecimentoRepository.findByStatus(status, pageable).map(this::toDTO);
    }

    /**
     * Cria novo abastecimento
     */
    @Transactional
    public AbastecimentoDTO create(AbastecimentoDTO dto) {
        log.info("Criando novo abastecimento para veículo ID: {}", dto.getVeiculoId());

        // Validar veículo
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        // Validar motorista
        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + dto.getMotoristaId()));

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Abastecimento abastecimento = toEntity(dto, veiculo, motorista, cliente);
        Abastecimento saved = abastecimentoRepository.save(abastecimento);
        log.info("Abastecimento criado com sucesso. ID: {}", saved.getId());

        return toDTO(saved);
    }

    /**
     * Atualiza abastecimento existente
     */
    @Transactional
    public AbastecimentoDTO update(Long id, AbastecimentoDTO dto) {
        log.info("Atualizando abastecimento ID: {}", id);

        Abastecimento existing = abastecimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado com ID: " + id));

        // Validar veículo
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        // Validar motorista
        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + dto.getMotoristaId()));

        // Validar cliente
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        // Atualizar campos
        existing.setVeiculo(veiculo);
        existing.setMotorista(motorista);
        existing.setCliente(cliente);
        existing.setDataHora(dto.getDataHora());
        existing.setKmOdometro(dto.getKmOdometro());
        existing.setLitrosAbastecidos(dto.getLitrosAbastecidos());
        existing.setValorTotal(dto.getValorTotal());
        existing.setPrecoLitro(dto.getPrecoLitro());
        existing.setCombustivelTipo(dto.getCombustivelTipo());
        existing.setTanqueCheio(dto.getTanqueCheio());
        existing.setObservacoes(dto.getObservacoes());
        existing.setStatus(dto.getStatus());

        // Atualizar local (opcional)
        if (dto.getLocalId() != null) {
            Local local = localRepository.findById(dto.getLocalId())
                    .orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + dto.getLocalId()));
            existing.setLocal(local);
        } else {
            existing.setLocal(null);
        }

        // Atualizar rota (opcional)
        if (dto.getRotaId() != null) {
            Rota rota = rotaRepository.findById(dto.getRotaId())
                    .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + dto.getRotaId()));
            existing.setRota(rota);
        } else {
            existing.setRota(null);
        }

        Abastecimento updated = abastecimentoRepository.save(existing);
        log.info("Abastecimento atualizado com sucesso. ID: {}", updated.getId());

        return toDTO(updated);
    }

    /**
     * Deleta abastecimento (soft delete alterando status)
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando abastecimento ID: {}", id);
        Abastecimento abastecimento = abastecimentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado com ID: " + id));

        // Soft delete
        abastecimento.setStatus(false);
        abastecimentoRepository.save(abastecimento);
        log.info("Abastecimento deletado (soft delete) com sucesso. ID: {}", id);
    }

    /**
     * Converte Entity para DTO
     */
    private AbastecimentoDTO toDTO(Abastecimento entity) {
        AbastecimentoDTO dto = new AbastecimentoDTO();
        dto.setId(entity.getId());
        dto.setVeiculoId(entity.getVeiculo().getId());
        dto.setVeiculoPlaca(entity.getVeiculo().getPlaca());
        dto.setMotoristaId(entity.getMotorista().getId());
        dto.setMotoristaNome(entity.getMotorista().getNome());
        dto.setClienteId(entity.getCliente().getId());
        dto.setClienteNome(entity.getCliente().getNome());

        if (entity.getRota() != null) {
            dto.setRotaId(entity.getRota().getId());
            dto.setRotaDescricao(entity.getRota().getDescricao());
        }

        if (entity.getLocal() != null) {
            dto.setLocalId(entity.getLocal().getId());
            dto.setLocalNome(entity.getLocal().getNome());
        }

        dto.setDataHora(entity.getDataHora());
        dto.setKmOdometro(entity.getKmOdometro());
        dto.setLitrosAbastecidos(entity.getLitrosAbastecidos());
        dto.setValorTotal(entity.getValorTotal());
        dto.setPrecoLitro(entity.getPrecoLitro());
        dto.setCombustivelTipo(entity.getCombustivelTipo());
        dto.setTanqueCheio(entity.getTanqueCheio());
        dto.setObservacoes(entity.getObservacoes());
        dto.setCriadoEm(entity.getCriadoEm());
        dto.setAtualizadoEm(entity.getAtualizadoEm());
        dto.setStatus(entity.getStatus());
        dto.setTemImagem(entity.getTemImagem());

        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Abastecimento toEntity(AbastecimentoDTO dto, Veiculo veiculo, Motorista motorista, Cliente cliente) {
        Abastecimento entity = new Abastecimento();
        entity.setVeiculo(veiculo);
        entity.setMotorista(motorista);
        entity.setCliente(cliente);
        entity.setDataHora(dto.getDataHora());
        entity.setKmOdometro(dto.getKmOdometro());
        entity.setLitrosAbastecidos(dto.getLitrosAbastecidos());
        entity.setValorTotal(dto.getValorTotal());
        entity.setPrecoLitro(dto.getPrecoLitro());
        entity.setCombustivelTipo(dto.getCombustivelTipo());
        entity.setTanqueCheio(dto.getTanqueCheio());
        entity.setObservacoes(dto.getObservacoes());
        entity.setStatus(dto.getStatus());
        entity.setTemImagem(dto.getTemImagem());

        // Local (opcional)
        if (dto.getLocalId() != null) {
            Local local = localRepository.findById(dto.getLocalId())
                    .orElseThrow(() -> new RuntimeException("Local não encontrado com ID: " + dto.getLocalId()));
            entity.setLocal(local);
        }

        // Rota (opcional)
        if (dto.getRotaId() != null) {
            Rota rota = rotaRepository.findById(dto.getRotaId())
                    .orElseThrow(() -> new RuntimeException("Rota não encontrada com ID: " + dto.getRotaId()));
            entity.setRota(rota);
        }

        return entity;
    }

    public void processarAbastecimentoWhatsApp(DadosFilaDTO dadosFilaDTO){
        try {
            Optional<Veiculo> optionalVeiculo = veiculoRepository.findByPlacaIgnoreCase(dadosFilaDTO.getPlaca());
            if(optionalVeiculo.isPresent()) {
                Veiculo veiculo = optionalVeiculo.get();
                String telefoneConvertido =
                        TelefoneUtils.converterNumeroWpp(dadosFilaDTO.getNumeroTelefone());
                Motorista motorista = motoristaRepository.findByTelefone(TelefoneUtils.converterNumeroWpp(telefoneConvertido)).orElse(null);
                Abastecimento abastecimento = getAbastecimento(dadosFilaDTO, veiculo);
                abastecimento.setMotorista(motorista);
                Abastecimento savedAbastecimento = abastecimentoRepository.save(abastecimento);
                if(savedAbastecimento.getTemImagem()){
                    salvarImagem(veiculo.getCliente(), savedAbastecimento, dadosFilaDTO);
                }
                contaPagarService.salvarContaPagarAbastecimentoWpp(savedAbastecimento, veiculo);
            }
        } catch (Exception e) {
            log.error("processarAbastecimentoWhatsApp", e);
        }
    }

    private Abastecimento getAbastecimento(DadosFilaDTO dadosFilaDTO, Veiculo veiculo) {
        AbastecimentoWhatAppDTO abastecimentoDTO = dadosFilaDTO.getAbastecimento();
        Abastecimento abastecimento = new Abastecimento();
        abastecimento.setDataHora(new Date());
        abastecimento.setCombustivelTipo(TipoCombustivel.DIESEL);
        abastecimento.setKmOdometro(Integer.valueOf(dadosFilaDTO.getHodometro()));
        abastecimento.setLitrosAbastecidos(defaultSeNulo(abastecimentoDTO.getLitros()));
        abastecimento.setObservacoes("Abastecimento criado via informação originadas do WhatsApp.");
        abastecimento.setPrecoLitro(defaultSeNulo(abastecimentoDTO.getPrecoPorLitro()));
        abastecimento.setStatus(Boolean.TRUE);
        abastecimento.setValorTotal(defaultSeNulo(abastecimentoDTO.getTotalAPagar()));
        abastecimento.setVeiculo(veiculo);
        abastecimento.setCliente(veiculo.getCliente());
        if(Objects.nonNull(dadosFilaDTO.getBase64())){
            abastecimento.setTemImagem(Boolean.TRUE);
        }
        return abastecimento;
    }

    private BigDecimal defaultSeNulo(BigDecimal valor) {
        return Objects.requireNonNullElse(valor, BigDecimal.ZERO);
    }

    private void salvarImagem(Cliente cliente, Abastecimento abastecimento, DadosFilaDTO dadosFilaDTO){
        try {
            String base64Input = dadosFilaDTO.getBase64();
            String cleanBase64 = base64Input.contains(",") ?
                    base64Input.split(",")[1] : base64Input;
            byte[] bytes = Base64.getDecoder().decode(cleanBase64);
            ImagemArquivo imagemArquivo = new ImagemArquivo();
            imagemArquivo.setAbastecimento(abastecimento);
            imagemArquivo.setCliente(cliente);
            imagemArquivo.setConteudoBinario(bytes);
            arquivoRepository.save(imagemArquivo);
        }catch (Exception e){
            log.error("salvarImagem", e);
        }
    }


}
