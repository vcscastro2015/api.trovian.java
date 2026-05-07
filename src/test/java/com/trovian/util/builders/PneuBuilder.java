package com.trovian.util.builders;

import com.trovian.entity.Cliente;
import com.trovian.entity.Pneu;
import com.trovian.enums.StatusPneu;
import com.trovian.enums.TipoEixo;
import com.trovian.enums.TipoPneu;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PneuBuilder {

    private Long id = 1L;
    private String numeroDot = "DOT HJ8X RWLX 2423";
    private String marca = "Michelin";
    private String modelo = "XZE2+";
    private String dimensao = "275/80R22.5";
    private TipoPneu tipoPneu = TipoPneu.BORRACHUDO;
    private TipoEixo eixo = TipoEixo.TRACAO;
    private LocalDate dataFabricacao = LocalDate.now().minusYears(2);
    private LocalDate dataCompra = LocalDate.now().minusYears(1);
    private BigDecimal valorCompra = new BigDecimal("1200.00");
    private BigDecimal profundidadeInicial = new BigDecimal("18.0");
    private BigDecimal profundidadeMinima = new BigDecimal("1.6");
    private Integer kmLimite = 120000;
    private StatusPneu status = StatusPneu.NOVO;
    private Integer numeroRecapagens = 0;
    private Integer kmAcumulado = 0;
    private Cliente cliente = null;
    private LocalDateTime dataCadastro = LocalDateTime.now();
    private LocalDateTime updatedAt = LocalDateTime.now();

    public static PneuBuilder umPneu() {
        return new PneuBuilder();
    }

    public PneuBuilder comId(Long id) {
        this.id = id;
        return this;
    }

    public PneuBuilder comDot(String numeroDot) {
        this.numeroDot = numeroDot;
        return this;
    }

    public PneuBuilder comMarca(String marca) {
        this.marca = marca;
        return this;
    }

    public PneuBuilder comStatus(StatusPneu status) {
        this.status = status;
        return this;
    }

    public PneuBuilder comNumeroRecapagens(Integer numeroRecapagens) {
        this.numeroRecapagens = numeroRecapagens;
        return this;
    }

    public PneuBuilder comKmAcumulado(Integer kmAcumulado) {
        this.kmAcumulado = kmAcumulado;
        return this;
    }

    public PneuBuilder comKmLimite(Integer kmLimite) {
        this.kmLimite = kmLimite;
        return this;
    }

    public PneuBuilder comValorCompra(BigDecimal valorCompra) {
        this.valorCompra = valorCompra;
        return this;
    }

    public PneuBuilder comDataFabricacao(LocalDate dataFabricacao) {
        this.dataFabricacao = dataFabricacao;
        return this;
    }

    public PneuBuilder comCliente(Cliente cliente) {
        this.cliente = cliente;
        return this;
    }

    public Pneu build() {
        Pneu pneu = new Pneu();
        pneu.setId(id);
        pneu.setNumeroDot(numeroDot);
        pneu.setMarca(marca);
        pneu.setModelo(modelo);
        pneu.setDimensao(dimensao);
        pneu.setTipoPneu(tipoPneu);
        pneu.setEixo(eixo);
        pneu.setDataFabricacao(dataFabricacao);
        pneu.setDataCompra(dataCompra);
        pneu.setValorCompra(valorCompra);
        pneu.setProfundidadeInicial(profundidadeInicial);
        pneu.setProfundidadeMinima(profundidadeMinima);
        pneu.setKmLimite(kmLimite);
        pneu.setStatus(status);
        pneu.setNumeroRecapagens(numeroRecapagens);
        pneu.setKmAcumulado(kmAcumulado);
        pneu.setCliente(cliente);
        pneu.setDataCadastro(dataCadastro);
        pneu.setUpdatedAt(updatedAt);
        return pneu;
    }
}
