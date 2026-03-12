package com.trovian.entity;

import com.trovian.converter.Int2ToBooleanConverter;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Table(name = "transmissao_basica")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransmissaoBasica {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "altitude")
    private Integer altitude;

    @Basic(optional = false)
    @Column(name = "data_transmissao")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataTransmissao;

    @Column(name = "direcao")
    private Integer direcao;

    @Column(name = "execesso_velocidade")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean execessoVelocidade;

    @Column(name = "gprs_ativo")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean gprsAtivo;

    @Column(name = "gps_ativo")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean gpsAtivo;

    @Column(name = "hodometro")
    private Double hodometro;

    @Column(name = "horimetro")
    private Double horimetro;

    @Column(name = "ignicao_ativa")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean ignicaoAtiva;

    @Basic(optional = false)
    @Column(name = "latitude")
    private double latitude;

    @Basic(optional = false)
    @Column(name = "longitude")
    private double longitude;

    @Column(name = "panico")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean panico;

    @Column(name = "percentual_bateria_equipamento")
    private Integer percentualBateriaEquipamento;

    @Column(name = "ponto_de_referencia")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean pontoDeReferencia;

    @Column(name = "saida4")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean saida4;

    @Column(name = "temperatura")
    private Double temperatura;

    @Column(name = "velocidade")
    private Double velocidade;

    @Column(name = "voltagem")
    private Double voltagem;

    @Column(name = "jamming")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean jamming;

    @Column(name = "sinal_jamming")
    private String sinalJamming;

    @Column(name = "falha_alimentacao_externa")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean falhaAlimentacaoExterna;

    @Column(name = "panico_visualizado")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean panicoVisualizado;

    @Column(name = "panico_desativado")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean panicoDesativado;

    @Column(name = "conexao_primaria_ativa")
    @Convert(converter = Int2ToBooleanConverter.class)
    private Boolean conexaoPrimariaAtiva;

    @Column(name = "identificador_de_evento")
    private Integer identificadorDeEvento;

    @Column(name = "data_envio_sms")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataEnvioSms;

    @Column(name = "entrada_digital_um")
    private Boolean entradaDigitalUm;

    @Column(name = "ultimo_envio_sms")
    private Boolean ultimoEnvioSms;

    @Column(name = "alerta")
    private Boolean alerta;

    @Column(name = "distancia_percorrida")
    private Double distanciaPercorrida;

    @Column(name = "evento")
    private Boolean evento;

    @Column(name = "ibutton")
    private String ibutton;

    @Column(name = "modo")
    private Integer modo;

    @Column(name = "rpm_corrente")
    private Double rpmCorrente;

    @Column(name = "tipo_alerta")
    private Integer tipoAlerta;

    @Column(name = "tipo_evento")
    private Integer tipoEvento;

    @Column(name = "viagem")
    private Boolean viagem;

    @Column(name = "data_registro")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dataRegistro;

    @Column(name = "id_transmissao_pai")
    private Double idTransmissaoPai;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "estado")
    private String estado;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "veiculo", referencedColumnName = "id")
    private Veiculo veiculo;
}
