package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "motorista_id")
    private Motorista motorista;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 1000)
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusNotificacao status = StatusNotificacao.PENDENTE;

    @Column(name = "data_criacao")
    private LocalDateTime dataCriacao;

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "tentativas_envio")
    private Integer tentativasEnvio = 0;

    @Column(name = "erro_envio", length = 500)
    private String erroEnvio;

    @Column(name = "resposta_motorista", length = 1000)
    private String respostaMotorista;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    // Referência para entidades do Trovian (opcional)
    @Column(name = "referencia_tipo")
    private String referenciaTipo; // Ex: "MANUTENCAO", "CHECKLIST", "FINANCEIRO"

    @Column(name = "referencia_id")
    private Long referenciaId;

    @PrePersist
    protected void onCreate() {
        dataCriacao = LocalDateTime.now();
    }

    public enum TipoNotificacao {
        MANUTENCAO_VENCIDA("Manutenção Vencida"),
        MANUTENCAO_PROXIMA("Manutenção Próxima"),
        CHECKLIST_PENDENTE("Checklist Pendente"),
        FINANCEIRO_PENDENTE("Pendência Financeira"),
        ALERTA_GERAL("Alerta Geral"),
        CONFIRMACAO("Confirmação"),
        LEMBRETE("Lembrete"),
        MENSAGEM_BEM_VINDO("Bem vindo");

        private final String descricao;

        TipoNotificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }

    public enum StatusNotificacao {
        PENDENTE("Pendente"),
        ENVIANDO("Enviando"),
        ENVIADA("Enviada"),
        LIDA("Lida"),
        RESPONDIDA("Respondida"),
        ERRO("Erro"),
        CANCELADA("Cancelada");

        private final String descricao;

        StatusNotificacao(String descricao) {
            this.descricao = descricao;
        }

        public String getDescricao() {
            return descricao;
        }
    }
}
