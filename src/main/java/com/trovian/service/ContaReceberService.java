package com.trovian.service;

import com.trovian.dto.*;
import com.trovian.entity.*;
import com.trovian.enums.StatusConta;
import com.trovian.enums.TipoConta;
import com.trovian.enums.TipoFornecedor;
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
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ContaReceberService {

    private final ContaReceberRepository contaReceberRepository;
    private final FornecedorRepository fornecedorRepository;
    private final ClienteRepository clienteRepository;
    private final CategoriaContaRepository categoriaContaRepository;
    private final CentroCustoRepository centroCustoRepository;
    private final FormaPagamentoRepository formaPagamentoRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final ViagemRepository viagemRepository;
    private final ImagemArquivoRepository arquivoRepository;
    private final ClientePlanoRepository clientePlanoRepository;
    private final HistoricoPagamentoRepository historicoPagamentoRepository;

    @Transactional
    public ContaReceberDTO create(ContaReceberDTO dto) {
        log.info("Criando conta a receber: {}", dto.getDescricao());
        dto.setNumeroControle(gerarNumeroControle());
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
        if (updated.getStatus().equals(StatusConta.RECEBIDO) && updated.getClientePlano() != null) {
            HistoricoPagamento historico = new HistoricoPagamento();
            historico.setClientePlano(updated.getClientePlano());
            historico.setValorPago(updated.getValorRecebido());
            historico.setDataPagamento(updated.getDataRecebimento());
            historico.setDataReferencia(updated.getDataCompetencia());
            historico.setFormaPagamento(updated.getClientePlano().getFormaPagamento());
            historicoPagamentoRepository.save(historico);
            log.info("HistoricoPagamento criado automaticamente para ClientePlano ID: {}", updated.getClientePlano().getId());
        }
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

        if (updated.getClientePlano() != null) {
            HistoricoPagamento historico = new HistoricoPagamento();
            historico.setClientePlano(updated.getClientePlano());
            historico.setValorPago(valorRecebido);
            historico.setDataPagamento(dataRecebimento);
            historico.setDataReferencia(updated.getDataCompetencia());
            historico.setFormaPagamento(updated.getClientePlano().getFormaPagamento());
            historicoPagamentoRepository.save(historico);
            log.info("HistoricoPagamento criado automaticamente para ClientePlano ID: {}", updated.getClientePlano().getId());
        }

        return toDTO(updated);
    }

    @Transactional(readOnly = true)
    public List<ContaReceberDTO> findByClientePlano(Long clientePlanoId) {
        return contaReceberRepository
                .findByClientePlanoIdOrderByDataVencimentoAsc(clientePlanoId)
                .stream().map(this::toDTO).toList();
    }

    @Transactional(readOnly = true)
    public BigDecimal getTotalPendente(Long clienteId) {
        BigDecimal total = contaReceberRepository.sumTotalByStatus(StatusConta.PENDENTE, clienteId);
        return total != null ? total : BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public BigDecimal getSaldoAReceber(Long clienteId) {
        BigDecimal saldo = contaReceberRepository.sumSaldoAReceber(clienteId);
        return saldo != null ? saldo : BigDecimal.ZERO;
    }

    @Transactional
    public ContaReceberDTO processarDocumentoCte(DadosFilaDTO dadosFilaDTO) {
        try {
            // 1. Buscar o Cliente pela placa informada e faz o processo somente se haver um carro cadastrado
            Optional<Veiculo> optionalVeiculo = veiculoRepository.findByPlacaIgnoreCase(dadosFilaDTO.getPlaca());
            if(optionalVeiculo.isPresent()) {
                Veiculo veiculo = optionalVeiculo.get();
                Cliente cliente = veiculo.getCliente();
                DocumentoCteDTO documentoCteDTO = dadosFilaDTO.getDocumentoCte();
                Fornecedor fornecedorTomador = buscarOuValidarFornecedor(documentoCteDTO.getTomador(), cliente);
                CategoriaConta categoria = buscarCategoriaFrete(cliente);
                //TODO implementar quando tiver mais dados na base
                Viagem viagem = tentarLocalizarViagem(documentoCteDTO);
                ContaReceber contaReceber = construirContaReceberDeCte(documentoCteDTO, cliente, categoria, fornecedorTomador, veiculo);
                if(Objects.nonNull(dadosFilaDTO.getBase64())){
                    contaReceber.setTemImagem(Boolean.TRUE);
                }
                ContaReceber saved = contaReceberRepository.save(contaReceber);
                log.info("Conta a receber criada com sucesso. ID: {} - CT-e: {}",
                        saved.getId(),
                        documentoCteDTO.getNumero());
                if(Objects.nonNull(saved.getTemImagem()) && saved.getTemImagem()){
                    salvarImagem(cliente, saved, dadosFilaDTO);
                }
                return toDTO(saved);
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Falha ao processar CT-e " /*+ cteDTO.getNumero()*/, e);
        }
    }

    @Transactional
    public List<ContaReceber> criarContaReceberDePlano(ClientePlano clientePlano) {
        Cliente clienteMaster = clienteRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Cliente master (ID=1) não encontrado"));
        CategoriaConta categoria = buscarOuCriarCategoriaAssinatura(clienteMaster);

        LocalDate dataInicio = clientePlano.getDataInicio() != null ? clientePlano.getDataInicio() : LocalDate.now();
        LocalDate dataFinal = clientePlano.getDataFinal();
        Integer diaVencimento = clientePlano.getDiaVencimento();
        BigDecimal valor = clientePlano.getValorFinal() != null ? clientePlano.getValorFinal() : BigDecimal.ZERO;
        String periodicidade = clientePlano.getPlano().getPeriodicidade() != null
                ? clientePlano.getPlano().getPeriodicidade().name() : null;

        Fornecedor fornecedorTrovian = fornecedorRepository.buscarFornecedorTrovian().orElse(null);

        // Somente MENSAL com dataFinal e diaVencimento gera N parcelas
        boolean gerarParcelas = "MENSAL".equals(periodicidade) && dataFinal != null && diaVencimento != null;

        if (!gerarParcelas) {
            ContaReceber conta = construirParcelaDePlano(clientePlano, clienteMaster, categoria,
                    fornecedorTrovian, valor, periodicidade,
                    clientePlano.getDataVencimento() != null ? clientePlano.getDataVencimento() : dataInicio.plusMonths(1),
                    dataInicio);
            ContaReceber saved = contaReceberRepository.save(conta);
            log.info("Conta a receber criada para plano ID: {}. Conta ID: {}", clientePlano.getId(), saved.getId());
            return List.of(saved);
        }

        // Calcular primeiro vencimento: primeiro diaVencimento >= dataInicio
        LocalDate primeiroVencimento;
        if (diaVencimento >= dataInicio.getDayOfMonth()) {
            primeiroVencimento = dataInicio.withDayOfMonth(diaVencimento);
        } else {
            primeiroVencimento = dataInicio.plusMonths(1).withDayOfMonth(diaVencimento);
        }

        List<ContaReceber> parcelas = new ArrayList<>();
        LocalDate vencimentoAtual = primeiroVencimento;
        int numeroParcela = 1;

        // Contar total de parcelas primeiro
        int totalParcelas = 0;
        LocalDate temp = primeiroVencimento;
        while (!temp.isAfter(dataFinal)) {
            totalParcelas++;
            temp = avancarPeriodicidade(temp, periodicidade);
        }

        while (!vencimentoAtual.isAfter(dataFinal)) {
            LocalDate dataCompetencia = vencimentoAtual.withDayOfMonth(1);
            ContaReceber conta = construirParcelaDePlano(clientePlano, clienteMaster, categoria,
                    fornecedorTrovian, valor, periodicidade, vencimentoAtual, dataCompetencia);
            conta.setNumeroParcela(numeroParcela);
            conta.setTotalParcelas(totalParcelas);
            ContaReceber saved = contaReceberRepository.save(conta);
            parcelas.add(saved);
            numeroParcela++;
            vencimentoAtual = avancarPeriodicidade(vencimentoAtual, periodicidade);
        }

        log.info("Criadas {} parcelas de conta a receber para plano ID: {}", parcelas.size(), clientePlano.getId());
        return parcelas;
    }

    private LocalDate avancarPeriodicidade(LocalDate data, String periodicidade) {
        if (periodicidade == null) return data.plusMonths(1);
        return switch (periodicidade) {
            case "ANUAL" -> data.plusYears(1);
            case "SEMANAL" -> data.plusWeeks(1);
            default -> data.plusMonths(1);
        };
    }

    private ContaReceber construirParcelaDePlano(ClientePlano clientePlano, Cliente clienteMaster,
            CategoriaConta categoria, Fornecedor fornecedor,
            BigDecimal valor, String periodicidade,
            LocalDate dataVencimento, LocalDate dataCompetencia) {
        ContaReceber conta = new ContaReceber();
        conta.setDescricao("Assinatura " + clientePlano.getPlano().getNome()
                + " - " + clientePlano.getCliente().getNome()
                + " - " + clientePlano.getPlano().getPeriodicidade());
        conta.setNumeroControle(gerarNumeroControle());
        conta.setCliente(clienteMaster);
        conta.setCategoria(categoria);
        conta.setStatus(StatusConta.PENDENTE);
        conta.setValorOriginal(valor);
        conta.setValorTotal(valor);
        conta.setValorDesconto(BigDecimal.ZERO);
        conta.setValorJuros(BigDecimal.ZERO);
        conta.setValorMulta(BigDecimal.ZERO);
        conta.setValorRecebido(BigDecimal.ZERO);
        conta.setDataEmissao(clientePlano.getDataInicio() != null ? clientePlano.getDataInicio() : LocalDate.now());
        conta.setDataVencimento(dataVencimento);
        conta.setDataCompetencia(dataCompetencia);
        conta.setFornecedor(fornecedor);
        conta.setRecorrente(true);
        conta.setPeriodicidade(periodicidade);
        conta.setUsuarioCadastro("SISTEMA_PLANO_AUTOMATICO");
        conta.setClientePlano(clientePlano);
        return conta;
    }

    private CategoriaConta buscarOuCriarCategoriaAssinatura(Cliente clienteMaster) {
        Optional<CategoriaConta> categoriaOpt = categoriaContaRepository.findByClienteAndCodigo(clienteMaster, "002-ASSINATURA");
        if (categoriaOpt.isPresent()) {
            return categoriaOpt.get();
        }
        CategoriaConta categoria = new CategoriaConta();
        categoria.setNome("002-ASSINATURA");
        categoria.setDescricao("Assinatura de Plano SaaS");
        categoria.setCodigo("002-ASSINATURA");
        categoria.setCliente(clienteMaster);
        categoria.setTipo(TipoConta.RECEBER);
        categoria.setStatus(false);
        categoria.setPodeEditar(false);
        return categoriaContaRepository.save(categoria);
    }

    @Transactional
    public ContaReceber criarContaReceberDeViagem(Viagem viagem) {
        // Evitar duplicatas: se já existe conta para esta viagem, não criar outra
        Optional<ContaReceber> existente = contaReceberRepository.findByViagemId(viagem.getId());
        if (existente.isPresent()) {
            log.info("Conta a receber já existe para viagem ID: {}. Ignorando criação.", viagem.getId());
            return existente.get();
        }

        log.info("Criando conta a receber para viagem ID: {}", viagem.getId());

        Cliente cliente = viagem.getCliente();
        CategoriaConta categoria = buscarCategoriaFrete(cliente);
        Fornecedor fornecedor = buscarOuCriarFornecedorDeCliente(cliente);

        ContaReceber conta = new ContaReceber();

        // Descrição: rotaIda.nome + " " + dataViagem (dd/MM/yyyy)
        String descricao = viagem.getRotaIda().getNome() + " "
                + new java.text.SimpleDateFormat("dd/MM/yyyy").format(viagem.getDataViagem());
        conta.setDescricao(descricao);

        conta.setNumeroControle(gerarNumeroControle());
        conta.setCliente(cliente);
        conta.setCategoria(categoria);
        conta.setFornecedor(fornecedor);
        conta.setVeiculo(viagem.getVeiculo());
        conta.setMotorista(viagem.getMotorista());
        conta.setViagem(viagem);
        conta.setStatus(StatusConta.PENDENTE);

        // Valores
        BigDecimal valor = viagem.getValorTotalLiquido() != null ? viagem.getValorTotalLiquido() : BigDecimal.ZERO;
        conta.setValorOriginal(valor);
        conta.setValorTotal(valor);
        conta.setValorDesconto(BigDecimal.ZERO);
        conta.setValorJuros(BigDecimal.ZERO);
        conta.setValorMulta(BigDecimal.ZERO);
        conta.setValorRecebido(BigDecimal.ZERO);

        // Datas - converter java.util.Date para LocalDate
        LocalDate dataEmissao = new java.sql.Date(viagem.getDataViagem().getTime()).toLocalDate();
        conta.setDataEmissao(dataEmissao);
        conta.setDataVencimento(dataEmissao.plusDays(30));
        conta.setDataCompetencia(dataEmissao);

        conta.setRecorrente(false);
        conta.setUsuarioCadastro("SISTEMA_VIAGEM_AUTOMATICO");

        ContaReceber saved = contaReceberRepository.save(conta);
        log.info("Conta a receber criada com sucesso para viagem ID: {}. Conta ID: {}", viagem.getId(), saved.getId());
        return saved;
    }

    private Fornecedor buscarOuCriarFornecedorDeCliente(Cliente cliente) {
        String cnpj = limparCnpj(cliente.getCnpjCpf());
        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpjCpfAndCliente(cnpj, cliente);
        if (fornecedorOpt.isPresent()) {
            return fornecedorOpt.get();
        }
        Fornecedor novoFornecedor = new Fornecedor();
        novoFornecedor.setRazaoSocial(cliente.getNome());
        novoFornecedor.setCnpjCpf(cnpj);
        novoFornecedor.setLogradouro(cliente.getEndereco());
        novoFornecedor.setCidade(cliente.getCidade());
        novoFornecedor.setUf(cliente.getUf());
        novoFornecedor.setCep(cliente.getCep());
        novoFornecedor.setTipo(TipoFornecedor.EMBARCADOR);
        novoFornecedor.setStatus(true);
        novoFornecedor.setCliente(cliente);
        return fornecedorRepository.save(novoFornecedor);
    }

    private String limparCnpj(String cnpj) {
        if (cnpj == null) {
            return null;
        }
        return cnpj.replaceAll("[^0-9]", "");
    }

    private Fornecedor buscarOuValidarFornecedor(EmpresaDTO tomadorDTO, Cliente cliente) {
        String cnpj = limparCnpj(tomadorDTO.getCnpj());
        Optional<Fornecedor> fornecedorOpt = fornecedorRepository.findByCnpjCpfAndCliente(cnpj, cliente);
        if (fornecedorOpt.isPresent()) {
            return fornecedorOpt.get();
        }
        Fornecedor novoFornecedor = criarFornecedorDeTomador(tomadorDTO, cnpj);
        novoFornecedor.setCliente(cliente);
        return fornecedorRepository.save(novoFornecedor);
    }

    private Fornecedor criarFornecedorDeTomador(EmpresaDTO tomadorDTO, String cnpjLimpo) {
        Fornecedor fornecedor = new Fornecedor();
        fornecedor.setRazaoSocial(tomadorDTO.getRazaoSocial());
        fornecedor.setCnpjCpf(cnpjLimpo);
        fornecedor.setInscricaoEstadual(tomadorDTO.getIe());
        if(Objects.nonNull(tomadorDTO.getEndereco())){
            fornecedor.setLogradouro(tomadorDTO.getEndereco().getLogradouro());
            fornecedor.setCidade(tomadorDTO.getEndereco().getMunicipio());
            fornecedor.setUf(tomadorDTO.getEndereco().getUf());
            fornecedor.setCep(tomadorDTO.getEndereco().getCep());
        }
        fornecedor.setStatus(true);
        fornecedor.setTipo(TipoFornecedor.TOMADOR);
        return fornecedor;
    }

    private CategoriaConta buscarCategoriaFrete(Cliente cliente) {
        String codigo = "001-TRANSPORTE";
        Optional<CategoriaConta> categoriaOpt = categoriaContaRepository.findByClienteAndCodigo(cliente, codigo);
        if (categoriaOpt.isPresent()) {
            return categoriaOpt.get();
        }
        //Criar uma categoria
        CategoriaConta categoriaConta = new CategoriaConta();
        categoriaConta.setPodeEditar(Boolean.FALSE);
        categoriaConta.setNome(codigo);
        categoriaConta.setCodigo(codigo);
        categoriaConta.setCliente(cliente);
        categoriaConta.setTipo(TipoConta.RECEBER);
        categoriaConta.setStatus(Boolean.TRUE);
        categoriaConta.setDataCadastro(new Date());
        return categoriaContaRepository.save(categoriaConta);
    }

    //TODO
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
        return String.format("CT-e %s/%s",
                cteDTO.getSerie(),
                cteDTO.getNumero()
        );
    }

    private LocalDate calcularDataVencimento(LocalDate dataEmissao) {
        // Padrão: 30 dias a partir da emissão
        // Pode ser configurado por cliente ou categoria no futuro
        return dataEmissao.plusDays(30);
    }

    private String construirObservacao(DocumentoCteDTO cteDTO) {
        try {
            StringBuilder obs = new StringBuilder();
            obs.append("=== CT-e Importado Automaticamente ===\n");
            obs.append("Chave de Acesso: ").append(cteDTO.getChaveAcesso()).append("\n");
            obs.append("Emitente: ").append(cteDTO.getEmitente().getRazaoSocial()).append("\n");
            obs.append("Remetente: ").append(cteDTO.getRemetente().getRazaoSocial());
            obs.append("Destinatário: ").append(cteDTO.getDestinatario().getRazaoSocial());
            obs.append("Tomador: ").append(cteDTO.getTomador().getRazaoSocial());
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
        } catch (Exception e) {
            return "Sem Observação";
        }
    }

    private ContaReceber construirContaReceberDeCte(
            DocumentoCteDTO cteDTO,
            Cliente cliente,
            CategoriaConta categoria,
            Fornecedor fornecedor,
            Veiculo veiculo) {

        ContaReceber conta = new ContaReceber();

        // Informações básicas
        conta.setDescricao(construirDescricao(cteDTO));
        conta.setNumeroDocumento(cteDTO.getNumero());
        conta.setNumeroNotaFiscal(cteDTO.getSerie() + "/" + cteDTO.getNumero());
        conta.setNumeroControle(gerarNumeroControle());
        conta.setNumeroCte(cteDTO.getNumero());

        // Relacionamentos
        conta.setCliente(cliente);
        conta.setCategoria(categoria);
        conta.setFornecedor(fornecedor);
        // Relacionamentos opcionais (se viagem encontrada)
        if (Objects.nonNull(veiculo)) {
            conta.setVeiculo(veiculo);
        }

        // Valores financeiros
        BigDecimal valorTotal = cteDTO.getValoresPrestacao().getValorTotalPrestacao();
        if(Objects.isNull(valorTotal)){
            valorTotal = BigDecimal.ZERO;
        }
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
        if (Objects.nonNull(cteDTO.getRemetente())
                && Objects.nonNull(cteDTO.getRemetente().getEndereco())) {
            conta.setOrigemFrete(cteDTO.getRemetente().getEndereco().getMunicipio() + "/" + cteDTO.getRemetente().getEndereco().getUf());
        }
        if (Objects.nonNull(cteDTO.getDestinatario())
                && Objects.nonNull(cteDTO.getDestinatario().getEndereco())) {
            conta.setDestinoFrete(cteDTO.getDestinatario().getEndereco().getMunicipio() + "/" + cteDTO.getDestinatario().getEndereco().getUf());
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
        dto.setTemImagem(entity.getTemImagem());


        if (entity.getCliente() != null) {
            dto.setClienteId(entity.getCliente().getId());
            dto.setClienteNome(entity.getCliente().getNome());
        }
        if (entity.getFornecedor() != null) {
            dto.setFornecedorId(entity.getFornecedor().getId());
            String nome = entity.getFornecedor().getNomeFantasia();
            if(Objects.isNull(nome) || nome.isEmpty()){
                nome = entity.getFornecedor().getRazaoSocial();
            }
            dto.setFornecedorNome(nome);
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
        if (entity.getViagem() != null) {
            dto.setViagemId(entity.getViagem().getId());
        }
        if (entity.getClientePlano() != null) {
            dto.setClientePlanoId(entity.getClientePlano().getId());
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
        entity.setTemImagem(dto.getTemImagem());

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
        if(dto.getFornecedorId() != null){
            entity.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId()).orElse(null));
        }
        if (dto.getViagemId() != null) {
            entity.setViagem(viagemRepository.findById(dto.getViagemId()).orElse(null));
        }
        if (dto.getClientePlanoId() != null) {
            entity.setClientePlano(clientePlanoRepository.findById(dto.getClientePlanoId()).orElse(null));
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
        entity.setNumeroCte(dto.getNumeroCte());
        entity.setValorOriginal(dto.getValorOriginal());
        entity.setValorDesconto(dto.getValorDesconto());
        entity.setValorRecebido(dto.getValorRecebido());
        entity.setValorJuros(dto.getValorJuros());
        entity.setValorMulta(dto.getValorMulta());
        entity.setValorTotal(dto.getValorTotal());
        entity.setNumeroParcela(dto.getNumeroParcela());
        entity.setTotalParcelas(dto.getTotalParcelas());
        entity.setRecorrente(dto.getRecorrente());
        entity.setPeriodicidade(dto.getPeriodicidade());
        entity.setDataEmissao(dto.getDataEmissao());
        entity.setDataVencimento(dto.getDataVencimento());
        entity.setDataCompetencia(dto.getDataCompetencia());
        entity.setDataRecebimento(dto.getDataRecebimento());
        entity.setStatus(dto.getStatus());
        entity.setOrigemFrete(dto.getOrigemFrete());
        entity.setDestinoFrete(dto.getDestinoFrete());
        entity.setPesoTransportado(dto.getPesoTransportado());
        entity.setTipoMercadoria(dto.getTipoMercadoria());
        entity.setDistanciaKm(dto.getDistanciaKm());
        entity.setObservacao(dto.getObservacao());

        if (dto.getFornecedorId() != null) {
            entity.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId()).orElse(null));
        } else {
            entity.setFornecedor(null);
        }
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
        if(dto.getFornecedorId() != null){
            entity.setFornecedor(fornecedorRepository.findById(dto.getFornecedorId()).orElse(null));
        }
        if (dto.getViagemId() != null) {
            entity.setViagem(viagemRepository.findById(dto.getViagemId()).orElse(null));
        }
        if (dto.getCategoriaId() != null) {
            entity.setCategoria(categoriaContaRepository.findById(dto.getCategoriaId()).orElse(null));
        }
        if (dto.getClientePlanoId() != null) {
            entity.setClientePlano(clientePlanoRepository.findById(dto.getClientePlanoId()).orElse(null));
        }
    }

    private void salvarImagem(Cliente cliente, ContaReceber contaReceber, DadosFilaDTO dadosFilaDTO){
        try {
            String base64Input = dadosFilaDTO.getBase64();
            String cleanBase64 = base64Input.contains(",") ?
                    base64Input.split(",")[1] : base64Input;
            byte[] bytes = Base64.getDecoder().decode(cleanBase64);

            ImagemArquivo imagemArquivo = new ImagemArquivo();
            imagemArquivo.setContaReceber(contaReceber);
            imagemArquivo.setCliente(cliente);
            imagemArquivo.setConteudoBinario(bytes);
            arquivoRepository.save(imagemArquivo);

        }catch (Exception e){
            log.error("salvarImagem", e);
        }
    }

    private  String gerarNumeroControle() {
        String data = LocalDate.now()
                .format(DateTimeFormatter.BASIC_ISO_DATE);
        long seq = contaReceberRepository.nextNumeroControle();
        return String.format("CR-%s-%06d", data, seq);
    }
}
