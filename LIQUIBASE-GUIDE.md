# Guia do Liquibase - API Trovian

## Visão Geral

O Liquibase foi implementado no projeto para gerenciar as migrações de banco de dados de forma versionada e controlada.

## Estrutura de Arquivos

```
src/main/resources/
└── db/
    └── changelog/
        ├── db.changelog-master.xml          # Arquivo mestre que referencia todos os changelogs
        └── changes/
            ├── v001-criar-tabelas-relatorios.sql
            └── v002-inserir-templates-sistema.sql
```

## Configuração Atual

### application.yml
```yaml
spring:
  liquibase:
    change-log: classpath:db/changelog/db.changelog-master.xml
    enabled: true
    drop-first: false
    default-schema: public
    liquibase-schema: public
```

### Mudanças Importantes
- **Hibernate DDL:** Mudado de `update` para `validate`
  - O Liquibase agora gerencia todas as mudanças de schema
  - Hibernate apenas valida se o schema está correto

## Como Criar Nova Migration

### 1. Criar arquivo SQL
Crie um novo arquivo em `src/main/resources/db/changelog/changes/`:

```sql
--liquibase formatted sql

--changeset seu-usuario:v003-nome-descritivo
--comment: Descrição do que essa migration faz

-- Seu SQL aqui
CREATE TABLE exemplo (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

--rollback DROP TABLE IF EXISTS exemplo;
```

### 2. Adicionar no Master
Edite `db.changelog-master.xml` e adicione a referência:

```xml
<include file="db/changelog/changes/v003-nome-descritivo.sql"/>
```

## Convenções de Nomenclatura

- **Formato:** `vXXX-descricao-breve.sql`
- **Exemplos:**
  - `v001-criar-tabelas-relatorios.sql`
  - `v002-inserir-templates-sistema.sql`
  - `v003-adicionar-coluna-email-usuario.sql`

## Changeset ID

Formato recomendado: `seu-usuario:vXXX-descricao`
- Exemplo: `trovian:v001-criar-tabelas-relatorios`

## Rollback

Sempre adicione comandos de rollback para facilitar reversões:

```sql
--rollback DROP TABLE IF EXISTS minha_tabela;
--rollback ALTER TABLE usuarios DROP COLUMN email;
```

## Comandos Úteis

### Verificar Status
```bash
mvn liquibase:status
```

### Executar Migrations
As migrations são executadas automaticamente ao iniciar a aplicação.

### Rollback da Última Migration
```bash
mvn liquibase:rollback -Dliquibase.rollbackCount=1
```

### Rollback para Data Específica
```bash
mvn liquibase:rollback -Dliquibase.rollbackDate=2026-01-08
```

### Gerar Changelog do Banco Existente
```bash
mvn liquibase:generateChangeLog
```

## Tabelas de Controle

O Liquibase cria duas tabelas:
- **databasechangelog**: Registra todas as migrations executadas
- **databasechangeloglock**: Controla locks durante execução

## Boas Práticas

1. **Nunca modifique** um changeset já executado em produção
2. **Sempre adicione** comandos de rollback
3. **Use comentários** descritivos nos changesets
4. **Teste rollbacks** em ambiente de desenvolvimento
5. **Um changeset = uma funcionalidade** lógica
6. **Mantenha ordem numérica** nos versionamentos

## Troubleshooting

### Erro: "Checksum validation failed"
- Causa: Arquivo de migration foi modificado após ser executado
- Solução: Use `mvn liquibase:clearCheckSums` (cuidado em produção!)

### Erro: "Lock is held"
- Causa: Processo anterior não liberou o lock
- Solução: `mvn liquibase:releaseLocks`

### Erro: "Table already exists"
- Causa: Tabela já foi criada pelo Hibernate (ddl-auto=update)
- Solução: Dropar o banco ou usar `preconditions` no changeset

## Desabilitar Temporariamente

Se necessário, desabilite no application.yml:
```yaml
spring:
  liquibase:
    enabled: false
```

## Migrando do Flyway

Os scripts originais do Flyway foram migrados para o formato Liquibase:
- `migrations/V001__*.sql` → `db/changelog/changes/v001-*.sql`
- Adicionada tag `--liquibase formatted sql`
- Adicionados comandos de rollback

## Referências

- [Documentação Oficial Liquibase](https://docs.liquibase.com)
- [SQL Format](https://docs.liquibase.com/concepts/changelogs/sql-format.html)
- [Best Practices](https://docs.liquibase.com/concepts/bestpractices.html)
