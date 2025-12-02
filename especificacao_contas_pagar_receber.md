# Especificação - Módulo Contas a Pagar e Receber
## API Trovian - Sistema de Gestão de Frota

---

## 📋 Visão Geral

O módulo de **Contas a Pagar e Receber** é essencial para a gestão financeira da frota de caminhões, permitindo controle completo sobre despesas operacionais (combustível, manutenção, impostos, etc.) e receitas (fretes, serviços prestados).

---

## 🎯 Objetivos do Módulo

1. **Controle Financeiro Completo**: Gerenciar todas as contas a pagar e receber relacionadas à operação da frota
2. **Fluxo de Caixa**: Acompanhar entradas e saídas financeiras
3. **Categorização**: Classificar despesas e receitas por tipo/categoria
4. **Relacionamentos**: Vincular contas com clientes, veículos, motoristas e fornecedores
5. **Controle de Pagamentos**: Gerenciar status, datas de vencimento e pagamentos parciais
6. **Relatórios Financeiros**: Base para geração de relatórios e análises

---

## 🏗️ Arquitetura - Entidades Principais

### 1. **ContaPagar** (Despesas/Passivo)
### 2. **ContaReceber** (Receitas/Ativo)
### 3. **CategoriaConta** (Classificação de contas)
### 4. **Fornecedor** (Fornecedores de produtos/serviços)
### 5. **FormaPagamento** (Métodos de pagamento)
### 6. **CentroCusto** (Departamentos/Setores)

---

## 📊 Modelo de Dados Detalhado

### 1. Entity: Fornecedor

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "fornecedor")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Fornecedor {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 200)
    private String razaoSocial;
    
    @Column(length = 200)
    private String nomeFantasia;
    
    @Column(unique = true, length = 18)
    private String cnpjCpf; // Pode ser CNPJ (14 dígitos) ou CPF (11 dígitos)
    
    @Column(length = 20)
    private String inscricaoEstadual;
    
    @Column(length = 20)
    private String inscricaoMunicipal;
    
    // Endereço
    @Column(length = 200)
    private String logradouro;
    
    @Column(length = 20)
    private String numero;
    
    @Column(length = 100)
    private String complemento;
    
    @Column(length = 100)
    private String bairro;
    
    @Column(length = 10)
    private String cep;
    
    @Column(length = 100)
    private String cidade;
    
    @Column(length = 2)
    private String uf;
    
    // Contato
    @Column(length = 20)
    private String telefone1;
    
    @Column(length = 20)
    private String telefone2;
    
    @Column(length = 100)
    private String email;
    
    @Column(length = 100)
    private String site;
    
    @Column(length = 100)
    private String contatoPrincipal;
    
    // Informações Bancárias
    @Column(length = 100)
    private String banco;
    
    @Column(length = 20)
    private String agencia;
    
    @Column(length = 30)
    private String conta;
    
    @Column(length = 50)
    private String tipoConta; // Corrente, Poupança, etc.
    
    @Column(length = 100)
    private String chavePix;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private TipoFornecedor tipo; // PRODUTO, SERVICO, COMBUSTIVEL, MANUTENCAO, OUTROS
    
    @Column(columnDefinition = "TEXT")
    private String observacao;
    
    @Column(nullable = false)
    private Boolean status = true;
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Enum: TipoFornecedor
```java
package com.trovian.entity;

public enum TipoFornecedor {
    PRODUTO,
    SERVICO,
    COMBUSTIVEL,
    MANUTENCAO,
    PECAS,
    SEGURO,
    DOCUMENTACAO,
    PEDAGIO,
    OUTROS
}
```

---

### 2. Entity: CategoriaConta

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "categoria_conta")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaConta {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoConta tipo; // PAGAR, RECEBER
    
    // Categoria Pai (para hierarquia)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_pai_id")
    private CategoriaConta categoriaPai;
    
    @Column(length = 50)
    private String codigo; // Código contábil
    
    @Column(nullable = false)
    private Boolean status = true;
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Enum: TipoConta
```java
package com.trovian.entity;

public enum TipoConta {
    PAGAR,
    RECEBER
}
```

---

### 3. Entity: CentroCusto

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "centro_custo")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CentroCusto {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Column(length = 50)
    private String codigo;
    
    // Relacionamento com Cliente (opcional)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    @Column(nullable = false)
    private Boolean status = true;
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

---

### 4. Entity: FormaPagamento

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "forma_pagamento")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FormaPagamento {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String nome;
    
    @Column(length = 500)
    private String descricao;
    
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private TipoFormaPagamento tipo;
    
    @Column
    private Integer prazoMedioDias; // Prazo médio em dias para essa forma de pagamento
    
    @Column(nullable = false)
    private Boolean permiteParcelamento = false;
    
    @Column(nullable = false)
    private Boolean status = true;
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
```

### Enum: TipoFormaPagamento
```java
package com.trovian.entity;

public enum TipoFormaPagamento {
    DINHEIRO,
    PIX,
    TRANSFERENCIA_BANCARIA,
    BOLETO,
    CHEQUE,
    CARTAO_CREDITO,
    CARTAO_DEBITO,
    DEPOSITO,
    OUTROS
}
```

---

### 5. Entity: ContaPagar

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "conta_pagar")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaPagar {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Informações Básicas
    @Column(nullable = false, length = 200)
    private String descricao;
    
    @Column(length = 50)
    private String numeroDocumento; // Número da NF, boleto, etc.
    
    @Column(length = 20)
    private String numeroNotaFiscal;
    
    @Column(length = 50)
    private String numeroControle; // Número interno de controle
    
    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fornecedor_id", nullable = false)
    private Fornecedor fornecedor;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaConta categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id")
    private FormaPagamento formaPagamento;
    
    // Relacionamentos opcionais com outras entidades
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo; // Se a despesa é relacionada a um veículo específico
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista; // Se a despesa é relacionada a um motorista
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;
    
    // Valores
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorOriginal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorJuros = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorMulta = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorPago = BigDecimal.ZERO;
    
    // Datas
    @Column(nullable = false)
    private LocalDate dataEmissao;
    
    @Column(nullable = false)
    private LocalDate dataVencimento;
    
    @Column
    private LocalDate dataPagamento;
    
    @Column
    private LocalDate dataCompetencia; // Mês/ano de referência
    
    // Status e Controle
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status = StatusConta.PENDENTE;
    
    @Column
    private Integer numeroParcela; // Ex: 1, 2, 3...
    
    @Column
    private Integer totalParcelas; // Total de parcelas
    
    @Column(nullable = false)
    private Boolean recorrente = false; // Se é uma conta recorrente (mensal)
    
    @Column(length = 20)
    private String periodicidade; // MENSAL, TRIMESTRAL, SEMESTRAL, ANUAL
    
    @Column(columnDefinition = "TEXT")
    private String observacao;
    
    @Column(columnDefinition = "TEXT")
    private String anexos; // JSON com URLs de anexos (NFs, boletos, etc.)
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(length = 100)
    private String usuarioCadastro;
    
    @Column(length = 100)
    private String usuarioPagamento;
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Métodos auxiliares
    public BigDecimal calcularSaldo() {
        return valorTotal.subtract(valorPago);
    }
    
    public boolean isVencida() {
        if (status == StatusConta.PAGO || status == StatusConta.CANCELADO) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }
    
    public boolean isPaga() {
        return status == StatusConta.PAGO;
    }
}
```

---

### 6. Entity: ContaReceber

```java
package com.trovian.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "conta_receber")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ContaReceber {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    // Informações Básicas
    @Column(nullable = false, length = 200)
    private String descricao;
    
    @Column(length = 50)
    private String numeroDocumento; // Número da NF de serviço
    
    @Column(length = 20)
    private String numeroNotaFiscal;
    
    @Column(length = 50)
    private String numeroControle;
    
    @Column(length = 50)
    private String numeroCte; // Conhecimento de Transporte Eletrônico
    
    // Relacionamentos
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoria_id", nullable = false)
    private CategoriaConta categoria;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "centro_custo_id")
    private CentroCusto centroCusto;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "forma_pagamento_id")
    private FormaPagamento formaPagamento;
    
    // Relacionamentos opcionais
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "veiculo_id")
    private Veiculo veiculo; // Veículo que realizou o frete
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "motorista_id")
    private Motorista motorista; // Motorista responsável
    
    // Valores
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorOriginal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorDesconto = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorJuros = BigDecimal.ZERO;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorMulta = BigDecimal.ZERO;
    
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal valorRecebido = BigDecimal.ZERO;
    
    // Datas
    @Column(nullable = false)
    private LocalDate dataEmissao;
    
    @Column(nullable = false)
    private LocalDate dataVencimento;
    
    @Column
    private LocalDate dataRecebimento;
    
    @Column
    private LocalDate dataCompetencia;
    
    // Status e Controle
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusConta status = StatusConta.PENDENTE;
    
    @Column
    private Integer numeroParcela;
    
    @Column
    private Integer totalParcelas;
    
    @Column(nullable = false)
    private Boolean recorrente = false;
    
    @Column(length = 20)
    private String periodicidade;
    
    // Informações de Frete (específico para transportadoras)
    @Column(length = 100)
    private String origemFrete;
    
    @Column(length = 100)
    private String destinoFrete;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal pesoTransportado;
    
    @Column(length = 50)
    private String tipoMercadoria;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal distanciaKm;
    
    @Column(columnDefinition = "TEXT")
    private String observacao;
    
    @Column(columnDefinition = "TEXT")
    private String anexos;
    
    // Auditoria
    @Temporal(TemporalType.DATE)
    @Column(nullable = false, updatable = false)
    private Date dataCadastro = new Date();
    
    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    @Column(length = 100)
    private String usuarioCadastro;
    
    @Column(length = 100)
    private String usuarioRecebimento;
    
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Métodos auxiliares
    public BigDecimal calcularSaldo() {
        return valorTotal.subtract(valorRecebido);
    }
    
    public boolean isVencida() {
        if (status == StatusConta.RECEBIDO || status == StatusConta.CANCELADO) {
            return false;
        }
        return LocalDate.now().isAfter(dataVencimento);
    }
    
    public boolean isRecebida() {
        return status == StatusConta.RECEBIDO;
    }
}
```

### Enum: StatusConta
```java
package com.trovian.entity;

public enum StatusConta {
    PENDENTE,           // Aguardando pagamento/recebimento
    PAGO,              // Pago (ContaPagar)
    RECEBIDO,          // Recebido (ContaReceber)
    PARCIAL,           // Pagamento/Recebimento parcial
    VENCIDO,           // Vencido e não pago/recebido
    CANCELADO,         // Cancelado
    RENEGOCIADO        // Renegociado
}
```

---

## 📝 DTOs (Data Transfer Objects)

### FornecedorDTO
```java
package com.trovian.dto;

import com.trovian.entity.TipoFornecedor;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Fornecedor")
public class FornecedorDTO {
    
    @Schema(description = "ID do fornecedor", example = "1")
    private Long id;
    
    @NotBlank(message = "Razão social é obrigatória")
    @Size(max = 200, message = "Razão social deve ter no máximo 200 caracteres")
    @Schema(description = "Razão social", example = "Auto Peças Silva LTDA")
    private String razaoSocial;
    
    @Size(max = 200, message = "Nome fantasia deve ter no máximo 200 caracteres")
    @Schema(description = "Nome fantasia", example = "Silva Peças")
    private String nomeFantasia;
    
    @Size(max = 18, message = "CNPJ/CPF deve ter no máximo 18 caracteres")
    @Schema(description = "CNPJ ou CPF", example = "12.345.678/0001-90")
    private String cnpjCpf;
    
    @Size(max = 20, message = "Inscrição estadual deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição estadual")
    private String inscricaoEstadual;
    
    @Size(max = 20, message = "Inscrição municipal deve ter no máximo 20 caracteres")
    @Schema(description = "Inscrição municipal")
    private String inscricaoMunicipal;
    
    @Schema(description = "Logradouro", example = "Rua das Flores")
    private String logradouro;
    
    @Schema(description = "Número", example = "123")
    private String numero;
    
    @Schema(description = "Complemento", example = "Sala 5")
    private String complemento;
    
    @Schema(description = "Bairro", example = "Centro")
    private String bairro;
    
    @Schema(description = "CEP", example = "12345-678")
    private String cep;
    
    @Schema(description = "Cidade", example = "São Paulo")
    private String cidade;
    
    @Schema(description = "UF", example = "SP")
    private String uf;
    
    @Schema(description = "Telefone 1", example = "11 99999-9999")
    private String telefone1;
    
    @Schema(description = "Telefone 2")
    private String telefone2;
    
    @Email(message = "Email inválido")
    @Schema(description = "Email", example = "contato@silvapecas.com.br")
    private String email;
    
    @Schema(description = "Site", example = "www.silvapecas.com.br")
    private String site;
    
    @Schema(description = "Contato principal", example = "João Silva")
    private String contatoPrincipal;
    
    @Schema(description = "Banco", example = "Banco do Brasil")
    private String banco;
    
    @Schema(description = "Agência", example = "1234-5")
    private String agencia;
    
    @Schema(description = "Conta", example = "12345-6")
    private String conta;
    
    @Schema(description = "Tipo de conta", example = "Corrente")
    private String tipoConta;
    
    @Schema(description = "Chave PIX", example = "12345678901")
    private String chavePix;
    
    @NotNull(message = "Tipo de fornecedor é obrigatório")
    @Schema(description = "Tipo de fornecedor", example = "PECAS")
    private TipoFornecedor tipo;
    
    @Schema(description = "Observações")
    private String observacao;
    
    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
```

### CategoriaContaDTO
```java
package com.trovian.dto;

import com.trovian.entity.TipoConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Categoria de Conta")
public class CategoriaContaDTO {
    
    @Schema(description = "ID da categoria", example = "1")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome da categoria", example = "Manutenção de Veículos")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição da categoria")
    private String descricao;
    
    @NotNull(message = "Tipo é obrigatório")
    @Schema(description = "Tipo da conta", example = "PAGAR")
    private TipoConta tipo;
    
    @Schema(description = "ID da categoria pai (para hierarquia)")
    private Long categoriaPaiId;
    
    @Schema(description = "Nome da categoria pai")
    private String categoriaPaiNome;
    
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código contábil", example = "1.2.01")
    private String codigo;
    
    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
```

### CentroCustoDTO
```java
package com.trovian.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Centro de Custo")
public class CentroCustoDTO {
    
    @Schema(description = "ID do centro de custo", example = "1")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome do centro de custo", example = "Frota SP")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição")
    private String descricao;
    
    @Size(max = 50, message = "Código deve ter no máximo 50 caracteres")
    @Schema(description = "Código", example = "CC-001")
    private String codigo;
    
    @Schema(description = "ID do cliente associado")
    private Long clienteId;
    
    @Schema(description = "Nome do cliente associado")
    private String clienteNome;
    
    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
```

### FormaPagamentoDTO
```java
package com.trovian.dto;

import com.trovian.entity.TipoFormaPagamento;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Forma de Pagamento")
public class FormaPagamentoDTO {
    
    @Schema(description = "ID da forma de pagamento", example = "1")
    private Long id;
    
    @NotBlank(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    @Schema(description = "Nome", example = "Boleto Bancário")
    private String nome;
    
    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    @Schema(description = "Descrição")
    private String descricao;
    
    @Schema(description = "Tipo", example = "BOLETO")
    private TipoFormaPagamento tipo;
    
    @Schema(description = "Prazo médio em dias", example = "30")
    private Integer prazoMedioDias;
    
    @Schema(description = "Permite parcelamento", example = "true")
    private Boolean permiteParcelamento;
    
    @Schema(description = "Status ativo/inativo", example = "true")
    private Boolean status;
}
```

### ContaPagarDTO
```java
package com.trovian.dto;

import com.trovian.entity.StatusConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Conta a Pagar")
public class ContaPagarDTO {
    
    @Schema(description = "ID da conta", example = "1")
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Schema(description = "Descrição", example = "Manutenção preventiva veículo ABC-1234")
    private String descricao;
    
    @Schema(description = "Número do documento")
    private String numeroDocumento;
    
    @Schema(description = "Número da nota fiscal")
    private String numeroNotaFiscal;
    
    @Schema(description = "Número de controle interno")
    private String numeroControle;
    
    @NotNull(message = "Fornecedor é obrigatório")
    @Schema(description = "ID do fornecedor", example = "1")
    private Long fornecedorId;
    
    @Schema(description = "Nome do fornecedor")
    private String fornecedorNome;
    
    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "ID da categoria", example = "1")
    private Long categoriaId;
    
    @Schema(description = "Nome da categoria")
    private String categoriaNome;
    
    @Schema(description = "ID do centro de custo")
    private Long centroCustoId;
    
    @Schema(description = "Nome do centro de custo")
    private String centroCustoNome;
    
    @Schema(description = "ID da forma de pagamento")
    private Long formaPagamentoId;
    
    @Schema(description = "Nome da forma de pagamento")
    private String formaPagamentoNome;
    
    @Schema(description = "ID do veículo relacionado")
    private Long veiculoId;
    
    @Schema(description = "Placa do veículo")
    private String veiculoPlaca;
    
    @Schema(description = "ID do motorista relacionado")
    private Long motoristaId;
    
    @Schema(description = "Nome do motorista")
    private String motoristaNome;
    
    @Schema(description = "ID do cliente")
    private Long clienteId;
    
    @Schema(description = "Nome do cliente")
    private String clienteNome;
    
    @NotNull(message = "Valor original é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor original deve ser maior que zero")
    @Schema(description = "Valor original", example = "1500.00")
    private BigDecimal valorOriginal;
    
    @DecimalMin(value = "0.00", message = "Valor de desconto não pode ser negativo")
    @Schema(description = "Valor de desconto", example = "50.00")
    private BigDecimal valorDesconto;
    
    @DecimalMin(value = "0.00", message = "Valor de juros não pode ser negativo")
    @Schema(description = "Valor de juros", example = "0.00")
    private BigDecimal valorJuros;
    
    @DecimalMin(value = "0.00", message = "Valor de multa não pode ser negativo")
    @Schema(description = "Valor de multa", example = "0.00")
    private BigDecimal valorMulta;
    
    @NotNull(message = "Valor total é obrigatório")
    @Schema(description = "Valor total", example = "1450.00")
    private BigDecimal valorTotal;
    
    @Schema(description = "Valor já pago", example = "0.00")
    private BigDecimal valorPago;
    
    @Schema(description = "Saldo a pagar", example = "1450.00")
    private BigDecimal saldo;
    
    @NotNull(message = "Data de emissão é obrigatória")
    @Schema(description = "Data de emissão", example = "2025-11-01")
    private LocalDate dataEmissao;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Schema(description = "Data de vencimento", example = "2025-12-01")
    private LocalDate dataVencimento;
    
    @Schema(description = "Data de pagamento", example = "2025-11-28")
    private LocalDate dataPagamento;
    
    @Schema(description = "Data de competência (mês/ano)", example = "2025-11-01")
    private LocalDate dataCompetencia;
    
    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da conta", example = "PENDENTE")
    private StatusConta status;
    
    @Schema(description = "Número da parcela", example = "1")
    private Integer numeroParcela;
    
    @Schema(description = "Total de parcelas", example = "3")
    private Integer totalParcelas;
    
    @Schema(description = "É recorrente?", example = "false")
    private Boolean recorrente;
    
    @Schema(description = "Periodicidade", example = "MENSAL")
    private String periodicidade;
    
    @Schema(description = "Observações")
    private String observacao;
    
    @Schema(description = "Anexos (JSON)")
    private String anexos;
    
    @Schema(description = "Usuário que cadastrou")
    private String usuarioCadastro;
    
    @Schema(description = "Usuário que realizou o pagamento")
    private String usuarioPagamento;
    
    @Schema(description = "Conta está vencida?", example = "false")
    private Boolean vencida;
}
```

### ContaReceberDTO
```java
package com.trovian.dto;

import com.trovian.entity.StatusConta;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "DTO para Conta a Receber")
public class ContaReceberDTO {
    
    @Schema(description = "ID da conta", example = "1")
    private Long id;
    
    @NotBlank(message = "Descrição é obrigatória")
    @Size(max = 200, message = "Descrição deve ter no máximo 200 caracteres")
    @Schema(description = "Descrição", example = "Frete SP-RJ")
    private String descricao;
    
    @Schema(description = "Número do documento")
    private String numeroDocumento;
    
    @Schema(description = "Número da nota fiscal")
    private String numeroNotaFiscal;
    
    @Schema(description = "Número de controle interno")
    private String numeroControle;
    
    @Schema(description = "Número do CT-e")
    private String numeroCte;
    
    @NotNull(message = "Cliente é obrigatório")
    @Schema(description = "ID do cliente", example = "1")
    private Long clienteId;
    
    @Schema(description = "Nome do cliente")
    private String clienteNome;
    
    @NotNull(message = "Categoria é obrigatória")
    @Schema(description = "ID da categoria", example = "1")
    private Long categoriaId;
    
    @Schema(description = "Nome da categoria")
    private String categoriaNome;
    
    @Schema(description = "ID do centro de custo")
    private Long centroCustoId;
    
    @Schema(description = "Nome do centro de custo")
    private String centroCustoNome;
    
    @Schema(description = "ID da forma de pagamento")
    private Long formaPagamentoId;
    
    @Schema(description = "Nome da forma de pagamento")
    private String formaPagamentoNome;
    
    @Schema(description = "ID do veículo relacionado")
    private Long veiculoId;
    
    @Schema(description = "Placa do veículo")
    private String veiculoPlaca;
    
    @Schema(description = "ID do motorista relacionado")
    private Long motoristaId;
    
    @Schema(description = "Nome do motorista")
    private String motoristaNome;
    
    @NotNull(message = "Valor original é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor original deve ser maior que zero")
    @Schema(description = "Valor original", example = "5000.00")
    private BigDecimal valorOriginal;
    
    @DecimalMin(value = "0.00", message = "Valor de desconto não pode ser negativo")
    @Schema(description = "Valor de desconto", example = "0.00")
    private BigDecimal valorDesconto;
    
    @DecimalMin(value = "0.00", message = "Valor de juros não pode ser negativo")
    @Schema(description = "Valor de juros", example = "0.00")
    private BigDecimal valorJuros;
    
    @DecimalMin(value = "0.00", message = "Valor de multa não pode ser negativo")
    @Schema(description = "Valor de multa", example = "0.00")
    private BigDecimal valorMulta;
    
    @NotNull(message = "Valor total é obrigatório")
    @Schema(description = "Valor total", example = "5000.00")
    private BigDecimal valorTotal;
    
    @Schema(description = "Valor já recebido", example = "0.00")
    private BigDecimal valorRecebido;
    
    @Schema(description = "Saldo a receber", example = "5000.00")
    private BigDecimal saldo;
    
    @NotNull(message = "Data de emissão é obrigatória")
    @Schema(description = "Data de emissão", example = "2025-11-01")
    private LocalDate dataEmissao;
    
    @NotNull(message = "Data de vencimento é obrigatória")
    @Schema(description = "Data de vencimento", example = "2025-12-15")
    private LocalDate dataVencimento;
    
    @Schema(description = "Data de recebimento", example = "2025-12-10")
    private LocalDate dataRecebimento;
    
    @Schema(description = "Data de competência", example = "2025-11-01")
    private LocalDate dataCompetencia;
    
    @NotNull(message = "Status é obrigatório")
    @Schema(description = "Status da conta", example = "PENDENTE")
    private StatusConta status;
    
    @Schema(description = "Número da parcela", example = "1")
    private Integer numeroParcela;
    
    @Schema(description = "Total de parcelas", example = "1")
    private Integer totalParcelas;
    
    @Schema(description = "É recorrente?", example = "false")
    private Boolean recorrente;
    
    @Schema(description = "Periodicidade")
    private String periodicidade;
    
    @Schema(description = "Origem do frete", example = "São Paulo - SP")
    private String origemFrete;
    
    @Schema(description = "Destino do frete", example = "Rio de Janeiro - RJ")
    private String destinoFrete;
    
    @Schema(description = "Peso transportado (ton)", example = "25.5")
    private BigDecimal pesoTransportado;
    
    @Schema(description = "Tipo de mercadoria", example = "Alimentos")
    private String tipoMercadoria;
    
    @Schema(description = "Distância percorrida (km)", example = "450.0")
    private BigDecimal distanciaKm;
    
    @Schema(description = "Observações")
    private String observacao;
    
    @Schema(description = "Anexos (JSON)")
    private String anexos;
    
    @Schema(description = "Usuário que cadastrou")
    private String usuarioCadastro;
    
    @Schema(description = "Usuário que confirmou recebimento")
    private String usuarioRecebimento;
    
    @Schema(description = "Conta está vencida?", example = "false")
    private Boolean vencida;
}
```

---

## 🔧 Repositories

### FornecedorRepository
```java
package com.trovian.repository;

import com.trovian.entity.Fornecedor;
import com.trovian.entity.TipoFornecedor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface FornecedorRepository extends JpaRepository<Fornecedor, Long> {
    
    Page<Fornecedor> findByStatus(Boolean status, Pageable pageable);
    
    Page<Fornecedor> findByTipo(TipoFornecedor tipo, Pageable pageable);
    
    Page<Fornecedor> findByRazaoSocialContainingIgnoreCase(String razaoSocial, Pageable pageable);
    
    Optional<Fornecedor> findByCnpjCpf(String cnpjCpf);
    
    boolean existsByCnpjCpf(String cnpjCpf);
}
```

### CategoriaContaRepository
```java
package com.trovian.repository;

import com.trovian.entity.CategoriaConta;
import com.trovian.entity.TipoConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriaContaRepository extends JpaRepository<CategoriaConta, Long> {
    
    Page<CategoriaConta> findByStatus(Boolean status, Pageable pageable);
    
    Page<CategoriaConta> findByTipo(TipoConta tipo, Pageable pageable);
    
    Page<CategoriaConta> findByCategoriaPaiId(Long categoriaPaiId, Pageable pageable);
    
    Page<CategoriaConta> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
```

### CentroCustoRepository
```java
package com.trovian.repository;

import com.trovian.entity.CentroCusto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CentroCustoRepository extends JpaRepository<CentroCusto, Long> {
    
    Page<CentroCusto> findByStatus(Boolean status, Pageable pageable);
    
    Page<CentroCusto> findByClienteId(Long clienteId, Pageable pageable);
    
    Page<CentroCusto> findByNomeContainingIgnoreCase(String nome, Pageable pageable);
}
```

### FormaPagamentoRepository
```java
package com.trovian.repository;

import com.trovian.entity.FormaPagamento;
import com.trovian.entity.TipoFormaPagamento;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormaPagamentoRepository extends JpaRepository<FormaPagamento, Long> {
    
    Page<FormaPagamento> findByStatus(Boolean status, Pageable pageable);
    
    Page<FormaPagamento> findByTipo(TipoFormaPagamento tipo, Pageable pageable);
    
    Page<FormaPagamento> findByPermiteParcelamento(Boolean permiteParcelamento, Pageable pageable);
}
```

### ContaPagarRepository
```java
package com.trovian.repository;

import com.trovian.entity.ContaPagar;
import com.trovian.entity.StatusConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaPagarRepository extends JpaRepository<ContaPagar, Long> {
    
    // Buscar por status
    Page<ContaPagar> findByStatus(StatusConta status, Pageable pageable);
    
    // Buscar por fornecedor
    Page<ContaPagar> findByFornecedorId(Long fornecedorId, Pageable pageable);
    
    // Buscar por categoria
    Page<ContaPagar> findByCategoriaId(Long categoriaId, Pageable pageable);
    
    // Buscar por centro de custo
    Page<ContaPagar> findByCentroCustoId(Long centroCustoId, Pageable pageable);
    
    // Buscar por cliente
    Page<ContaPagar> findByClienteId(Long clienteId, Pageable pageable);
    
    // Buscar por veículo
    Page<ContaPagar> findByVeiculoId(Long veiculoId, Pageable pageable);
    
    // Buscar por motorista
    Page<ContaPagar> findByMotoristaId(Long motoristaId, Pageable pageable);
    
    // Buscar por período de vencimento
    Page<ContaPagar> findByDataVencimentoBetween(
        LocalDate dataInicio, 
        LocalDate dataFim, 
        Pageable pageable
    );
    
    // Buscar por período de emissão
    Page<ContaPagar> findByDataEmissaoBetween(
        LocalDate dataInicio, 
        LocalDate dataFim, 
        Pageable pageable
    );
    
    // Buscar contas vencidas
    @Query("SELECT c FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento < :hoje")
    Page<ContaPagar> findVencidas(@Param("hoje") LocalDate hoje, Pageable pageable);
    
    // Buscar contas a vencer (próximos X dias)
    @Query("SELECT c FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento BETWEEN :hoje AND :dataFutura")
    Page<ContaPagar> findAVencer(
        @Param("hoje") LocalDate hoje, 
        @Param("dataFutura") LocalDate dataFutura, 
        Pageable pageable
    );
    
    // Buscar por descrição
    Page<ContaPagar> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
    
    // Totalizadores
    @Query("SELECT SUM(c.valorTotal) FROM ContaPagar c WHERE c.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") StatusConta status);
    
    @Query("SELECT SUM(c.valorTotal - c.valorPago) FROM ContaPagar c WHERE c.status IN ('PENDENTE', 'PARCIAL')")
    BigDecimal sumSaldoAPagar();
    
    @Query("SELECT SUM(c.valorTotal) FROM ContaPagar c WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumTotalPorPeriodo(
        @Param("dataInicio") LocalDate dataInicio, 
        @Param("dataFim") LocalDate dataFim
    );
}
```

### ContaReceberRepository
```java
package com.trovian.repository;

import com.trovian.entity.ContaReceber;
import com.trovian.entity.StatusConta;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.time.LocalDate;

@Repository
public interface ContaReceberRepository extends JpaRepository<ContaReceber, Long> {
    
    // Buscar por status
    Page<ContaReceber> findByStatus(StatusConta status, Pageable pageable);
    
    // Buscar por cliente
    Page<ContaReceber> findByClienteId(Long clienteId, Pageable pageable);
    
    // Buscar por categoria
    Page<ContaReceber> findByCategoriaId(Long categoriaId, Pageable pageable);
    
    // Buscar por centro de custo
    Page<ContaReceber> findByCentroCustoId(Long centroCustoId, Pageable pageable);
    
    // Buscar por veículo
    Page<ContaReceber> findByVeiculoId(Long veiculoId, Pageable pageable);
    
    // Buscar por motorista
    Page<ContaReceber> findByMotoristaId(Long motoristaId, Pageable pageable);
    
    // Buscar por período de vencimento
    Page<ContaReceber> findByDataVencimentoBetween(
        LocalDate dataInicio, 
        LocalDate dataFim, 
        Pageable pageable
    );
    
    // Buscar por período de emissão
    Page<ContaReceber> findByDataEmissaoBetween(
        LocalDate dataInicio, 
        LocalDate dataFim, 
        Pageable pageable
    );
    
    // Buscar contas vencidas
    @Query("SELECT c FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento < :hoje")
    Page<ContaReceber> findVencidas(@Param("hoje") LocalDate hoje, Pageable pageable);
    
    // Buscar contas a vencer (próximos X dias)
    @Query("SELECT c FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL') AND c.dataVencimento BETWEEN :hoje AND :dataFutura")
    Page<ContaReceber> findAVencer(
        @Param("hoje") LocalDate hoje, 
        @Param("dataFutura") LocalDate dataFutura, 
        Pageable pageable
    );
    
    // Buscar por descrição
    Page<ContaReceber> findByDescricaoContainingIgnoreCase(String descricao, Pageable pageable);
    
    // Buscar por origem e destino
    Page<ContaReceber> findByOrigemFreteContainingIgnoreCaseOrDestinoFreteContainingIgnoreCase(
        String origem, 
        String destino, 
        Pageable pageable
    );
    
    // Totalizadores
    @Query("SELECT SUM(c.valorTotal) FROM ContaReceber c WHERE c.status = :status")
    BigDecimal sumTotalByStatus(@Param("status") StatusConta status);
    
    @Query("SELECT SUM(c.valorTotal - c.valorRecebido) FROM ContaReceber c WHERE c.status IN ('PENDENTE', 'PARCIAL')")
    BigDecimal sumSaldoAReceber();
    
    @Query("SELECT SUM(c.valorTotal) FROM ContaReceber c WHERE c.dataVencimento BETWEEN :dataInicio AND :dataFim")
    BigDecimal sumTotalPorPeriodo(
        @Param("dataInicio") LocalDate dataInicio, 
        @Param("dataFim") LocalDate dataFim
    );
}
```

---

## 🎯 Services (Lógica de Negócio)

*Nota: Os services seguem o mesmo padrão dos demais módulos da aplicação:*

1. **Injeção de dependências** via `@RequiredArgsConstructor` (Lombok)
2. **Logging** com `@Slf4j`
3. **Transações** com `@Transactional`
4. **Conversão** entre Entity ↔ DTO
5. **Validações de negócio**
6. **Exceções customizadas** (RuntimeException para não encontrado)

### FornecedorService
```java
package com.trovian.service;

import com.trovian.dto.FornecedorDTO;
import com.trovian.entity.Fornecedor;
import com.trovian.repository.FornecedorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FornecedorService {
    
    private final FornecedorRepository fornecedorRepository;
    
    // CREATE
    @Transactional
    public FornecedorDTO create(FornecedorDTO dto) {
        log.info("Criando fornecedor: {}", dto.getRazaoSocial());
        
        // Validar CNPJ/CPF único
        if (dto.getCnpjCpf() != null && fornecedorRepository.existsByCnpjCpf(dto.getCnpjCpf())) {
            throw new RuntimeException("CNPJ/CPF já cadastrado: " + dto.getCnpjCpf());
        }
        
        Fornecedor fornecedor = toEntity(dto);
        Fornecedor saved = fornecedorRepository.save(fornecedor);
        
        log.info("Fornecedor criado com sucesso. ID: {}", saved.getId());
        return toDTO(saved);
    }
    
    // READ ALL
    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findAll(int page, int size, String sortBy, String direction) {
        log.info("Buscando fornecedores - página: {}, tamanho: {}, ordenar por: {}", page, size, sortBy);
        
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        return fornecedorRepository.findAll(pageable).map(this::toDTO);
    }
    
    // READ BY ID
    @Transactional(readOnly = true)
    public FornecedorDTO findById(Long id) {
        log.info("Buscando fornecedor por ID: {}", id);
        
        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com ID: " + id));
        
        return toDTO(fornecedor);
    }
    
    // UPDATE
    @Transactional
    public FornecedorDTO update(Long id, FornecedorDTO dto) {
        log.info("Atualizando fornecedor ID: {}", id);
        
        Fornecedor fornecedor = fornecedorRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Fornecedor não encontrado com ID: " + id));
        
        // Validar CNPJ/CPF único (se foi alterado)
        if (dto.getCnpjCpf() != null && !dto.getCnpjCpf().equals(fornecedor.getCnpjCpf())) {
            if (fornecedorRepository.existsByCnpjCpf(dto.getCnpjCpf())) {
                throw new RuntimeException("CNPJ/CPF já cadastrado: " + dto.getCnpjCpf());
            }
        }
        
        updateEntityFromDTO(fornecedor, dto);
        Fornecedor updated = fornecedorRepository.save(fornecedor);
        
        log.info("Fornecedor atualizado com sucesso. ID: {}", id);
        return toDTO(updated);
    }
    
    // DELETE
    @Transactional
    public void delete(Long id) {
        log.info("Deletando fornecedor ID: {}", id);
        
        if (!fornecedorRepository.existsById(id)) {
            throw new RuntimeException("Fornecedor não encontrado com ID: " + id);
        }
        
        fornecedorRepository.deleteById(id);
        log.info("Fornecedor deletado com sucesso. ID: {}", id);
    }
    
    // SEARCHES
    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByStatus(Boolean status, Pageable pageable) {
        return fornecedorRepository.findByStatus(status, pageable).map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByTipo(String tipo, Pageable pageable) {
        return fornecedorRepository.findByTipo(
            TipoFornecedor.valueOf(tipo), 
            pageable
        ).map(this::toDTO);
    }
    
    @Transactional(readOnly = true)
    public Page<FornecedorDTO> findByRazaoSocial(String razaoSocial, Pageable pageable) {
        return fornecedorRepository.findByRazaoSocialContainingIgnoreCase(
            razaoSocial, 
            pageable
        ).map(this::toDTO);
    }
    
    // CONVERSIONS
    private FornecedorDTO toDTO(Fornecedor entity) {
        FornecedorDTO dto = new FornecedorDTO();
        dto.setId(entity.getId());
        dto.setRazaoSocial(entity.getRazaoSocial());
        dto.setNomeFantasia(entity.getNomeFantasia());
        dto.setCnpjCpf(entity.getCnpjCpf());
        dto.setInscricaoEstadual(entity.getInscricaoEstadual());
        dto.setInscricaoMunicipal(entity.getInscricaoMunicipal());
        dto.setLogradouro(entity.getLogradouro());
        dto.setNumero(entity.getNumero());
        dto.setComplemento(entity.getComplemento());
        dto.setBairro(entity.getBairro());
        dto.setCep(entity.getCep());
        dto.setCidade(entity.getCidade());
        dto.setUf(entity.getUf());
        dto.setTelefone1(entity.getTelefone1());
        dto.setTelefone2(entity.getTelefone2());
        dto.setEmail(entity.getEmail());
        dto.setSite(entity.getSite());
        dto.setContatoPrincipal(entity.getContatoPrincipal());
        dto.setBanco(entity.getBanco());
        dto.setAgencia(entity.getAgencia());
        dto.setConta(entity.getConta());
        dto.setTipoConta(entity.getTipoConta());
        dto.setChavePix(entity.getChavePix());
        dto.setTipo(entity.getTipo());
        dto.setObservacao(entity.getObservacao());
        dto.setStatus(entity.getStatus());
        return dto;
    }
    
    private Fornecedor toEntity(FornecedorDTO dto) {
        Fornecedor entity = new Fornecedor();
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setNomeFantasia(dto.getNomeFantasia());
        entity.setCnpjCpf(dto.getCnpjCpf());
        entity.setInscricaoEstadual(dto.getInscricaoEstadual());
        entity.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setTelefone1(dto.getTelefone1());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEmail(dto.getEmail());
        entity.setSite(dto.getSite());
        entity.setContatoPrincipal(dto.getContatoPrincipal());
        entity.setBanco(dto.getBanco());
        entity.setAgencia(dto.getAgencia());
        entity.setConta(dto.getConta());
        entity.setTipoConta(dto.getTipoConta());
        entity.setChavePix(dto.getChavePix());
        entity.setTipo(dto.getTipo());
        entity.setObservacao(dto.getObservacao());
        entity.setStatus(dto.getStatus() != null ? dto.getStatus() : true);
        return entity;
    }
    
    private void updateEntityFromDTO(Fornecedor entity, FornecedorDTO dto) {
        entity.setRazaoSocial(dto.getRazaoSocial());
        entity.setNomeFantasia(dto.getNomeFantasia());
        entity.setCnpjCpf(dto.getCnpjCpf());
        entity.setInscricaoEstadual(dto.getInscricaoEstadual());
        entity.setInscricaoMunicipal(dto.getInscricaoMunicipal());
        entity.setLogradouro(dto.getLogradouro());
        entity.setNumero(dto.getNumero());
        entity.setComplemento(dto.getComplemento());
        entity.setBairro(dto.getBairro());
        entity.setCep(dto.getCep());
        entity.setCidade(dto.getCidade());
        entity.setUf(dto.getUf());
        entity.setTelefone1(dto.getTelefone1());
        entity.setTelefone2(dto.getTelefone2());
        entity.setEmail(dto.getEmail());
        entity.setSite(dto.getSite());
        entity.setContatoPrincipal(dto.getContatoPrincipal());
        entity.setBanco(dto.getBanco());
        entity.setAgencia(dto.getAgencia());
        entity.setConta(dto.getConta());
        entity.setTipoConta(dto.getTipoConta());
        entity.setChavePix(dto.getChavePix());
        entity.setTipo(dto.getTipo());
        entity.setObservacao(dto.getObservacao());
        entity.setStatus(dto.getStatus());
    }
}
```

*Nota: Os demais services (CategoriaConta, CentroCusto, FormaPagamento, ContaPagar, ContaReceber) seguem estrutura similar.*

---

## 🎮 Controllers (REST API)

### ContaPagarController (Exemplo Completo)
```java
package com.trovian.controller;

import com.trovian.dto.ContaPagarDTO;
import com.trovian.service.ContaPagarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("/conta-pagar")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contas a Pagar", description = "Gerenciamento de contas a pagar")
public class ContaPagarController {
    
    private final ContaPagarService contaPagarService;
    
    @PostMapping
    @Operation(summary = "Criar nova conta a pagar")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Conta criada com sucesso"),
        @ApiResponse(responseCode = "400", description = "Dados inválidos")
    })
    public ResponseEntity<ContaPagarDTO> create(@Valid @RequestBody ContaPagarDTO dto) {
        log.info("Request para criar conta a pagar: {}", dto.getDescricao());
        ContaPagarDTO created = contaPagarService.create(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
    
    @GetMapping
    @Operation(summary = "Listar todas as contas a pagar (paginado)")
    public ResponseEntity<Page<ContaPagarDTO>> findAll(
        @Parameter(description = "Número da página") @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Tamanho da página") @RequestParam(defaultValue = "20") int size,
        @Parameter(description = "Campo para ordenação") @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @Parameter(description = "Direção da ordenação") @RequestParam(defaultValue = "ASC") String direction
    ) {
        log.info("Request para listar contas a pagar - página: {}, tamanho: {}", page, size);
        Page<ContaPagarDTO> contas = contaPagarService.findAll(page, size, sortBy, direction);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Buscar conta a pagar por ID")
    public ResponseEntity<ContaPagarDTO> findById(
        @Parameter(description = "ID da conta") @PathVariable Long id
    ) {
        log.info("Request para buscar conta a pagar ID: {}", id);
        ContaPagarDTO conta = contaPagarService.findById(id);
        return ResponseEntity.ok(conta);
    }
    
    @GetMapping("/fornecedor/{fornecedorId}")
    @Operation(summary = "Buscar contas por fornecedor")
    public ResponseEntity<Page<ContaPagarDTO>> findByFornecedor(
        @PathVariable Long fornecedorId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ContaPagarDTO> contas = contaPagarService.findByFornecedor(fornecedorId, pageable);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/veiculo/{veiculoId}")
    @Operation(summary = "Buscar contas por veículo")
    public ResponseEntity<Page<ContaPagarDTO>> findByVeiculo(
        @PathVariable Long veiculoId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ContaPagarDTO> contas = contaPagarService.findByVeiculo(veiculoId, pageable);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/vencidas")
    @Operation(summary = "Buscar contas vencidas")
    public ResponseEntity<Page<ContaPagarDTO>> findVencidas(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ContaPagarDTO> contas = contaPagarService.findVencidas(pageable);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/a-vencer")
    @Operation(summary = "Buscar contas a vencer nos próximos X dias")
    public ResponseEntity<Page<ContaPagarDTO>> findAVencer(
        @Parameter(description = "Número de dias") @RequestParam(defaultValue = "30") int dias,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ContaPagarDTO> contas = contaPagarService.findAVencer(dias, pageable);
        return ResponseEntity.ok(contas);
    }
    
    @GetMapping("/periodo")
    @Operation(summary = "Buscar contas por período de vencimento")
    public ResponseEntity<Page<ContaPagarDTO>> findByPeriodo(
        @Parameter(description = "Data inicial") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
        @Parameter(description = "Data final") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size,
        @RequestParam(defaultValue = "dataVencimento") String sortBy,
        @RequestParam(defaultValue = "ASC") String direction
    ) {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("DESC") 
            ? Sort.Direction.DESC 
            : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));
        
        Page<ContaPagarDTO> contas = contaPagarService.findByPeriodo(dataInicio, dataFim, pageable);
        return ResponseEntity.ok(contas);
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Atualizar conta a pagar")
    public ResponseEntity<ContaPagarDTO> update(
        @PathVariable Long id,
        @Valid @RequestBody ContaPagarDTO dto
    ) {
        log.info("Request para atualizar conta a pagar ID: {}", id);
        ContaPagarDTO updated = contaPagarService.update(id, dto);
        return ResponseEntity.ok(updated);
    }
    
    @PutMapping("/{id}/pagar")
    @Operation(summary = "Registrar pagamento de conta")
    public ResponseEntity<ContaPagarDTO> registrarPagamento(
        @PathVariable Long id,
        @Parameter(description = "Valor pago") @RequestParam BigDecimal valorPago,
        @Parameter(description = "Data do pagamento") 
        @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataPagamento,
        @Parameter(description = "Usuário responsável") @RequestParam String usuario
    ) {
        log.info("Registrando pagamento da conta ID: {} - Valor: {}", id, valorPago);
        ContaPagarDTO updated = contaPagarService.registrarPagamento(id, valorPago, dataPagamento, usuario);
        return ResponseEntity.ok(updated);
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Deletar conta a pagar")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        log.info("Request para deletar conta a pagar ID: {}", id);
        contaPagarService.delete(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/total/pendente")
    @Operation(summary = "Total de contas pendentes")
    public ResponseEntity<BigDecimal> totalPendente() {
        BigDecimal total = contaPagarService.getTotalPendente();
        return ResponseEntity.ok(total);
    }
    
    @GetMapping("/saldo/a-pagar")
    @Operation(summary = "Saldo total a pagar")
    public ResponseEntity<BigDecimal> saldoAPagar() {
        BigDecimal saldo = contaPagarService.getSaldoAPagar();
        return ResponseEntity.ok(saldo);
    }
}
```

---

## 📡 Endpoints da API

### Fornecedor
```
POST   /fornecedor                     - Criar fornecedor
GET    /fornecedor                     - Listar todos (paginado)
GET    /fornecedor/{id}                - Buscar por ID
GET    /fornecedor/tipo/{tipo}         - Buscar por tipo
GET    /fornecedor/buscar              - Buscar por razão social
PUT    /fornecedor/{id}                - Atualizar
DELETE /fornecedor/{id}                - Deletar
```

### Categoria Conta
```
POST   /categoria-conta                - Criar categoria
GET    /categoria-conta                - Listar todas (paginado)
GET    /categoria-conta/{id}           - Buscar por ID
GET    /categoria-conta/tipo/{tipo}    - Buscar por tipo (PAGAR/RECEBER)
PUT    /categoria-conta/{id}           - Atualizar
DELETE /categoria-conta/{id}           - Deletar
```

### Centro de Custo
```
POST   /centro-custo                   - Criar centro de custo
GET    /centro-custo                   - Listar todos (paginado)
GET    /centro-custo/{id}              - Buscar por ID
GET    /centro-custo/cliente/{id}      - Buscar por cliente
PUT    /centro-custo/{id}              - Atualizar
DELETE /centro-custo/{id}              - Deletar
```

### Forma de Pagamento
```
POST   /forma-pagamento                - Criar forma de pagamento
GET    /forma-pagamento                - Listar todas (paginado)
GET    /forma-pagamento/{id}           - Buscar por ID
GET    /forma-pagamento/tipo/{tipo}    - Buscar por tipo
PUT    /forma-pagamento/{id}           - Atualizar
DELETE /forma-pagamento/{id}           - Deletar
```

### Conta a Pagar
```
POST   /conta-pagar                           - Criar conta
GET    /conta-pagar                           - Listar todas (paginado)
GET    /conta-pagar/{id}                      - Buscar por ID
GET    /conta-pagar/fornecedor/{id}           - Buscar por fornecedor
GET    /conta-pagar/veiculo/{id}              - Buscar por veículo
GET    /conta-pagar/motorista/{id}            - Buscar por motorista
GET    /conta-pagar/categoria/{id}            - Buscar por categoria
GET    /conta-pagar/cliente/{id}              - Buscar por cliente
GET    /conta-pagar/status/{status}           - Buscar por status
GET    /conta-pagar/vencidas                  - Contas vencidas
GET    /conta-pagar/a-vencer?dias=30          - A vencer (próximos X dias)
GET    /conta-pagar/periodo?inicio=&fim=      - Por período
PUT    /conta-pagar/{id}                      - Atualizar
PUT    /conta-pagar/{id}/pagar                - Registrar pagamento
DELETE /conta-pagar/{id}                      - Deletar
GET    /conta-pagar/total/pendente            - Total pendente
GET    /conta-pagar/saldo/a-pagar             - Saldo a pagar
```

### Conta a Receber
```
POST   /conta-receber                         - Criar conta
GET    /conta-receber                         - Listar todas (paginado)
GET    /conta-receber/{id}                    - Buscar por ID
GET    /conta-receber/cliente/{id}            - Buscar por cliente
GET    /conta-receber/veiculo/{id}            - Buscar por veículo
GET    /conta-receber/motorista/{id}          - Buscar por motorista
GET    /conta-receber/categoria/{id}          - Buscar por categoria
GET    /conta-receber/status/{status}         - Buscar por status
GET    /conta-receber/vencidas                - Contas vencidas
GET    /conta-receber/a-vencer?dias=30        - A vencer (próximos X dias)
GET    /conta-receber/periodo?inicio=&fim=    - Por período
PUT    /conta-receber/{id}                    - Atualizar
PUT    /conta-receber/{id}/receber            - Registrar recebimento
DELETE /conta-receber/{id}                    - Deletar
GET    /conta-receber/total/pendente          - Total pendente
GET    /conta-receber/saldo/a-receber         - Saldo a receber
```

---

## 💡 Casos de Uso Importantes

### 1. Cadastrar Despesa de Manutenção
```bash
# 1. Cadastrar fornecedor (oficina)
curl -X POST http://localhost:8080/fornecedor \
  -H "Content-Type: application/json" \
  -d '{
    "razaoSocial": "Mecânica Silva LTDA",
    "nomeFantasia": "Silva Mecânica",
    "cnpjCpf": "12.345.678/0001-90",
    "tipo": "MANUTENCAO",
    "telefone1": "11 99999-9999",
    "chavePix": "mecanica@silva.com.br",
    "status": true
  }'

# 2. Criar conta a pagar
curl -X POST http://localhost:8080/conta-pagar \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Manutenção preventiva - Troca de óleo e filtros",
    "numeroNotaFiscal": "12345",
    "fornecedorId": 1,
    "categoriaId": 2,
    "veiculoId": 5,
    "valorOriginal": 850.00,
    "valorTotal": 850.00,
    "dataEmissao": "2025-11-01",
    "dataVencimento": "2025-11-15",
    "status": "PENDENTE"
  }'
```

### 2. Registrar Receita de Frete
```bash
curl -X POST http://localhost:8080/conta-receber \
  -H "Content-Type: application/json" \
  -d '{
    "descricao": "Frete São Paulo - Rio de Janeiro",
    "numeroCte": "CTe-123456",
    "clienteId": 3,
    "categoriaId": 1,
    "veiculoId": 5,
    "motoristaId": 2,
    "valorOriginal": 5000.00,
    "valorTotal": 5000.00,
    "dataEmissao": "2025-11-01",
    "dataVencimento": "2025-11-30",
    "origemFrete": "São Paulo - SP",
    "destinoFrete": "Rio de Janeiro - RJ",
    "pesoTransportado": 25.5,
    "tipoMercadoria": "Alimentos",
    "distanciaKm": 430.0,
    "status": "PENDENTE"
  }'
```

### 3. Registrar Pagamento
```bash
curl -X PUT "http://localhost:8080/conta-pagar/1/pagar?valorPago=850.00&dataPagamento=2025-11-10&usuario=admin"
```

### 4. Consultar Contas Vencidas
```bash
curl -X GET "http://localhost:8080/conta-pagar/vencidas?page=0&size=20"
```

### 5. Fluxo de Caixa (Próximos 30 dias)
```bash
# Contas a pagar
curl -X GET "http://localhost:8080/conta-pagar/a-vencer?dias=30"

# Contas a receber
curl -X GET "http://localhost:8080/conta-receber/a-vencer?dias=30"
```

---

## 🎨 Funcionalidades Avançadas

### 1. **Contas Recorrentes**
- Geração automática de contas mensais (ex: seguro, licenciamento)
- Job agendado para criar novas parcelas

### 2. **Pagamentos Parciais**
- Suporte a múltiplos pagamentos
- Status PARCIAL quando valorPago < valorTotal

### 3. **Renegociação**
- Alterar data de vencimento
- Recalcular juros e multas
- Status RENEGOCIADO

### 4. **Relatórios Financeiros**
- Fluxo de caixa projetado
- DRE (Demonstrativo de Resultado)
- Análise por categoria
- Análise por veículo/motorista
- Indicadores: Inadimplência, Lucratividade

### 5. **Integrações**
- Importação de arquivos bancários (CNAB/OFX)
- Geração de boletos
- Conciliação bancária
- API de pagamento (PIX, boleto)

---

## 🔐 Validações e Regras de Negócio

### ContaPagar
1. **Obrigatórios**: descrição, fornecedor, categoria, valores, datas
2. **Cálculos automáticos**: valorTotal = valorOriginal - desconto + juros + multa
3. **Status**: Atualizar automaticamente baseado em datas e pagamentos
4. **Vencimento**: Não pode ser anterior à emissão
5. **Pagamento**: valorPago não pode exceder valorTotal

### ContaReceber
1. **Obrigatórios**: descrição, cliente, categoria, valores, datas
2. **Cálculos automáticos**: similar a ContaPagar
3. **Status**: Atualizar automaticamente
4. **Frete**: Campos específicos para transporte

### Fornecedor
1. **CNPJ/CPF único**: Validar unicidade
2. **Tipo obrigatório**: Para classificação

---

## 📊 Dashboard - Indicadores Sugeridos

### KPIs Principais
- **Saldo a Pagar**: Total de contas pendentes
- **Saldo a Receber**: Total a receber
- **Fluxo de Caixa**: Entrada - Saída (próximos 30 dias)
- **Contas Vencidas**: Quantidade e valor
- **Taxa de Inadimplência**: % de contas vencidas

### Gráficos
- Evolução mensal (receitas vs despesas)
- Despesas por categoria
- Receitas por cliente
- Despesas por veículo
- Comparativo ano anterior

---

## 🚀 Próximos Passos para Implementação

### Fase 1: Cadastros Básicos (Prioridade Alta)
1. ✅ Implementar Entity Fornecedor
2. ✅ Implementar Entity CategoriaConta
3. ✅ Implementar Entity FormaPagamento
4. ✅ Implementar Entity CentroCusto
5. ✅ Testar relacionamentos

### Fase 2: Contas (Prioridade Alta)
1. ✅ Implementar Entity ContaPagar
2. ✅ Implementar Entity ContaReceber
3. ✅ Implementar Services com lógica de negócio
4. ✅ Implementar Controllers
5. ✅ Testar CRUD completo

### Fase 3: Funcionalidades Avançadas (Prioridade Média)
1. ⚠️ Pagamentos parciais
2. ⚠️ Contas recorrentes (job agendado)
3. ⚠️ Cálculo automático de juros/multa
4. ⚠️ Relatórios financeiros
5. ⚠️ Exportação (Excel, PDF)

### Fase 4: Integrações (Prioridade Baixa)
1. 📋 API de boletos
2. 📋 PIX
3. 📋 Conciliação bancária
4. 📋 Importação CNAB

---

## 📝 Observações Técnicas

### Performance
- **Lazy Loading**: Relacionamentos carregados sob demanda
- **Índices**: Criar índices em campos de busca frequente (dataVencimento, status, fornecedorId, clienteId)
- **Pagination**: Todas as listagens são paginadas

### Segurança
- Validar permissões (quem pode criar/editar/deletar)
- Audit log de alterações financeiras
- Criptografar dados sensíveis (contas bancárias)

### Testes
- Testes unitários (Services)
- Testes de integração (Controllers)
- Testes de cálculos financeiros

---

## 📚 Documentação Adicional

- **Swagger/OpenAPI**: http://localhost:8080/swagger-ui.html
- **Modelo ER**: Disponível no diretório /docs
- **Postman Collection**: Disponível no diretório /postman

---

**Data de Criação**: 06/11/2025  
**Versão**: 1.0.0  
**Analista**: Claude Code  
**Status**: ✅ Especificação Completa - Pronto para Implementação
