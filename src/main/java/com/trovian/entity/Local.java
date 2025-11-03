package com.trovian.entity;

import com.trovian.enums.FuncaoLocal;
import com.trovian.enums.TipoLocal;
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

@Entity
@Table(name = "local")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Local {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Basic(optional = false)
    @NotNull
    @NotBlank
    @Size(min = 1, max = 255)
    @Column(name = "nome", nullable = false)
    private String nome;

    @NotNull
    @Column(name = "ativo", nullable = false)
    private Boolean ativo;

    @Column(name = "codigo_unico")
    private Integer codigoUnico;

    @Column(name = "mostrar_mapa_principal")
    private Boolean mostrarNoMapaPrincipal;

    @Column(name = "mostrar_nome_mapa")
    private Boolean mostrarNomeNoMapa;

    @Column(name = "notifica_evento")
    private Boolean notificaEvento;

    @Enumerated(EnumType.STRING)
    @Column(name = "funcao")
    private FuncaoLocal funcao;

    @Column(name = "endereco")
    private String endereco;

    @Column(name = "bairro")
    private String bairro;

    @Column(name = "complemento")
    private String complemento;

    @Column(name = "cidade")
    private String cidade;

    @Column(name = "uf", length = 2)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo")
    private TipoLocal tipo;

    @Column(name = "permite_descanso")
    private Boolean permiteDescanso;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    @NotNull
    private Cliente cliente;

    @OneToMany(mappedBy = "local", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Coordenada> listaDeCoordenadas = new ArrayList<>();

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "parametro_local_id")
    private ParametroLocal parametroLocal;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "data_cadastro")
    private Date dataCadastro;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        dataCadastro = new Date();
        updatedAt = LocalDateTime.now();
        if (ativo == null) {
            ativo = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods para gerenciar coordenadas
    public void addCoordenada(Coordenada coordenada) {
        listaDeCoordenadas.add(coordenada);
        coordenada.setLocal(this);
    }

    public void removeCoordenada(Coordenada coordenada) {
        listaDeCoordenadas.remove(coordenada);
        coordenada.setLocal(null);
    }
}
