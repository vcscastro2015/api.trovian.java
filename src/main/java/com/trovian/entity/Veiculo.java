package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "veiculo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Veiculo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Size(min = 1, max = 5, message = "Ano de fabricação deve ter entre 1 e 5 caracteres")
    @Column(name = "ano_fabricacao", length = 5)
    private String anoFabricacao;

    @Size(min = 1, max = 5, message = "Ano do modelo deve ter entre 1 e 5 caracteres")
    @Column(name = "ano_modelo", length = 5)
    private String anoModelo;

    @Size(min = 1, max = 20, message = "Chassi deve ter entre 1 e 20 caracteres")
    @Column(name = "chassi", length = 20)
    private String chassi;

    @Size(min = 1, max = 15, message = "Cor deve ter entre 1 e 15 caracteres")
    @Column(name = "cor", length = 15)
    private String cor;

    @NotNull(message = "Data de cadastro é obrigatória")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro", nullable = false, updatable = false)
    private Date dataCadastro;

    @Column(name = "observacao", columnDefinition = "TEXT")
    private String observacao;

    @NotNull(message = "Placa é obrigatória")
    @Size(min = 1, max = 15, message = "Placa deve ter entre 1 e 15 caracteres")
    @Column(name = "placa", nullable = false, length = 15)
    private String placa;

    @Column(name = "velocidade_maxima")
    private Integer velocidadeMaxima;

    @Column(name = "velocidade_maxima_chuva")
    private Integer velocidadeMaximaChuva;

    @Column(name = "velocidade_maxima_desaceleracao")
    private Integer velocidadeMaximaDesaceleracao;

    @Column(name = "velocidade_maxima_curva")
    private Integer velocidadeMaximaCurva;

    @Size(min = 1, max = 255, message = "Renavam deve ter entre 1 e 255 caracteres")
    @Column(name = "renavam", length = 255)
    private String renavam;

    @NotNull(message = "Status é obrigatório")
    @Column(name = "status", nullable = false)
    private Boolean status;

    @NotNull(message = "Tipo é obrigatório")
    @Size(min = 1, max = 20, message = "Tipo deve ter entre 1 e 20 caracteres")
    @Column(name = "tipo", nullable = false, length = 20)
    private String tipo; // Moto, Carro, Onibus, Caminhao, Carreta, Implemento

    @Column(name = "capacidade_maxima_tracao")
    private Double capacidadeMaximaTracao;

    @Column(name = "usa_entrada_digital_um")
    private Boolean usaEntradaDigitalUm;

    @Column(name = "usa_entrada_digital_dois")
    private Boolean usaEntradaDigitalDois;

    @Column(name = "usa_entrada_digital_tres")
    private Boolean usaEntradaDigitalTres;

    @Column(name = "usa_entrada_digital_quatro")
    private Boolean usaEntradaDigitalQuatro;

    @Column(name = "excesso_velocidade")
    private Boolean excessoVelocidade;

    @Column(name = "bateria_carro_baixa")
    private Boolean bateriaCarroBaixa;

    @Column(name = "falta_energia_principal")
    private Boolean faltaEnergiaPrincipal;

    @Column(name = "quantidade_dias_sem_trasmissao")
    private Boolean quantidadeDiasSemTrasmissao;

    @Column(name = "sem_comunicacao")
    private Boolean semComunicacao;

    @Column(name = "ativa_rota_no_mapa")
    private Boolean ativaRotaNoMapa;

    @Column(name = "ativa_validacao_cerca")
    private Boolean ativaValidacaoDeCerca;

    @Column(name = "troca_de_horimetro")
    private Boolean trocaDeHorimetro;

    @Column(name = "rpm_modo_eco_minimo")
    private Integer rpmModoEconomicoMinimo;

    @Column(name = "rpm_modo_eco_maximo")
    private Integer rpmModoEconomicoMaximo;

    @Column(name = "rpm_maximo")
    private Integer rpmMaximo;

    @Column(name = "rpm_inicio_faixa_azul")
    private Integer rpmInicioFaixaAzul;

    @Column(name = "rpm_fim_faixa_azul")
    private Integer rpmFimFaixaAzul;

    @Column(name = "rpm_inicio_faixa_economica")
    private Integer rpmInicioFaixaEconomica;

    @Column(name = "rpm_fim_faixa_economica")
    private Integer rpmFimFaixaEconomica;

    @Column(name = "rpm_inicio_faixa_verde")
    private Integer rpmInicioFaixaVerde;

    @Column(name = "rpm_fim_faixa_verde")
    private Integer rpmFimFaixaVerde;

    @Column(name = "rpm_inicio_faixa_amarela")
    private Integer rpmInicioFaixaAmarela;

    @Column(name = "rpm_fim_faixa_amarela")
    private Integer rpmFimFaixaAmarela;

    @Column(name = "rpm_inicio_marcha_lenta")
    private Integer rpmInicioMarchaLenta;

    @Column(name = "rpm_fim_marcha_lenta")
    private Integer rpmFimMarchaLenta;

    @Column(name = "gera_endereco")
    private Boolean geraEnderecoAutomatico;

    @Column(name = "validar_ibotton")
    private Boolean validarIbutton;

    @Column(name = "carga_maxima")
    private Double cargaMaxima;

    @Column(name = "capacidade_tanque", precision = 10, scale = 2)
    private BigDecimal capacidadeTanque;

    @Column(name = "numero_eixos")
    private Integer numeroEixos;

    @Column(name = "tara", precision = 10, scale = 2)
    private BigDecimal tara;

    @Column(name = "combustivel", length = 10)
    private String combustivel; // Gasolina, Alcool, Diesel

    @Column(name = "validar_rota")
    private Boolean validarRota;

    @NotNull(message = "Modelo é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "modelo", referencedColumnName = "id", nullable = false)
    private Modelo modelo;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "equipamento", referencedColumnName = "id")
    private Equipamento equipamento;

    @NotNull(message = "Cliente é obrigatório")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente", referencedColumnName = "id", nullable = false)
    private Cliente cliente;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
