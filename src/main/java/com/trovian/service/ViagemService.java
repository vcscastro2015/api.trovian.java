package com.trovian.service;

import com.trovian.dto.ComissaoMotoristaDTO;
import com.trovian.dto.ConsumoDetalhadoDTO;
import com.trovian.dto.ViagemDTO;
import com.trovian.entity.*;
import com.trovian.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Service para lógica de negócio de Viagem
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ViagemService {

    private final ViagemRepository viagemRepository;
    private final VeiculoRepository veiculoRepository;
    private final MotoristaRepository motoristaRepository;
    private final RotaRepository rotaRepository;
    private final AbastecimentoRepository abastecimentoRepository;
    private final ClienteRepository clienteRepository;
    private final ConsumoDetalhadoRepository consumoDetalhadoRepository;
    private final ComissaoMotoristaService comissaoMotoristaService;
    private final ChecklistRealizadoService checklistRealizadoService;

    /**
     * Busca todas as viagens com paginação
     *
     * @param pageable Configuração de paginação
     * @return Página de viagens
     */
    @Transactional(readOnly = true)
    public Page<ViagemDTO> findAll(Pageable pageable) {
        log.info("Buscando todas as viagens com paginação: {}", pageable);
        Page<Viagem> viagens = viagemRepository.findAll(pageable);
        return viagens.map(this::toDTO);
    }

    /**
     * Busca viagem por ID
     *
     * @param id ID da viagem
     * @return ViagemDTO
     */
    @Transactional(readOnly = true)
    public ViagemDTO findById(Long id) {
        log.info("Buscando viagem por ID: {}", id);
        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + id));
        return toDTO(viagem);
    }

    /**
     * Busca viagens por veículo com paginação
     *
     * @param veiculoId ID do veículo
     * @param pageable  Configuração de paginação
     * @return Página de viagens
     */
    @Transactional(readOnly = true)
    public Page<ViagemDTO> findByVeiculo(Long veiculoId, Pageable pageable) {
        log.info("Buscando viagens do veículo ID: {} com paginação: {}", veiculoId, pageable);
        Page<Viagem> viagens = viagemRepository.findByVeiculoId(veiculoId, pageable);
        return viagens.map(this::toDTO);
    }

    /**
     * Busca viagens por motorista com paginação
     *
     * @param motoristaId ID do motorista
     * @param pageable    Configuração de paginação
     * @return Página de viagens
     */
    @Transactional(readOnly = true)
    public Page<ViagemDTO> findByMotorista(Long motoristaId, Pageable pageable) {
        log.info("Buscando viagens do motorista ID: {} com paginação: {}", motoristaId, pageable);
        Page<Viagem> viagens = viagemRepository.findByMotoristaId(motoristaId, pageable);
        return viagens.map(this::toDTO);
    }

    /**
     * Busca viagens por cliente com paginação
     *
     * @param clienteId ID do cliente
     * @param pageable  Configuração de paginação
     * @return Página de viagens
     */
    @Transactional(readOnly = true)
    public Page<ViagemDTO> findByCliente(Long clienteId, Pageable pageable) {
        log.info("Buscando viagens do cliente ID: {} com paginação: {}", clienteId, pageable);
        Page<Viagem> viagens = viagemRepository.findByClienteId(clienteId, pageable);
        return viagens.map(this::toDTO);
    }

    /**
     * Busca viagens por status da viagem (ABERTA, ANALISE, FECHADA) com paginação
     *
     * @param statusViagem Status da viagem
     * @param pageable     Configuração de paginação
     * @return Página de viagens
     */
    @Transactional(readOnly = true)
    public Page<ViagemDTO> findByStatusViagem(com.trovian.enums.StatusViagem statusViagem, Pageable pageable) {
        log.info("Buscando viagens com status {} com paginação: {}", statusViagem, pageable);
        Page<Viagem> viagens = viagemRepository.findByStatusViagem(statusViagem, pageable);
        return viagens.map(this::toDTO);
    }

    /**
     * CALCULA a viagem SEM SALVAR no banco
     * Usado quando usuário clica em "Calcular" no front
     *
     * @param dto ViagemDTO com dados básicos preenchidos
     * @return ViagemDTO com todos os cálculos realizados
     */
    @Transactional(readOnly = true)
    public ViagemDTO calcular(ViagemDTO dto) {
        log.info("Calculando viagem para veículo ID: {} e motorista ID: {}", dto.getVeiculoId(), dto.getMotoristaId());

        // 1. Buscar dados complementares
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado com ID: " + dto.getClienteId()));

        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado com ID: " + dto.getVeiculoId()));

        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado com ID: " + dto.getMotoristaId()));

        Rota rotaIda = rotaRepository.findById(dto.getRotaIdaId())
                .orElseThrow(() -> new RuntimeException("Rota de ida não encontrada com ID: " + dto.getRotaIdaId()));

        Rota rotaVolta = null;
        if (dto.getRotaVoltaId() != null) {
            rotaVolta = rotaRepository.findById(dto.getRotaVoltaId())
                    .orElseThrow(() -> new RuntimeException("Rota de volta não encontrada com ID: " + dto.getRotaVoltaId()));
        }

        // Buscar último abastecimento (se não foi informado)
        Abastecimento ultimoAbastecimento = null;
        if (dto.getAbastecimentoId() != null) {
            ultimoAbastecimento = abastecimentoRepository.findById(dto.getAbastecimentoId())
                    .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado com ID: " + dto.getAbastecimentoId()));
        } else {
            // Buscar último abastecimento do veículo
            ultimoAbastecimento = buscarUltimoAbastecimento(veiculo.getId());
            if (ultimoAbastecimento != null) {
                dto.setAbastecimentoId(ultimoAbastecimento.getId());
            }
        }

        // Preencher dados do cliente, veículo e motorista no DTO (para retornar ao front)
        preencherDadosComplementares(dto, cliente, veiculo, motorista, rotaIda, rotaVolta, ultimoAbastecimento);

        // 2. Executar cálculos
        calcularReceitaBruta(dto);
        calcularFatorCarga(dto, veiculo);
        calcularCombustivel(dto, veiculo, rotaIda, rotaVolta, ultimoAbastecimento);
        ConsumoDetalhadoDTO consumoDetalhado = calcularFatorTerreno(rotaIda, veiculo, dto.getQuantidadeToneladasIda(), dto);
        dto.setConsumoDetalhadoIda(consumoDetalhado);
        if (rotaVolta != null) {
            consumoDetalhado = calcularFatorTerreno(rotaVolta, veiculo, dto.getQuantidadeToneladasIda(), dto);
            dto.setConsumoDetalhadoVolta(consumoDetalhado);
        }
        calcularPedagios(dto, veiculo);
        calcularComissao(dto, motorista);
        calcularResultadoFinal(dto);

        log.info("Cálculos concluídos. Receita Bruta: {}, Lucro Líquido: {}, Margem: {}%",
                dto.getValorTotalBrutoViagem(), dto.getValorTotalLiquido(), dto.getMargemPercentual());

        // 3. Retornar DTO calculado (NÃO salva no banco)
        return dto;
    }

    /**
     * Cria e SALVA viagem no banco
     *
     * @param dto ViagemDTO
     * @return ViagemDTO criado
     */
    @Transactional
    public ViagemDTO create(ViagemDTO dto) {
        log.info("Criando nova viagem");

        // Recalcula para garantir valores corretos
        ViagemDTO calculado = calcular(dto);

        // Converte para entidade e salva
        Viagem viagem = toEntity(calculado);
        Viagem saved = viagemRepository.save(viagem);
        log.info("Viagem criada com sucesso. ID: {}", saved.getId());
        criarComissaoMotorista(saved, dto.getComissaoMotorista());
        return toDTO(saved);
    }

    /**
     * Atualiza viagem existente
     *
     * @param id  ID da viagem
     * @param dto ViagemDTO
     * @return ViagemDTO atualizado
     */
    @Transactional
    public ViagemDTO update(Long id, ViagemDTO dto) {
        log.info("Atualizando viagem ID: {}", id);

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + id));

        // Recalcula com novos dados
        ViagemDTO calculado = calcular(dto);

        // Atualiza entidade
        updateEntity(viagem, calculado);
        Viagem updated = viagemRepository.save(viagem);
        log.info("Viagem atualizada com sucesso. ID: {}", updated.getId());
        editarComissaoMotorista(updated, dto.getComissaoMotorista());
        return toDTO(updated);
    }

    /**
     * Recalcula viagem existente
     *
     * @param id ID da viagem
     * @return ViagemDTO recalculado
     */
    @Transactional
    public ViagemDTO recalcular(Long id) {
        log.info("Recalculando viagem ID: {}", id);

        Viagem viagem = viagemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Viagem não encontrada com ID: " + id));

        ViagemDTO dto = toDTO(viagem);
        ViagemDTO calculado = calcular(dto);

        // Atualiza entidade com valores recalculados
        updateEntity(viagem, calculado);
        Viagem updated = viagemRepository.save(viagem);
        log.info("Viagem recalculada com sucesso. ID: {}", updated.getId());

        return toDTO(updated);
    }

    /**
     * Deleta viagem
     *
     * @param id ID da viagem
     */
    @Transactional
    public void delete(Long id) {
        log.info("Deletando viagem ID: {}", id);

        if (!viagemRepository.existsById(id)) {
            throw new RuntimeException("Viagem não encontrada com ID: " + id);
        }

        viagemRepository.deleteById(id);
        log.info("Viagem deletada com sucesso. ID: {}", id);
    }

    // ========== MÉTODOS DE CÁLCULO ==========

    /**
     * Calcula receita bruta da ida e volta
     */
    private void calcularReceitaBruta(ViagemDTO dto) {
        log.debug("Calculando receita bruta");

        // Ida
        BigDecimal receitaIda = BigDecimal.valueOf(dto.getQuantidadeToneladasIda())
                .multiply(dto.getValorToneladaBrutaIda())
                .setScale(2, RoundingMode.HALF_UP);

        BigDecimal valorImposto = receitaIda
                .multiply(dto.getImpostoBaseIda())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        receitaIda =  receitaIda.subtract(valorImposto);
        dto.setValorTotalBrutoIda(receitaIda);

        // Volta (se existir)
        BigDecimal receitaVolta = BigDecimal.ZERO;
        if (dto.getRotaVoltaId() != null && dto.getQuantidadeToneladasVolta() != null) {
            receitaVolta = BigDecimal.valueOf(dto.getQuantidadeToneladasVolta())
                    .multiply(dto.getValorToneladaBrutaVolta() != null ? dto.getValorToneladaBrutaVolta() : BigDecimal.ZERO)
                    .setScale(2, RoundingMode.HALF_UP);

             valorImposto = receitaVolta
                    .multiply(dto.getImpostoBaseVolta())
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

            receitaVolta =  receitaVolta.subtract(valorImposto);
            dto.setValorTotalBrutoVolta(receitaVolta);
        } else {
            dto.setValorTotalBrutoVolta(BigDecimal.ZERO);
        }

        // Total
        dto.setValorTotalBrutoViagem(receitaIda.add(receitaVolta));
        log.debug("Receita bruta calculada: Ida={}, Volta={}, Total={}", receitaIda, receitaVolta, dto.getValorTotalBrutoViagem());
    }

    /**
     * Calcula fator de carga
     */
    private void calcularFatorCarga(ViagemDTO dto, Veiculo veiculo) {
        log.debug("Calculando fator de carga");

        if (veiculo.getCargaMaxima() == null || veiculo.getCargaMaxima() <= 0) {
            log.warn("Veículo sem carga máxima definida. Usando fator de carga 1.0");
            dto.setFatorCarga(1.0);
            return;
        }

        double cargaTotal = (veiculo.getTara() != null ? veiculo.getTara().doubleValue() : 0.0)
                + dto.getQuantidadeToneladasIda()
                + (dto.getQuantidadeToneladasVolta() != null ? dto.getQuantidadeToneladasVolta() : 0.0);

        double fator = cargaTotal / veiculo.getCargaMaxima();
        dto.setFatorCarga(fator);
        log.debug("Fator de carga calculado: {} (carga total: {} / carga máxima: {})", fator, cargaTotal, veiculo.getCargaMaxima());
    }

    /**
     * Calcula consumo e valor do combustível
     */
    private void calcularCombustivel(ViagemDTO dto, Veiculo veiculo, Rota rotaIda, Rota rotaVolta, Abastecimento ultimoAbastecimento) {
        log.debug("Calculando combustível");

        // Distância total (converter de metros para km)
        double distanciaTotal = rotaIda.getDistanciaTotal() / 1000.0;
        if (rotaVolta != null) {
            distanciaTotal += rotaVolta.getDistanciaTotal() / 1000.0;
        }

        // Consumo estimado
        double consumo = (distanciaTotal / dto.getMediaVeiculoKmL());
        dto.setConsumoEstimadoLitros(consumo);

        // Valor total de combustível
        if (ultimoAbastecimento != null) {
            BigDecimal valorCombustivel = BigDecimal.valueOf(consumo)
                    .multiply(ultimoAbastecimento.getPrecoLitro())
                    .setScale(2, RoundingMode.HALF_UP);
            dto.setValorTotalCombustivel(valorCombustivel);
            log.debug("Combustível calculado: {} litros x R$ {} = R$ {}", consumo, ultimoAbastecimento.getPrecoLitro(), valorCombustivel);
        } else {
            dto.setValorTotalCombustivel(BigDecimal.ZERO);
            log.warn("Sem abastecimento de referência. Valor de combustível definido como zero.");
        }
    }

    /**
     * Calcula valor total de pedágios
     */
    private void calcularPedagios(ViagemDTO dto, Veiculo veiculo) {
        log.debug("Calculando pedágios");

        if (veiculo.getNumeroEixos() == null || veiculo.getNumeroEixos() <= 0) {
            log.warn("Veículo sem número de eixos definido. Usando valor zero para pedágios.");
            dto.setValorTotalPedagios(BigDecimal.ZERO);
            return;
        }

        // Ida
        BigDecimal pedagiosIda = BigDecimal.ZERO;
        if(Objects.nonNull(dto.getQuantidadePedagiosIda()) && Objects.nonNull(dto.getValorPedagioPorEixoIda())) {
            pedagiosIda = BigDecimal.valueOf(dto.getQuantidadePedagiosIda())
                    .multiply(dto.getValorPedagioPorEixoIda())
                    .multiply(BigDecimal.valueOf(veiculo.getNumeroEixos()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        // Volta
        BigDecimal pedagiosVolta = BigDecimal.ZERO;
        if (dto.getRotaVoltaId() != null && dto.getQuantidadePedagiosVolta() != null) {
            pedagiosVolta = BigDecimal.valueOf(dto.getQuantidadePedagiosVolta())
                    .multiply(dto.getValorPedagioPorEixoVolta() != null ? dto.getValorPedagioPorEixoVolta() : BigDecimal.ZERO)
                    .multiply(BigDecimal.valueOf(veiculo.getNumeroEixos()))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        dto.setValorTotalPedagios(pedagiosIda.add(pedagiosVolta));
        log.debug("Pedágios calculados: Ida={}, Volta={}, Total={}", pedagiosIda, pedagiosVolta, dto.getValorTotalPedagios());
    }

    /**
     * Calcula comissão do motorista
     */
    private void calcularComissao(ViagemDTO dto, Motorista motorista) {
        log.debug("Calculando comissão do motorista");

        if (motorista.getComissao() == null || motorista.getComissao() <= 0) {
            log.warn("Motorista sem comissão definida. Usando valor zero.");
            dto.setComissaoMotorista(BigDecimal.ZERO);
            return;
        }

        BigDecimal comissao = dto.getValorTotalBrutoViagem()
                .multiply(BigDecimal.valueOf(motorista.getComissao()))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        dto.setComissaoMotorista(comissao);
        log.debug("Comissão calculada: {}% de {} = {}", motorista.getComissao(), dto.getValorTotalBrutoViagem(), comissao);
    }

    /**
     * Calcula resultado final (custos, lucro líquido e margem)
     */
    private void calcularResultadoFinal(ViagemDTO dto) {
        log.debug("Calculando resultado final");

        // Total de custos
        BigDecimal custos = dto.getValorTotalPedagios()
                .add(dto.getValorTotalCombustivel())
                .add(dto.getComissaoMotorista())
                .setScale(2, RoundingMode.HALF_UP);
        dto.setValorTotalCustos(custos);

        // Valor líquido
        BigDecimal liquido = dto.getValorTotalBrutoViagem().subtract(custos);
        dto.setValorTotalLiquido(liquido);

        // Margem percentual
        if (dto.getValorTotalBrutoViagem().compareTo(BigDecimal.ZERO) > 0) {
            double margem = liquido.doubleValue() / dto.getValorTotalBrutoViagem().doubleValue() * 100;
            dto.setMargemPercentual(margem);
        } else {
            dto.setMargemPercentual(0.0);
        }

        log.debug("Resultado final: Custos={}, Líquido={}, Margem={}%", custos, liquido, dto.getMargemPercentual());
    }


    /**
     * Busca último abastecimento de um veículo
     */
    private Abastecimento buscarUltimoAbastecimento(Long veiculoId) {
        // TODO: Implementar busca do último abastecimento por veículo
        // Por enquanto retorna null - o AbastecimentoRepository precisa ter esse método
        log.warn("Busca de último abastecimento não implementada. Retornando null.");
        return null;
    }

    /**
     * Preenche dados complementares no DTO
     */
    private void preencherDadosComplementares(ViagemDTO dto, Cliente cliente, Veiculo veiculo, Motorista motorista,
                                               Rota rotaIda, Rota rotaVolta, Abastecimento ultimoAbastecimento) {
        // Cliente
        dto.setClienteNome(cliente.getNome());

        // Veículo
        dto.setVeiculoPlaca(veiculo.getPlaca());
        dto.setVeiculoTara(veiculo.getTara());
        dto.setVeiculoCargaMaxima(veiculo.getCargaMaxima());
        dto.setVeiculoNumeroEixos(veiculo.getNumeroEixos());

        // Motorista
        dto.setMotoristaNome(motorista.getNome());
        dto.setMotoristaComissao(motorista.getComissao());

        // Rota Ida
        dto.setRotaIdaNome(rotaIda.getNome());
        dto.setRotaIdaDistanciaKm(rotaIda.getDistanciaTotal() / 1000.0);

        // Rota Volta
        if (rotaVolta != null) {
            dto.setRotaVoltaNome(rotaVolta.getNome());
            dto.setRotaVoltaDistanciaKm(rotaVolta.getDistanciaTotal() / 1000.0);
        }

        // Abastecimento
        if (ultimoAbastecimento != null) {
            dto.setPrecoLitroCombustivel(ultimoAbastecimento.getPrecoLitro());
        }
    }

    /**
     * Converte Entity para DTO
     */
    private ViagemDTO toDTO(Viagem viagem) {
        ViagemDTO dto = new ViagemDTO();
        dto.setId(viagem.getId());
        dto.setClienteId(viagem.getCliente().getId());
        dto.setClienteNome(viagem.getCliente().getNome());
        dto.setVeiculoId(viagem.getVeiculo().getId());
        dto.setVeiculoPlaca(viagem.getVeiculo().getPlaca());
        dto.setMotoristaId(viagem.getMotorista().getId());
        dto.setMotoristaNome(viagem.getMotorista().getNome());
        dto.setRotaIdaId(viagem.getRotaIda().getId());
        dto.setRotaIdaNome(viagem.getRotaIda().getNome());
        dto.setRotaIdaDistanciaKm(viagem.getRotaIda().getDistanciaTotal() / 1000.0);

        if (viagem.getRotaVolta() != null) {
            dto.setRotaVoltaId(viagem.getRotaVolta().getId());
            dto.setRotaVoltaNome(viagem.getRotaVolta().getNome());
            dto.setRotaVoltaDistanciaKm(viagem.getRotaVolta().getDistanciaTotal() / 1000.0);
        }

        if (viagem.getAbastecimento() != null) {
            dto.setAbastecimentoId(viagem.getAbastecimento().getId());
            dto.setPrecoLitroCombustivel(viagem.getAbastecimento().getPrecoLitro());
        }

        dto.setDataViagem(viagem.getDataViagem());
        dto.setStatus(viagem.getStatus());
        dto.setStatusViagem(viagem.getStatusViagem());

        // Dados da ida
        dto.setQuantidadeToneladasIda(viagem.getQuantidadeToneladasIda());
        dto.setMaterialIda(viagem.getMaterialIda());
        dto.setValorToneladaBrutaIda(viagem.getValorToneladaBrutaIda());
        dto.setImpostoBaseIda(viagem.getImpostoBaseIda());
        dto.setQuantidadePedagiosIda(viagem.getQuantidadePedagiosIda());
        dto.setValorPedagioPorEixoIda(viagem.getValorPedagioPorEixoIda());
        dto.setValorTotalBrutoIda(viagem.getValorTotalBrutoIda());

        // Dados da volta
        dto.setQuantidadeToneladasVolta(viagem.getQuantidadeToneladasVolta());
        dto.setMaterialVolta(viagem.getMaterialVolta());
        dto.setValorToneladaBrutaVolta(viagem.getValorToneladaBrutaVolta());
        dto.setImpostoBaseVolta(viagem.getImpostoBaseVolta());
        dto.setQuantidadePedagiosVolta(viagem.getQuantidadePedagiosVolta());
        dto.setValorPedagioPorEixoVolta(viagem.getValorPedagioPorEixoVolta());
        dto.setValorTotalBrutoVolta(viagem.getValorTotalBrutoVolta());

        // Combustível
        dto.setMediaVeiculoKmL(viagem.getMediaVeiculoKmL());
        dto.setFatorCarga(viagem.getFatorCarga());
        dto.setConsumoEstimadoLitros(viagem.getConsumoEstimadoLitros());
        dto.setValorTotalCombustivel(viagem.getValorTotalCombustivel());

        // Dados do veículo
        dto.setVeiculoTara(viagem.getVeiculo().getTara());
        dto.setVeiculoCargaMaxima(viagem.getVeiculo().getCargaMaxima());
        dto.setVeiculoNumeroEixos(viagem.getVeiculo().getNumeroEixos());
        dto.setMotoristaComissao(viagem.getMotorista().getComissao());

        // Resultado final
        dto.setValorTotalBrutoViagem(viagem.getValorTotalBrutoViagem());
        dto.setValorTotalPedagios(viagem.getValorTotalPedagios());
        dto.setComissaoMotorista(viagem.getComissaoMotorista());
        dto.setValorTotalCustos(viagem.getValorTotalCustos());
        dto.setValorTotalLiquido(viagem.getValorTotalLiquido());
        dto.setMargemPercentual(viagem.getMargemPercentual());

        // Auditoria
        dto.setDataCadastro(viagem.getDataCadastro());
        dto.setUpdatedAt(viagem.getUpdatedAt() != null ? viagem.getUpdatedAt().toString() : null);

        // Consumo Detalhado
        dto.setConsumoDetalhadoIda(toConsumoDetalhadoDTO(viagem.getConsumoDetalhadoIda()));
        dto.setConsumoDetalhadoVolta(toConsumoDetalhadoDTO(viagem.getConsumoDetalhadoVolta()));

        return dto;
    }

    /**
     * Converte DTO para Entity
     */
    private Viagem toEntity(ViagemDTO dto) {
        Viagem viagem = new Viagem();

        // Buscar relacionamentos
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
        Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
        Rota rotaIda = rotaRepository.findById(dto.getRotaIdaId())
                .orElseThrow(() -> new RuntimeException("Rota de ida não encontrada"));

        viagem.setCliente(cliente);
        viagem.setVeiculo(veiculo);
        viagem.setMotorista(motorista);
        viagem.setRotaIda(rotaIda);

        if (dto.getRotaVoltaId() != null) {
            Rota rotaVolta = rotaRepository.findById(dto.getRotaVoltaId())
                    .orElseThrow(() -> new RuntimeException("Rota de volta não encontrada"));
            viagem.setRotaVolta(rotaVolta);
        }

        if (dto.getAbastecimentoId() != null) {
            Abastecimento abastecimento = abastecimentoRepository.findById(dto.getAbastecimentoId())
                    .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado"));
            viagem.setAbastecimento(abastecimento);
        }

        viagem.setDataViagem(dto.getDataViagem());
        viagem.setStatus(dto.getStatus());
        viagem.setStatusViagem(dto.getStatusViagem());

        // Dados da ida
        viagem.setQuantidadeToneladasIda(dto.getQuantidadeToneladasIda());
        viagem.setMaterialIda(dto.getMaterialIda());
        viagem.setValorToneladaBrutaIda(dto.getValorToneladaBrutaIda());
        viagem.setImpostoBaseIda(dto.getImpostoBaseIda());
        viagem.setQuantidadePedagiosIda(dto.getQuantidadePedagiosIda());
        viagem.setValorPedagioPorEixoIda(dto.getValorPedagioPorEixoIda());
        viagem.setValorTotalBrutoIda(dto.getValorTotalBrutoIda());

        // Dados da volta
        viagem.setQuantidadeToneladasVolta(dto.getQuantidadeToneladasVolta());
        viagem.setMaterialVolta(dto.getMaterialVolta());
        viagem.setValorToneladaBrutaVolta(dto.getValorToneladaBrutaVolta());
        viagem.setImpostoBaseVolta(dto.getImpostoBaseVolta());
        viagem.setQuantidadePedagiosVolta(dto.getQuantidadePedagiosVolta());
        viagem.setValorPedagioPorEixoVolta(dto.getValorPedagioPorEixoVolta());
        viagem.setValorTotalBrutoVolta(dto.getValorTotalBrutoVolta());

        // Combustível
        viagem.setMediaVeiculoKmL(dto.getMediaVeiculoKmL());
        viagem.setFatorCarga(dto.getFatorCarga());
        viagem.setConsumoEstimadoLitros(dto.getConsumoEstimadoLitros());
        viagem.setValorTotalCombustivel(dto.getValorTotalCombustivel());

        // Resultado final
        viagem.setValorTotalBrutoViagem(dto.getValorTotalBrutoViagem());
        viagem.setValorTotalPedagios(dto.getValorTotalPedagios());
        viagem.setComissaoMotorista(dto.getComissaoMotorista());
        viagem.setValorTotalCustos(dto.getValorTotalCustos());
        viagem.setValorTotalLiquido(dto.getValorTotalLiquido());
        viagem.setMargemPercentual(dto.getMargemPercentual());

        // Consumo Detalhado
        viagem.setConsumoDetalhadoIda(toConsumoDetalhadoEntity(dto.getConsumoDetalhadoIda()));
        viagem.setConsumoDetalhadoVolta(toConsumoDetalhadoEntity(dto.getConsumoDetalhadoVolta()));

        return viagem;
    }

    /**
     * Atualiza entidade com dados do DTO
     */
    private void updateEntity(Viagem viagem, ViagemDTO dto) {
        // Atualizar relacionamentos se mudaram
        if (!viagem.getCliente().getId().equals(dto.getClienteId())) {
            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
            viagem.setCliente(cliente);
        }

        if (!viagem.getVeiculo().getId().equals(dto.getVeiculoId())) {
            Veiculo veiculo = veiculoRepository.findById(dto.getVeiculoId())
                    .orElseThrow(() -> new RuntimeException("Veículo não encontrado"));
            viagem.setVeiculo(veiculo);
        }

        if (!viagem.getMotorista().getId().equals(dto.getMotoristaId())) {
            Motorista motorista = motoristaRepository.findById(dto.getMotoristaId())
                    .orElseThrow(() -> new RuntimeException("Motorista não encontrado"));
            viagem.setMotorista(motorista);
        }

        if (!viagem.getRotaIda().getId().equals(dto.getRotaIdaId())) {
            Rota rotaIda = rotaRepository.findById(dto.getRotaIdaId())
                    .orElseThrow(() -> new RuntimeException("Rota de ida não encontrada"));
            viagem.setRotaIda(rotaIda);
        }

        if (dto.getRotaVoltaId() != null) {
            Rota rotaVolta = rotaRepository.findById(dto.getRotaVoltaId())
                    .orElseThrow(() -> new RuntimeException("Rota de volta não encontrada"));
            viagem.setRotaVolta(rotaVolta);
        } else {
            viagem.setRotaVolta(null);
        }

        if (dto.getAbastecimentoId() != null) {
            Abastecimento abastecimento = abastecimentoRepository.findById(dto.getAbastecimentoId())
                    .orElseThrow(() -> new RuntimeException("Abastecimento não encontrado"));
            viagem.setAbastecimento(abastecimento);
        } else {
            viagem.setAbastecimento(null);
        }

        viagem.setDataViagem(dto.getDataViagem());
        viagem.setStatus(dto.getStatus());
        viagem.setStatusViagem(dto.getStatusViagem());

        // Dados da ida
        viagem.setQuantidadeToneladasIda(dto.getQuantidadeToneladasIda());
        viagem.setMaterialIda(dto.getMaterialIda());
        viagem.setValorToneladaBrutaIda(dto.getValorToneladaBrutaIda());
        viagem.setImpostoBaseIda(dto.getImpostoBaseIda());
        viagem.setQuantidadePedagiosIda(dto.getQuantidadePedagiosIda());
        viagem.setValorPedagioPorEixoIda(dto.getValorPedagioPorEixoIda());
        viagem.setValorTotalBrutoIda(dto.getValorTotalBrutoIda());

        // Dados da volta
        viagem.setQuantidadeToneladasVolta(dto.getQuantidadeToneladasVolta());
        viagem.setMaterialVolta(dto.getMaterialVolta());
        viagem.setValorToneladaBrutaVolta(dto.getValorToneladaBrutaVolta());
        viagem.setImpostoBaseVolta(dto.getImpostoBaseVolta());
        viagem.setQuantidadePedagiosVolta(dto.getQuantidadePedagiosVolta());
        viagem.setValorPedagioPorEixoVolta(dto.getValorPedagioPorEixoVolta());
        viagem.setValorTotalBrutoVolta(dto.getValorTotalBrutoVolta());

        // Combustível
        viagem.setMediaVeiculoKmL(dto.getMediaVeiculoKmL());
        viagem.setFatorCarga(dto.getFatorCarga());
        viagem.setConsumoEstimadoLitros(dto.getConsumoEstimadoLitros());
        viagem.setValorTotalCombustivel(dto.getValorTotalCombustivel());

        // Resultado final
        viagem.setValorTotalBrutoViagem(dto.getValorTotalBrutoViagem());
        viagem.setValorTotalPedagios(dto.getValorTotalPedagios());
        viagem.setComissaoMotorista(dto.getComissaoMotorista());
        viagem.setValorTotalCustos(dto.getValorTotalCustos());
        viagem.setValorTotalLiquido(dto.getValorTotalLiquido());
        viagem.setMargemPercentual(dto.getMargemPercentual());

        // Consumo Detalhado - Atualizar ou criar novos
        if (dto.getConsumoDetalhadoIda() != null) {
            if (viagem.getConsumoDetalhadoIda() != null) {
                updateConsumoDetalhadoEntity(viagem.getConsumoDetalhadoIda(), dto.getConsumoDetalhadoIda());
            } else {
                viagem.setConsumoDetalhadoIda(toConsumoDetalhadoEntity(dto.getConsumoDetalhadoIda()));
            }
        } else {
            viagem.setConsumoDetalhadoIda(null);
        }

        if (dto.getConsumoDetalhadoVolta() != null) {
            if (viagem.getConsumoDetalhadoVolta() != null) {
                updateConsumoDetalhadoEntity(viagem.getConsumoDetalhadoVolta(), dto.getConsumoDetalhadoVolta());
            } else {
                viagem.setConsumoDetalhadoVolta(toConsumoDetalhadoEntity(dto.getConsumoDetalhadoVolta()));
            }
        } else {
            viagem.setConsumoDetalhadoVolta(null);
        }
    }

    /**
     * Atualiza entidade ConsumoDetalhado com dados do DTO
     */
    private void updateConsumoDetalhadoEntity(ConsumoDetalhado entity, ConsumoDetalhadoDTO dto) {
        entity.setConsumoTotal(dto.getConsumoTotal());
        entity.setFatorTerreno(dto.getFatorTerreno());
        entity.setClassificacao(dto.getClassificacao());

        entity.setConsumoSubida(dto.getConsumoSubida());
        entity.setConsumoDescida(dto.getConsumoDescida());
        entity.setConsumoPlano(dto.getConsumoPlano());

        entity.setMediaSubidaLKm(dto.getMediaSubidaLKm());
        entity.setMediaDescidaLKm(dto.getMediaDescidaLKm());
        entity.setMediaPlanoLKm(dto.getMediaPlanoLKm());
        entity.setMediaTotalLKm(dto.getMediaTotalLKm());

        entity.setMediaSubidaKmL(dto.getMediaSubidaKmL());
        entity.setMediaDescidaKmL(dto.getMediaDescidaKmL());
        entity.setMediaPlanoKmL(dto.getMediaPlanoKmL());
        entity.setMediaTotalKmL(dto.getMediaTotalKmL());

        entity.setElevacaoPor100km(dto.getElevacaoPor100km());
        entity.setInclinacaoMediaSubida(dto.getInclinacaoMediaSubida());
        entity.setInclinacaoMediaDescida(dto.getInclinacaoMediaDescida());

        entity.setElevacaoSubida(dto.getElevacaoSubida());
        entity.setElevacaoDescida(dto.getElevacaoDescida());
        entity.setDistanciaSubida(dto.getDistanciaSubida());
        entity.setDistanciaDescida(dto.getDistanciaDescida());
        entity.setDistanciaPlano(dto.getDistanciaPlano());
        entity.setDistanciaTotal(dto.getDistanciaTotal());
    }

    private ConsumoDetalhadoDTO calcularFatorTerreno(Rota rota, Veiculo veiculo, Double quantidadeToneladas, ViagemDTO dto){

        double GRAVIDADE = 9.8;            // m/s²
        double EFICIENCIA_MOTOR = 0.45;    // 45% eficiência térmica diesel
        double ENERGIA_DIESEL = 36_000_000; // Joules por litro
        double FATOR_CONSUMO_DESCIDA  = 0.25; // 25% energia recuperada no freio motor

        double massaVeiculoKg = ((veiculo.getTara().doubleValue() + quantidadeToneladas) * 1000); //converter tn em kg
        double elevationGain = rota.getEstatisticas().getGanhoTotalElevacao();
        double elevationLoss = rota.getEstatisticas().getPerdaTotalElevacao();
        double distanciaSubidaKm = (rota.getEstatisticas().getDistanciaSubida() / 1000);
        double distanciaDescidaKm = (rota.getEstatisticas().getDistanciaDescida() / 1000);
        double distanciaPlanaKm = (rota.getEstatisticas().getDistanciaPlano() / 1000);
        double consumoBaseKmL = dto.getMediaVeiculoKmL();

        double energiaUtilPorLitro = ENERGIA_DIESEL * EFICIENCIA_MOTOR;
        double distanciaTotalKm = distanciaSubidaKm + distanciaDescidaKm + distanciaPlanaKm;

        // === SUBIDA ===
        double consumoBaseSubida = distanciaSubidaKm / consumoBaseKmL;
        double energiaSubida = massaVeiculoKg * GRAVIDADE * elevationGain;
        double litrosExtrasSubida = energiaSubida / energiaUtilPorLitro;
        double consumoSubida = consumoBaseSubida + litrosExtrasSubida;

        // === DESCIDA ===
        double consumoBaseDescida = distanciaDescidaKm / consumoBaseKmL;
        double energiaGravidadeDescida = massaVeiculoKg * GRAVIDADE * elevationLoss;
        double energiaResistencias = consumoBaseDescida * energiaUtilPorLitro;

        double consumoDescida;
        if (energiaGravidadeDescida >= energiaResistencias) {
            // Gravidade suficiente para "empurrar" - freio motor ativo
            consumoDescida = consumoBaseDescida * FATOR_CONSUMO_DESCIDA;
        } else {
            // Gravidade ajuda mas não é suficiente
            double litrosEconomizados = energiaGravidadeDescida / energiaUtilPorLitro;
            consumoDescida = Math.max(consumoBaseDescida - litrosEconomizados,
                    consumoBaseDescida * FATOR_CONSUMO_DESCIDA);
        }

        // === PLANO ===
        double consumoPlano = distanciaPlanaKm / consumoBaseKmL;

        // === TOTAIS ===
        double consumoTotal = consumoSubida + consumoDescida + consumoPlano;
        double consumoBaseTeorico = distanciaTotalKm / consumoBaseKmL;
        double fatorTerreno = consumoTotal / consumoBaseTeorico;
        fatorTerreno = Math.max(1.00, Math.min(1.50, fatorTerreno));

        // === MÉDIAS ===
        double mediaSubidaLKm = consumoSubida / distanciaSubidaKm;
        double mediaDescidaLKm = consumoDescida / distanciaDescidaKm;
        double mediaPlanoLKm = consumoPlano / distanciaPlanaKm;
        double mediaTotalLKm = consumoTotal / distanciaTotalKm;

        // === CLASSIFICAÇÃO ===
        double elevacaoPor100km = (elevationGain / distanciaTotalKm) * 100;
        String classificacao = classificarTerreno(elevacaoPor100km);

        // === MONTAR RESULTADO ===
        ConsumoDetalhadoDTO resultado = new ConsumoDetalhadoDTO();

        resultado.setConsumoTotal(toBigDecimal(consumoTotal));
        resultado.setFatorTerreno(toBigDecimal(fatorTerreno));
        resultado.setClassificacao(classificacao);

        resultado.setConsumoSubida(toBigDecimal(consumoSubida));
        resultado.setConsumoDescida(toBigDecimal(consumoDescida));
        resultado.setConsumoPlano(toBigDecimal(consumoPlano));

        resultado.setMediaSubidaLKm(toBigDecimal(mediaSubidaLKm));
        resultado.setMediaDescidaLKm(toBigDecimal(mediaDescidaLKm));
        resultado.setMediaPlanoLKm(toBigDecimal(mediaPlanoLKm));
        resultado.setMediaTotalLKm(toBigDecimal(mediaTotalLKm));

        resultado.setMediaSubidaKmL(toBigDecimal(1 / mediaSubidaLKm));
        resultado.setMediaDescidaKmL(toBigDecimal(1 / mediaDescidaLKm));
        resultado.setMediaPlanoKmL(toBigDecimal(1 / mediaPlanoLKm));
        resultado.setMediaTotalKmL(toBigDecimal(1 / mediaTotalLKm));

        resultado.setElevacaoPor100km(elevacaoPor100km);
        resultado.setInclinacaoMediaSubida((elevationGain / (distanciaSubidaKm * 1000)) * 100);
        resultado.setInclinacaoMediaDescida((elevationLoss / (distanciaDescidaKm * 1000)) * 100);

        resultado.setDistanciaSubida(distanciaSubidaKm);
        resultado.setDistanciaDescida(distanciaDescidaKm);
        resultado.setDistanciaPlano(distanciaPlanaKm);
        resultado.setDistanciaTotal(distanciaSubidaKm + distanciaDescidaKm + distanciaPlanaKm);

        resultado.setElevacaoSubida(elevationGain);
        resultado.setElevacaoDescida(elevationLoss);

        return resultado;

    }

    private String classificarTerreno(double elevacaoPor100km) {
        if (elevacaoPor100km < 100) return "Plano";
        if (elevacaoPor100km < 200) return "Levemente ondulado";
        if (elevacaoPor100km < 300) return "Ondulado";
        if (elevacaoPor100km < 400) return "Bastante ondulado";
        if (elevacaoPor100km < 500) return "Montanhoso";
        return "Muito montanhoso";
    }

    private  BigDecimal toBigDecimal(double valor) {
        return new BigDecimal(valor).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Converte ConsumoDetalhadoDTO para ConsumoDetalhado entity
     */
    private ConsumoDetalhado toConsumoDetalhadoEntity(ConsumoDetalhadoDTO dto) {
        if (dto == null) {
            return null;
        }

        ConsumoDetalhado entity = new ConsumoDetalhado();
        entity.setConsumoTotal(dto.getConsumoTotal());
        entity.setFatorTerreno(dto.getFatorTerreno());
        entity.setClassificacao(dto.getClassificacao());

        entity.setConsumoSubida(dto.getConsumoSubida());
        entity.setConsumoDescida(dto.getConsumoDescida());
        entity.setConsumoPlano(dto.getConsumoPlano());

        entity.setMediaSubidaLKm(dto.getMediaSubidaLKm());
        entity.setMediaDescidaLKm(dto.getMediaDescidaLKm());
        entity.setMediaPlanoLKm(dto.getMediaPlanoLKm());
        entity.setMediaTotalLKm(dto.getMediaTotalLKm());

        entity.setMediaSubidaKmL(dto.getMediaSubidaKmL());
        entity.setMediaDescidaKmL(dto.getMediaDescidaKmL());
        entity.setMediaPlanoKmL(dto.getMediaPlanoKmL());
        entity.setMediaTotalKmL(dto.getMediaTotalKmL());

        entity.setElevacaoPor100km(dto.getElevacaoPor100km());
        entity.setInclinacaoMediaSubida(dto.getInclinacaoMediaSubida());
        entity.setInclinacaoMediaDescida(dto.getInclinacaoMediaDescida());

        entity.setElevacaoSubida(dto.getElevacaoSubida());
        entity.setElevacaoDescida(dto.getElevacaoDescida());
        entity.setDistanciaSubida(dto.getDistanciaSubida());
        entity.setDistanciaDescida(dto.getDistanciaDescida());
        entity.setDistanciaPlano(dto.getDistanciaPlano());
        entity.setDistanciaTotal(dto.getDistanciaTotal());

        return entity;
    }

    /**
     * Converte ConsumoDetalhado entity para ConsumoDetalhadoDTO
     */
    private ConsumoDetalhadoDTO toConsumoDetalhadoDTO(ConsumoDetalhado entity) {
        if (entity == null) {
            return null;
        }

        ConsumoDetalhadoDTO dto = new ConsumoDetalhadoDTO();
        dto.setConsumoTotal(entity.getConsumoTotal());
        dto.setFatorTerreno(entity.getFatorTerreno());
        dto.setClassificacao(entity.getClassificacao());

        dto.setConsumoSubida(entity.getConsumoSubida());
        dto.setConsumoDescida(entity.getConsumoDescida());
        dto.setConsumoPlano(entity.getConsumoPlano());

        dto.setMediaSubidaLKm(entity.getMediaSubidaLKm());
        dto.setMediaDescidaLKm(entity.getMediaDescidaLKm());
        dto.setMediaPlanoLKm(entity.getMediaPlanoLKm());
        dto.setMediaTotalLKm(entity.getMediaTotalLKm());

        dto.setMediaSubidaKmL(entity.getMediaSubidaKmL());
        dto.setMediaDescidaKmL(entity.getMediaDescidaKmL());
        dto.setMediaPlanoKmL(entity.getMediaPlanoKmL());
        dto.setMediaTotalKmL(entity.getMediaTotalKmL());

        dto.setElevacaoPor100km(entity.getElevacaoPor100km());
        dto.setInclinacaoMediaSubida(entity.getInclinacaoMediaSubida());
        dto.setInclinacaoMediaDescida(entity.getInclinacaoMediaDescida());

        dto.setElevacaoSubida(entity.getElevacaoSubida());
        dto.setElevacaoDescida(entity.getElevacaoDescida());
        dto.setDistanciaSubida(entity.getDistanciaSubida());
        dto.setDistanciaDescida(entity.getDistanciaDescida());
        dto.setDistanciaPlano(entity.getDistanciaPlano());
        dto.setDistanciaTotal(entity.getDistanciaTotal());

        return dto;
    }

    private void criarComissaoMotorista(Viagem viagem, BigDecimal comissaoMotorista){
        try {
            ComissaoMotoristaDTO comissaoDTO = new ComissaoMotoristaDTO();
            comissaoDTO.setMotoristaId(viagem.getMotorista().getId());
            comissaoDTO.setViagemId(viagem.getId());
            comissaoDTO.setClienteId(viagem.getCliente().getId());
            comissaoDTO.setPercentualComissao(BigDecimal.valueOf(viagem.getMotorista().getComissao()));
            comissaoDTO.setValorComissao(comissaoMotorista);
            comissaoDTO.setOrigem(viagem.getRotaIda().getNome());
            if (Objects.nonNull(viagem.getRotaVolta())) {
                comissaoDTO.setDestino(viagem.getRotaVolta().getNome());
            }
            comissaoMotoristaService.create(comissaoDTO);
        }catch (Exception e){
            log.error("criarComissaoMotorista",e);
        }

    }

    private void editarComissaoMotorista(Viagem viagem, BigDecimal comissaoMotorista){
        try {
            ComissaoMotoristaDTO comissaoDTO= comissaoMotoristaService.findByIdViagem(viagem.getId());
            comissaoDTO.setValorComissao(comissaoMotorista);
            comissaoDTO.setOrigem(viagem.getRotaIda().getNome());
            if (Objects.nonNull(viagem.getRotaVolta())) {
                comissaoDTO.setDestino(viagem.getRotaVolta().getNome());
            }
            comissaoMotoristaService.update(comissaoDTO.getId(), comissaoDTO);
        }catch (Exception e){
            log.error("editarComissaoMotorista",e);
        }

    }
}
