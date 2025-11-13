package com.trovian.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Entity que representa uma rota completa
 */
@Entity
@Table(name = "rota")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rota {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 255, message = "Nome deve ter no máximo 255 caracteres")
    @Column(nullable = false, length = 255)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @NotNull(message = "Status ativo é obrigatório")
    @Column(nullable = false)
    private Boolean ativa = true;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    private Date dataCadastro;

    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @NotNull(message = "Distância total é obrigatória")
    @Column(name = "distancia_total", nullable = false)
    private Double distanciaTotal; // metros

    // Relacionamentos opcionais
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cooperativa_id")
    private Cooperativa cooperativa;

    // GeoJSON para visualização rápida
    @Column(name = "geo_json", columnDefinition = "TEXT")
    private String geoJson;

    // Relacionamentos OneToMany e OneToOne
    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequencia ASC")
    private List<PontoRota> pontos = new ArrayList<>();

    @OneToOne(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    private RotaEstatisticas estatisticas;

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sequencia ASC")
    private List<RotaSegmento> segmentos = new ArrayList<>();

    @OneToMany(mappedBy = "rota", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RotaPedagio> pedagios = new ArrayList<>();

    // Métodos auxiliares para gerenciar relacionamentos bidirecionais
    public void addPonto(PontoRota ponto) {
        pontos.add(ponto);
        ponto.setRota(this);
    }

    public void removePonto(PontoRota ponto) {
        pontos.remove(ponto);
        ponto.setRota(null);
    }

    public void addSegmento(RotaSegmento segmento) {
        segmentos.add(segmento);
        segmento.setRota(this);
    }

    public void removeSegmento(RotaSegmento segmento) {
        segmentos.remove(segmento);
        segmento.setRota(null);
    }

    public void addPedagio(RotaPedagio pedagio) {
        pedagios.add(pedagio);
        pedagio.setRota(this);
    }

    public void removePedagio(RotaPedagio pedagio) {
        pedagios.remove(pedagio);
        pedagio.setRota(null);
    }

    public void setEstatisticas(RotaEstatisticas estatisticas) {
        this.estatisticas = estatisticas;
        if (estatisticas != null) {
            estatisticas.setRota(this);
        }
    }

    // Lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
        dataAtualizacao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        dataAtualizacao = LocalDateTime.now();
    }
}
