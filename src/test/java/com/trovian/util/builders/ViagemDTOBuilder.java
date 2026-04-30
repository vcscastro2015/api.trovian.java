package com.trovian.util.builders;

import com.trovian.dto.ViagemDTO;
import com.trovian.enums.StatusViagem;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Builder de dados de teste para ViagemDTO.
 * Fornece defaults válidos para que ViagemService.calcular() execute sem erros.
 */
public class ViagemDTOBuilder {

    private Long id = null;
    private Long clienteId = 1L;
    private Long veiculoId = 1L;
    private Long motoristaId = 1L;
    private Long rotaIdaId = 1L;
    private Long rotaVoltaId = null;
    private Long abastecimentoId = null;

    // Dados básicos
    private Date dataViagem = new Date();
    private StatusViagem statusViagem = StatusViagem.ABERTA;
    private Boolean status = true;
    private Boolean andarVazioVolta = false;

    // Dados da ida
    private Double quantidadeToneladasIda = 25.0;
    private BigDecimal valorToneladaBrutaIda = BigDecimal.valueOf(200.00);
    private BigDecimal impostoBaseIda = BigDecimal.valueOf(5.0);  // 5%
    private Integer quantidadePedagiosIda = 3;
    private BigDecimal valorPedagioPorEixoIda = BigDecimal.valueOf(12.50);

    // Combustível
    private Double mediaVeiculoKmL = 2.5;

    public static ViagemDTOBuilder umaViagem() {
        return new ViagemDTOBuilder();
    }

    public ViagemDTOBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public ViagemDTOBuilder comClienteId(Long clienteId) {
        this.clienteId = clienteId;
        return this;
    }

    public ViagemDTOBuilder comVeiculoId(Long veiculoId) {
        this.veiculoId = veiculoId;
        return this;
    }

    public ViagemDTOBuilder comMotoristaId(Long motoristaId) {
        this.motoristaId = motoristaId;
        return this;
    }

    public ViagemDTOBuilder comRotaIdaId(Long rotaIdaId) {
        this.rotaIdaId = rotaIdaId;
        return this;
    }

    public ViagemDTOBuilder comRotaVoltaId(Long rotaVoltaId) {
        this.rotaVoltaId = rotaVoltaId;
        return this;
    }

    public ViagemDTOBuilder comQuantidadeToneladasIda(Double quantidade) {
        this.quantidadeToneladasIda = quantidade;
        return this;
    }

    public ViagemDTOBuilder comValorToneladaBrutaIda(BigDecimal valor) {
        this.valorToneladaBrutaIda = valor;
        return this;
    }

    public ViagemDTOBuilder comImpostoBaseIda(BigDecimal imposto) {
        this.impostoBaseIda = imposto;
        return this;
    }

    public ViagemDTOBuilder comStatusViagem(StatusViagem statusViagem) {
        this.statusViagem = statusViagem;
        return this;
    }

    public ViagemDTO build() {
        ViagemDTO dto = new ViagemDTO();
        dto.setId(id);
        dto.setClienteId(clienteId);
        dto.setVeiculoId(veiculoId);
        dto.setMotoristaId(motoristaId);
        dto.setRotaIdaId(rotaIdaId);
        dto.setRotaVoltaId(rotaVoltaId);
        dto.setAbastecimentoId(abastecimentoId);
        dto.setDataViagem(dataViagem);
        dto.setStatusViagem(statusViagem);
        dto.setStatus(status);
        dto.setAndarVazioVolta(andarVazioVolta);
        dto.setQuantidadeToneladasIda(quantidadeToneladasIda);
        dto.setValorToneladaBrutaIda(valorToneladaBrutaIda);
        dto.setImpostoBaseIda(impostoBaseIda);
        dto.setQuantidadePedagiosIda(quantidadePedagiosIda);
        dto.setValorPedagioPorEixoIda(valorPedagioPorEixoIda);
        dto.setMediaVeiculoKmL(mediaVeiculoKmL);
        return dto;
    }
}
