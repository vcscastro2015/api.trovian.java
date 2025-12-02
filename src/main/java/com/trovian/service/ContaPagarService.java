package com.trovian.service;

import com.trovian.dto.ContaPagarDTO;
import com.trovian.entity.*;
import com.trovian.enums.StatusConta;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaPagarService {

    private final ContaPagarRepository contaPagarRepository;
    private final FornecedorRepository fornecedorRepository;
    private final CategoriaContaRepository categoriaContaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final ClienteRepository clienteRepository;

    @Transactional
    public ContaPagarDTO create(ContaPagarDTO dto) {
        log.info("Criando conta a pagar: {}", dto.getDescricao());
        ContaPagar conta = toEntity(dto);
        ContaPagar saved = contaPagarRepository.save(conta);
        log.info("Conta a pagar criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return contaPagarRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ContaPagarDTO findById(Long id) {
        ContaPagar conta = contaPagarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada com ID: " + id));
        return toDTO(conta);
    }

    @Transactional
    public ContaPagarDTO update(Long id, ContaPagarDTO dto) {
        ContaPagar conta = contaPagarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada com ID: " + id));
        updateEntityFromDTO(conta, dto);
        ContaPagar updated = contaPagarRepository.save(conta);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!contaPagarRepository.existsById(id)) {
            throw new RuntimeException("Conta a pagar não encontrada com ID: " + id);
        }
        contaPagarRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findByFornecedor(Long fornecedorId, Pageable pageable) {
        return contaPagarRepository.findByFornecedorId(fornecedorId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        return contaPagarRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findVencidas(Pageable pageable) {
        return contaPagarRepository.findVencidas(LocalDate.now(), pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findAVencer(int dias, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataFutura = hoje.plusDays(dias);
        return contaPagarRepository.findAVencer(hoje, dataFutura, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaPagarDTO> findByPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return contaPagarRepository.findByDataVencimentoBetween(dataInicio, dataFim, pageable).map(this::toDTO);
    }

    @Transactional
    public ContaPagarDTO registrarPagamento(Long id, BigDecimal valorPago, LocalDate dataPagamento, String usuario) {
        log.info("Registrando pagamento da conta ID: {} - Valor: {}", id, valorPago);
        ContaPagar conta = contaPagarRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a pagar não encontrada com ID: " + id));

        BigDecimal novoValorPago = conta.getValorPago().add(valorPago);
        conta.setValorPago(novoValorPago);
        conta.setDataPagamento(dataPagamento);
        conta.setUsuarioPagamento(usuario);

        if (novoValorPago.compareTo(conta.getValorTotal()) >= 0) {
            conta.setStatus(StatusConta.PAGO);
        } else if (novoValorPago.compareTo(BigDecimal.ZERO) > 0) {
            conta.setStatus(StatusConta.PARCIAL);
        }

        ContaPagar updated = contaPagarRepository.save(conta);
        log.info("Pagamento registrado com sucesso");
        return toDTO(updated);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPendente() {
        BigDecimal total = contaPagarRepository.sumTotalByStatus(StatusConta.PENDENTE);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getSaldoAPagar() {
        BigDecimal saldo = contaPagarRepository.sumSaldoAPagar();
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    private ContaPagarDTO toDTO(ContaPagar entity) {
        ContaPagarDTO dto = new ContaPagarDTO();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        dto.setNumeroNotaFiscal(entity.getNumeroNotaFiscal());
        dto.setNumeroControle(entity.getNumeroControle());

        if (entity.getFornecedor() != null) {
            dto.setFornecedorId(entity.getFornecedor().getId());
            dto.setFornecedorNome(entity.getFornecedor().getRazaoSocial());
        }
        if (entity.getCategoria() != null) {
            dto.setCategoriaId(entity.getCategoria().getId());
            dto.setCategoriaNome(entity.getCategoria().getNome());
        }
        if (entity.getCentroCusto() != null) {
            dto.setCentroCustoId(entity.getCentroCusto().getId());
            dto.setCentroCustoNome(entity.getCentroCusto().getNome());
        }
        if (entity.getFormaPagamento() != null) {
            dto.setFormaPagamentoId(entity.getFormaPagamento().getId());
            dto.setFormaPagamentoNome(entity.getFormaPagamento().getNome());
        }
        if (entity.getVeiculo() != null) {
            dto.setVeiculoId(entity.getVeiculo().getId());
            dto.setVeiculoPlaca(entity.getVeiculo().getPlaca());
        }
        if (entity.getMotorista() != null) {
            dto.setMotoristaId(entity.getMotorista().getId());
            dto.setMotoristaNome(entity.getMotorista().getNome());
        }
        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNome(entity.getCliente().getNome());
        }

        dto.setValorOriginal(entity.getValorOriginal());
        dto.setValorDesconto(entity.getValorDesconto());
        dto.setValorJuros(entity.getValorJuros());
        dto.setValorMulta(entity.getValorMulta());
        dto.setValorTotal(entity.getValorTotal());
        dto.setValorPago(entity.getValorPago());
        dto.setSaldo(entity.calcularSaldo());
        dto.setDataEmissao(entity.getDataEmissao());
        dto.setDataVencimento(entity.getDataVencimento());
        dto.setDataPagamento(entity.getDataPagamento());
        dto.setDataCompetencia(entity.getDataCompetencia());
        dto.setStatus(entity.getStatus());
        dto.setNumeroParcela(entity.getNumeroParcela());
        dto.setTotalParcelas(entity.getTotalParcelas());
        dto.setRecorrente(entity.getRecorrente());
        dto.setPeriodicidade(entity.getPeriodicidade());
        dto.setObservacao(entity.getObservacao());
        dto.setAnexos(entity.getAnexos());
        dto.setUsuarioCadastro(entity.getUsuarioCadastro());
        dto.setUsuarioPagamento(entity.getUsuarioPagamento());
        dto.setVencida(entity.isVencida());
        return dto;
    }

    private ContaPagar toEntity(ContaPagarDTO dto) {
        ContaPagar entity = new ContaPagar();
        entity.setDescricao(dto.getDescricao());
        entity.setNumeroDocumento(dto.getNumeroDocumento());
        entity.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        entity.setNumeroControle(dto.getNumeroControle());

        entity.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId())
            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado")));
        entity.setCategoria(categoriaContaRepository.findById(dto.getCategoriaId())
            .orElseThrow(() -> new RuntimeException("Categoria não encontrada")));

        if (dto.getCentroCustoId() != null) {
            entity.setCentroCusto(centroCustoRepository.findById(dto.getCentroCustoId()).orElse(null));
        }
        if (dto.getFormaPagamentoId() != null) {
            entity.setFormaPagamento(formaPagamentoRepository.findById(dto.getFormaPagamentoId()).orElse(null));
        }
        if (dto.getVeiculoId() != null) {
            entity.setVeiculo(veiculoRepository.findById(dto.getVeiculoId()).orElse(null));
        }
        if (dto.getMotoristaId() != null) {
            entity.setMotorista(motoristaRepository.findById(dto.getMotoristaId()).orElse(null));
        }
        if (dto.getClienteId() != null) {
            entity.setCliente(clienteRepository.findById(dto.getClienteId()).orElse(null));
        }

        entity.setValorOriginal(dto.getValorOriginal());
        entity.setValorDesconto(dto.getValorDesconto() != null ? dto.getValorDesconto() : BigDecimal.ZERO);
        entity.setValorJuros(dto.getValorJuros() != null ? dto.getValorJuros() : BigDecimal.ZERO);
        entity.setValorMulta(dto.getValorMulta() != null ? dto.getValorMulta() : BigDecimal.ZERO);
        entity.setValorTotal(dto.getValorTotal());
        entity.setValorPago(dto.getValorPago() != null ? dto.getValorPago() : BigDecimal.ZERO);
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setDataPagamento(dto.getDataPagamento());
        entity.setDataCompetencia(dto.getDataCompetencia());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusConta.PENDENTE);
        entity.setNumeroParcela(dto.getNumeroParcela());
        entity.setTotalParcelas(dto.getTotalParcelas());
        entity.setRecorrente(dto.getRecorrente() != null ? dto.getRecorrente() : false);
        entity.setPeriodicidade(dto.getPeriodicidade());
        entity.setObservacao(dto.getObservacao());
        entity.setAnexos(dto.getAnexos());
        entity.setUsuarioCadastro(dto.getUsuarioCadastro());
        return entity;
    }

    private void updateEntityFromDTO(ContaPagar entity, ContaPagarDTO dto) {
        entity.setDescricao(dto.getDescricao());
        entity.setNumeroDocumento(dto.getNumeroDocumento());
        entity.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        entity.setNumeroControle(dto.getNumeroControle());
        entity.setValorOriginal(dto.getValorOriginal());
        entity.setValorDesconto(dto.getValorDesconto());
        entity.setValorJuros(dto.getValorJuros());
        entity.setValorMulta(dto.getValorMulta());
        entity.setValorTotal(dto.getValorTotal());
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setDataCompetencia(dto.getDataCompetencia());
        entity.setStatus(dto.getStatus());
        entity.setObservacao(dto.getObservacao());
    }
}
