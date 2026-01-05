package com.trovian.service;

import com.trovian.dto.ContaReceberDTO;
import com.trovian.dto.DocumentoCteDTO;
import com.trovian.dto.EmpresaDTO;
import com.trovian.dto.ValoresPrestacaoDTO;
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
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaContaRepository categoriaContaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;

    @Transactional
    public ContaReceberDTO create(ContaReceberDTO dto) {
        log.info("Criando conta a receber: {}", dto.getDescricao());
        ContaReceber conta = toEntity(dto);
        ContaReceber saved = contaReceberRepository.save(conta);
        log.info("Conta a receber criada com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findAll(int page, int size, String sortBy, String direction) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC")
            ? Sort.Direction.DESC
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        return contaReceberRepository.findAll(pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public ContaReceberDTO findById(Long id) {
        ContaReceber conta = contaReceberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a receber não encontrada com ID: " + id));
        return toDTO(conta);
    }

    @Transactional
    public ContaReceberDTO update(Long id, ContaReceberDTO dto) {
        ContaReceber conta = contaReceberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a receber não encontrada com ID: " + id));
        updateEntityFromDTO(conta, dto);
        ContaReceber updated = contaReceberRepository.save(conta);
        return toDTO(updated);
    }

    @Transactional
    public void delete(Long id) {
        if (!contaReceberRepository.existsById(id)) {
            throw new RuntimeException("Conta a receber não encontrada com ID: " + id);
        }
        contaReceberRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findByCliente(Long clienteId, Pageable pageable) {
        return contaReceberRepository.findByClienteId(clienteId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        return contaReceberRepository.findByVeiculoId(veiculoId, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findVencidas(Pageable pageable) {
        return contaReceberRepository.findVencidas(LocalDate.now(), pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findAVencer(int dias, Pageable pageable) {
        LocalDate hoje = LocalDate.now();
        LocalDate dataFutura = hoje.plusDays(dias);
        return contaReceberRepository.findAVencer(hoje, dataFutura, pageable).map(this::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<ContaReceberDTO> findByPeriodo(LocalDate dataInicio, LocalDate dataFim, Pageable pageable) {
        return contaReceberRepository.findByDataVencimentoBetween(dataInicio, dataFim, pageable).map(this::toDTO);
    }

    @Transactional
    public ContaReceberDTO registrarRecebimento(Long id, BigDecimal valorRecebido, LocalDate dataRecebimento, String usuario) {
        log.info("Registrando recebimento da conta ID: {} - Valor: {}", id, valorRecebido);
        ContaReceber conta = contaReceberRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Conta a receber não encontrada com ID: " + id));

        BigDecimal novoValorRecebido = conta.getValorRecebido().add(valorRecebido);
        conta.setValorRecebido(novoValorRecebido);
        conta.setDataRecebimento(dataRecebimento);
        conta.setUsuarioRecebimento(usuario);

        if (novoValorRecebido.compareTo(conta.getValorTotal()) >= 0) {
            conta.setStatus(StatusConta.RECEBIDO);
        } else if (novoValorRecebido.compareTo(BigDecimal.ZERO) > 0) {
            conta.setStatus(StatusConta.PARCIAL);
        }

        ContaReceber updated = contaReceberRepository.save(conta);
        log.info("Recebimento registrado com sucesso");
        return toDTO(updated);
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPendente() {
        BigDecimal total = contaReceberRepository.sumTotalByStatus(StatusConta.PENDENTE);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getSaldoAReceber() {
        BigDecimal saldo = contaReceberRepository.sumSaldoAReceber();
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    @Transactional
    public ContaReceberDTO processarDocumentoCte(DocumentoCteDTO cteDTO) {
        log.info("Processando CT-e {} - Tomador: {}",
                cteDTO.getNumero(),
                cteDTO.getTomador() != null ? cteDTO.getTomador().getRazaoSocial() : "N/A");

        try {
            // 1. Buscar ou validar Cliente (tomador é quem paga pelo serviço)
            Cliente cliente = buscarOuValidarCliente(cteDTO.getTomador());

            // 2. Buscar categoria padrão para frete/transporte
            CategoriaConta categoria = buscarCategoriaFrete();

            // 3. Tentar localizar Viagem relacionada (opcional)
            Viagem viagem = tentarLocalizarViagem(cteDTO);

            // 4. Construir entidade ContaReceber a partir do CT-e
            ContaReceber contaReceber = construirContaReceberDeCte(cteDTO, cliente, categoria, viagem);

            // 5. Salvar e retornar
            ContaReceber saved = contaReceberRepository.save(contaReceber);
            log.info("Conta a receber criada com sucesso. ID: {} - CT-e: {}",
                    saved.getId(),
                    cteDTO.getNumero());

            return toDTO(saved);

        } catch (Exception e) {
            log.error("Erro ao processar CT-e {}: {}", cteDTO.getNumero(), e.getMessage(), e);
            throw new RuntimeException("Falha ao processar CT-e " + cteDTO.getNumero(), e);
        }
    }

    private String limparCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        // Remove todos os caracteres não numéricos
        return cnpj.replaceAll("[^0-9]", "");
    }

    private Cliente buscarOuValidarCliente(EmpresaDTO tomadorDTO) {
        // Limpa CNPJ (remove formatação)
        String cnpj = limparCnpj(tomadorDTO.getCnpj());

        // Tenta encontrar cliente existente por CNPJ
        Optional<Cliente> clienteOpt = clienteRepository.findByCnpjCpf(cnpj);

        if (clienteOpt.isPresent()) {
            log.info("Cliente encontrado: {}", clienteOpt.get().getNome());
            return clienteOpt.get();
        }

        // Cliente não encontrado - criar automaticamente a partir dos dados do tomador
        log.info("Cliente não encontrado - Criando automaticamente - CNPJ: {} - Razão Social: {}",
                cnpj,
                tomadorDTO.getRazaoSocial());

        Cliente novoCliente = criarClienteDeTomador(tomadorDTO, cnpj);
        Cliente savedCliente = clienteRepository.save(novoCliente);

        log.info("Cliente criado automaticamente com ID: {} - {}",
                savedCliente.getId(),
                savedCliente.getNome());

        return savedCliente;
    }

    private Cliente criarClienteDeTomador(EmpresaDTO tomadorDTO, String cnpjLimpo) {
        Cliente cliente = new Cliente();

        // Dados básicos
        cliente.setNome(tomadorDTO.getRazaoSocial());
        cliente.setCnpjCpf(cnpjLimpo);
        cliente.setIe(tomadorDTO.getIe());

        // Endereço
        cliente.setEndereco(tomadorDTO.getEndereco());
        cliente.setCidade(tomadorDTO.getMunicipio());
        cliente.setUf(tomadorDTO.getUf());
        cliente.setCep(tomadorDTO.getCep());

        // Status
        cliente.setStatus(true);
        cliente.setCooperado(false);

        return cliente;
    }

    private CategoriaConta buscarCategoriaFrete() {
        // Usa ID 1 como categoria padrão temporário
        // TODO: Configurar via application.properties (cte.categoria.padrao.id)
        Optional<CategoriaConta> categoriaOpt = categoriaContaRepository.findById(1L);

        if (categoriaOpt.isEmpty()) {
            throw new RuntimeException(
                    "Categoria padrão para CT-e não encontrada. " +
                    "Configure a categoria 'FRETE' ou 'TRANSPORTE' no sistema com ID 1."
            );
        }

        return categoriaOpt.get();
    }

    private Viagem tentarLocalizarViagem(DocumentoCteDTO cteDTO) {
        // Para MVP: Retorna null (sem vinculação automática)
        // Vinculação manual pode ser feita posteriormente através da UI
        log.debug("Vinculação automática de viagem não implementada para CT-e {}",
                cteDTO.getNumero());
        return null;

        // IMPLEMENTAÇÃO FUTURA:
        // - Adicionar campo chaveAcessoCte na entidade Viagem
        // - Buscar por: viagem.cliente.cnpj = cte.tomador.cnpj AND
        //              viagem.dataViagem between cte.dataEmissao-7days and cte.dataEmissao+7days
    }

    private String construirDescricao(DocumentoCteDTO cteDTO) {
        return String.format("CT-e %s/%s - Frete %s -> %s",
                cteDTO.getSerie(),
                cteDTO.getNumero(),
                cteDTO.getRemetente().getMunicipio(),
                cteDTO.getDestinatario().getMunicipio()
        );
    }

    private LocalDate calcularDataVencimento(LocalDate dataEmissao) {
        // Padrão: 30 dias a partir da emissão
        // Pode ser configurado por cliente ou categoria no futuro
        return dataEmissao.plusDays(30);
    }

    private String construirObservacao(DocumentoCteDTO cteDTO) {
        StringBuilder obs = new StringBuilder();
        obs.append("=== CT-e Importado Automaticamente ===\n");
        obs.append("Chave de Acesso: ").append(cteDTO.getChaveAcesso()).append("\n");
        obs.append("Emitente: ").append(cteDTO.getEmitente().getRazaoSocial()).append("\n");
        obs.append("Remetente: ").append(cteDTO.getRemetente().getRazaoSocial())
           .append(" - ").append(cteDTO.getRemetente().getMunicipio())
           .append("/").append(cteDTO.getRemetente().getUf()).append("\n");
        obs.append("Destinatário: ").append(cteDTO.getDestinatario().getRazaoSocial())
           .append(" - ").append(cteDTO.getDestinatario().getMunicipio())
           .append("/").append(cteDTO.getDestinatario().getUf()).append("\n");
        obs.append("Tomador: ").append(cteDTO.getTomador().getRazaoSocial())
           .append(" - ").append(cteDTO.getTomador().getMunicipio())
           .append("/").append(cteDTO.getTomador().getUf()).append("\n");

        if (cteDTO.getDadosCarga() != null) {
            obs.append("Produto: ").append(cteDTO.getDadosCarga().getProdutoPredominante()).append("\n");
            obs.append("Peso: ").append(cteDTO.getDadosCarga().getPesoBruto()).append(" kg\n");
        }

        if (cteDTO.getValoresPrestacao() != null) {
            ValoresPrestacaoDTO valores = cteDTO.getValoresPrestacao();
            obs.append("Valor Frete: ").append(valores.getValorTotalPrestacao()).append("\n");
            if (valores.getPedagio() != null && valores.getPedagio().compareTo(BigDecimal.ZERO) > 0) {
                obs.append("Pedágio: ").append(valores.getPedagio()).append("\n");
            }
            if (valores.getDespachoArifa() != null && valores.getDespachoArifa().compareTo(BigDecimal.ZERO) > 0) {
                obs.append("Despacho/Tarifa: ").append(valores.getDespachoArifa()).append("\n");
            }
        }

        if (cteDTO.getObservacoes() != null && !cteDTO.getObservacoes().isEmpty()) {
            obs.append("Obs CT-e: ").append(cteDTO.getObservacoes()).append("\n");
        }

        return obs.toString();
    }

    private ContaReceber construirContaReceberDeCte(
            DocumentoCteDTO cteDTO,
            Cliente cliente,
            CategoriaConta categoria,
            Viagem viagem) {

        ContaReceber conta = new ContaReceber();

        // Informações básicas
        conta.setDescricao(construirDescricao(cteDTO));
        conta.setNumeroDocumento(cteDTO.getNumero());
        conta.setNumeroNotaFiscal(cteDTO.getSerie() + "/" + cteDTO.getNumero());
        conta.setNumeroControle(cteDTO.getChaveAcesso());
        conta.setNumeroCte(cteDTO.getNumero());

        // Relacionamentos
        conta.setCliente(cliente);
        conta.setCategoria(categoria);

        // Relacionamentos opcionais (se viagem encontrada)
        if (viagem != null) {
            conta.setVeiculo(viagem.getVeiculo());
            conta.setMotorista(viagem.getMotorista());
        }

        // Valores financeiros
        BigDecimal valorTotal = cteDTO.getValoresPrestacao().getValorTotalPrestacao();
        conta.setValorOriginal(valorTotal);
        conta.setValorDesconto(BigDecimal.ZERO);
        conta.setValorJuros(BigDecimal.ZERO);
        conta.setValorMulta(BigDecimal.ZERO);
        conta.setValorTotal(valorTotal);
        conta.setValorRecebido(BigDecimal.ZERO);

        // Datas
        LocalDate dataEmissao = cteDTO.getDataEmissao().toLocalDate();
        conta.setDataEmissao(dataEmissao);
        conta.setDataVencimento(calcularDataVencimento(dataEmissao));
        conta.setDataCompetencia(dataEmissao);

        // Informações de frete
        if (cteDTO.getRemetente() != null && cteDTO.getRemetente().getMunicipio() != null) {
            conta.setOrigemFrete(cteDTO.getRemetente().getMunicipio() + "/" + cteDTO.getRemetente().getUf());
        }
        if (cteDTO.getDestinatario() != null && cteDTO.getDestinatario().getMunicipio() != null) {
            conta.setDestinoFrete(cteDTO.getDestinatario().getMunicipio() + "/" + cteDTO.getDestinatario().getUf());
        }
        if (Objects.nonNull(cteDTO.getDadosCarga())) {
            if(Objects.nonNull(cteDTO.getDadosCarga().getPesoBruto())){
                conta.setPesoTransportado(new BigDecimal(cteDTO.getDadosCarga().getPesoBruto()));
            }
            conta.setTipoMercadoria(cteDTO.getDadosCarga().getProdutoPredominante());
        }

        // Status
        conta.setStatus(StatusConta.PENDENTE);

        // Observações - armazena detalhes do CT-e
        conta.setObservacao(construirObservacao(cteDTO));

        // Auditoria
        conta.setUsuarioCadastro("SISTEMA_CTE_AUTOMATICO");
        conta.setRecorrente(false);

        return conta;
    }

    private ContaReceberDTO toDTO(ContaReceber entity) {
        ContaReceberDTO dto = new ContaReceberDTO();
        dto.setId(entity.getId());
        dto.setDescricao(entity.getDescricao());
        dto.setNumeroDocumento(entity.getNumeroDocumento());
        dto.setNumeroNotaFiscal(entity.getNumeroNotaFiscal());
        dto.setNumeroControle(entity.getNumeroControle());
        dto.setNumeroCte(entity.getNumeroCte());

        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNome(entity.getCliente().getNome());
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

        dto.setValorOriginal(entity.getValorOriginal());
        dto.setValorDesconto(entity.getValorDesconto());
        dto.setValorJuros(entity.getValorJuros());
        dto.setValorMulta(entity.getValorMulta());
        dto.setValorTotal(entity.getValorTotal());
        dto.setValorRecebido(entity.getValorRecebido());
        dto.setSaldo(entity.calcularSaldo());
        dto.setDataEmissao(entity.getDataEmissao());
        dto.setDataVencimento(entity.getDataVencimento());
        dto.setDataRecebimento(entity.getDataRecebimento());
        dto.setDataCompetencia(entity.getDataCompetencia());
        dto.setStatus(entity.getStatus());
        dto.setNumeroParcela(entity.getNumeroParcela());
        dto.setTotalParcelas(entity.getTotalParcelas());
        dto.setRecorrente(entity.getRecorrente());
        dto.setPeriodicidade(entity.getPeriodicidade());
        dto.setOrigemFrete(entity.getOrigemFrete());
        dto.setDestinoFrete(entity.getDestinoFrete());
        dto.setPesoTransportado(entity.getPesoTransportado());
        dto.setTipoMercadoria(entity.getTipoMercadoria());
        dto.setDistanciaKm(entity.getDistanciaKm());
        dto.setObservacao(entity.getObservacao());
        dto.setAnexos(entity.getAnexos());
        dto.setUsuarioCadastro(entity.getUsuarioCadastro());
        dto.setUsuarioRecebimento(entity.getUsuarioRecebimento());
        dto.setVencida(entity.isVencida());
        return dto;
    }

    private ContaReceber toEntity(ContaReceberDTO dto) {
        ContaReceber entity = new ContaReceber();
        entity.setDescricao(dto.getDescricao());
        entity.setNumeroDocumento(dto.getNumeroDocumento());
        entity.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        entity.setNumeroControle(dto.getNumeroControle());
        entity.setNumeroCte(dto.getNumeroCte());

        entity.setCliente(clienteRepository.findById(dto.getClienteId())
            .orElseThrow(() -> new RuntimeException("Cliente não encontrado")));
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

        entity.setValorOriginal(dto.getValorOriginal());
        entity.setValorDesconto(dto.getValorDesconto() != null ? dto.getValorDesconto() : BigDecimal.ZERO);
        entity.setValorJuros(dto.getValorJuros() != null ? dto.getValorJuros() : BigDecimal.ZERO);
        entity.setValorMulta(dto.getValorMulta() != null ? dto.getValorMulta() : BigDecimal.ZERO);
        entity.setValorTotal(dto.getValorTotal());
        entity.setValorRecebido(dto.getValorRecebido() != null ? dto.getValorRecebido() : BigDecimal.ZERO);
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setDataRecebimento(dto.getDataRecebimento());
        entity.setDataCompetencia(dto.getDataCompetencia());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : StatusConta.PENDENTE);
        entity.setNumeroParcela(dto.getNumeroParcela());
        entity.setTotalParcelas(dto.getTotalParcelas());
        entity.setRecorrente(dto.getRecorrente() != null ? dto.getRecorrente() : false);
        entity.setPeriodicidade(dto.getPeriodicidade());
        entity.setOrigemFrete(dto.getOrigemFrete());
        entity.setDestinoFrete(dto.getDestinoFrete());
        entity.setPesoTransportado(dto.getPesoTransportado());
        entity.setTipoMercadoria(dto.getTipoMercadoria());
        entity.setDistanciaKm(dto.getDistanciaKm());
        entity.setObservacao(dto.getObservacao());
        entity.setAnexos(dto.getAnexos());
        entity.setUsuarioCadastro(dto.getUsuarioCadastro());
        return entity;
    }

    private void updateEntityFromDTO(ContaReceber entity, ContaReceberDTO dto) {
        entity.setDescricao(dto.getDescricao());
        entity.setNumeroDocumento(dto.getNumeroDocumento());
        entity.setNumeroNotaFiscal(dto.getNumeroNotaFiscal());
        entity.setNumeroControle(dto.getNumeroControle());
        entity.setNumeroCte(dto.getNumeroCte());
        entity.setValorOriginal(dto.getValorOriginal());
        entity.setValorDesconto(dto.getValorDesconto());
        entity.setValorJuros(dto.getValorJuros());
        entity.setValorMulta(dto.getValorMulta());
        entity.setValorTotal(dto.getValorTotal());
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setDataCompetencia(dto.getDataCompetencia());
        entity.setStatus(dto.getStatus());
        entity.setOrigemFrete(dto.getOrigemFrete());
        entity.setDestinoFrete(dto.getDestinoFrete());
        entity.setPesoTransportado(dto.getPesoTransportado());
        entity.setTipoMercadoria(dto.getTipoMercadoria());
        entity.setDistanciaKm(dto.getDistanciaKm());
        entity.setObservacao(dto.getObservacao());
    }
}
