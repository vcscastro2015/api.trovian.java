# Alterações - Persistência de ConsumoDetalhado

## Resumo
Implementação da persistência dos dados de `ConsumoDetalhadoDTO` no banco de dados, permitindo que os cálculos detalhados de consumo de combustível sejam salvos junto com cada viagem.

## Arquivos Criados

### 1. ConsumoDetalhado.java
**Localização:** `src/main/java/com/trovian/entity/ConsumoDetalhado.java`

Entidade JPA que representa os dados detalhados de consumo de combustível. Inclui:
- Totais (consumo total, fator terreno, classificação)
- Consumo por trecho (subida, descida, plano)
- Médias de consumo em L/km e km/L
- Dados do terreno (elevação, inclinação, distâncias)

### 2. ConsumoDetalhadoRepository.java
**Localização:** `src/main/java/com/trovian/repository/ConsumoDetalhadoRepository.java`

Repository JPA padrão para operações com `ConsumoDetalhado`.

### 3. migration_consumo_detalhado.sql
**Localização:** `migration_consumo_detalhado.sql`

Script SQL para criar a estrutura no banco de dados:
- Cria tabela `consumo_detalhado`
- Adiciona colunas `consumo_detalhado_ida_id` e `consumo_detalhado_volta_id` na tabela `viagem`
- Cria foreign keys com `ON DELETE CASCADE`
- Adiciona índices para performance

## Arquivos Modificados

### 1. Viagem.java
**Localização:** `src/main/java/com/trovian/entity/Viagem.java`

**Alterações:**
- Adicionados relacionamentos `@OneToOne` para `consumoDetalhadoIda` e `consumoDetalhadoVolta`
- Configurado `cascade = CascadeType.ALL` e `orphanRemoval = true` para gerenciamento automático

```java
@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "consumo_detalhado_ida_id")
private ConsumoDetalhado consumoDetalhadoIda;

@OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
@JoinColumn(name = "consumo_detalhado_volta_id")
private ConsumoDetalhado consumoDetalhadoVolta;
```

### 2. ViagemService.java
**Localização:** `src/main/java/com/trovian/service/ViagemService.java`

**Alterações:**

#### Injeção de Dependência
- Adicionado `ConsumoDetalhadoRepository` às dependências

#### Novos Métodos
1. `toConsumoDetalhadoEntity(ConsumoDetalhadoDTO dto)` - Converte DTO para entidade
2. `toConsumoDetalhadoDTO(ConsumoDetalhado entity)` - Converte entidade para DTO
3. `updateConsumoDetalhadoEntity(ConsumoDetalhado entity, ConsumoDetalhadoDTO dto)` - Atualiza entidade existente

#### Métodos Modificados

**`toDTO(Viagem viagem)`**
- Adiciona conversão dos dados de consumo detalhado ao retornar DTO

**`toEntity(ViagemDTO dto)`**
- Cria entidades `ConsumoDetalhado` a partir dos DTOs calculados
- As entidades são automaticamente salvas devido ao `cascade = CascadeType.ALL`

**`updateEntity(Viagem viagem, ViagemDTO dto)`**
- Atualiza entidades `ConsumoDetalhado` existentes ou cria novas se necessário
- Remove entidades órfãs automaticamente

## Como Funciona

### 1. Fluxo de Criação de Viagem

```
1. Frontend envia dados básicos para calcular
2. ViagemService.calcular() executa cálculos
3. calcularFatorTerreno() popula consumoDetalhadoIda e consumoDetalhadoVolta no DTO
4. Frontend chama POST /viagens para salvar
5. ViagemService.create() chama calcular() novamente
6. toEntity() converte DTOs em entidades (incluindo ConsumoDetalhado)
7. viagemRepository.save() persiste tudo (Viagem + ConsumoDetalhado)
```

### 2. Fluxo de Atualização de Viagem

```
1. Frontend envia dados atualizados
2. ViagemService.update() busca viagem existente
3. calcular() recalcula tudo
4. updateEntity() atualiza ou cria ConsumoDetalhado
5. viagemRepository.save() persiste as mudanças
```

### 3. Fluxo de Consulta

```
1. Frontend chama GET /viagens/{id}
2. viagemRepository.findById() carrega Viagem + ConsumoDetalhado
3. toDTO() converte entidades em DTOs
4. Frontend recebe dados completos incluindo consumoDetalhadoIda e consumoDetalhadoVolta
```

## Cascade e Orphan Removal

O relacionamento `@OneToOne` foi configurado com:

- **`cascade = CascadeType.ALL`**: Todas as operações (persist, merge, remove, etc.) na `Viagem` são propagadas para `ConsumoDetalhado`
- **`orphanRemoval = true`**: Se um `ConsumoDetalhado` for removido da `Viagem`, ele será automaticamente deletado do banco

Isso significa:
- ✅ Não precisa salvar `ConsumoDetalhado` manualmente
- ✅ Não precisa deletar `ConsumoDetalhado` manualmente
- ✅ Atualizar `Viagem` atualiza automaticamente `ConsumoDetalhado`
- ✅ Deletar `Viagem` deleta automaticamente `ConsumoDetalhado`

## Executar Migração

Para aplicar as mudanças no banco de dados:

```bash
# Opção 1: MySQL Command Line
mysql -u seu_usuario -p seu_banco < migration_consumo_detalhado.sql

# Opção 2: MySQL Workbench
# Abra o arquivo migration_consumo_detalhado.sql e execute

# Opção 3: DBeaver/HeidiSQL
# Importe e execute o script SQL
```

## Verificação

Após executar a migração, você pode verificar:

```sql
-- Verificar estrutura da tabela consumo_detalhado
DESCRIBE consumo_detalhado;

-- Verificar novas colunas na tabela viagem
SHOW COLUMNS FROM viagem LIKE 'consumo_detalhado%';

-- Verificar foreign keys
SELECT
    CONSTRAINT_NAME,
    TABLE_NAME,
    REFERENCED_TABLE_NAME
FROM information_schema.KEY_COLUMN_USAGE
WHERE TABLE_NAME = 'viagem'
AND CONSTRAINT_NAME LIKE 'fk_viagem_consumo%';
```

## Rollback (se necessário)

Para reverter as mudanças:

```sql
-- Remover foreign keys
ALTER TABLE viagem DROP FOREIGN KEY fk_viagem_consumo_detalhado_ida;
ALTER TABLE viagem DROP FOREIGN KEY fk_viagem_consumo_detalhado_volta;

-- Remover colunas
ALTER TABLE viagem DROP COLUMN consumo_detalhado_ida_id;
ALTER TABLE viagem DROP COLUMN consumo_detalhado_volta_id;

-- Remover tabela
DROP TABLE consumo_detalhado;
```

## Próximos Passos

1. ✅ Executar script de migração no banco de dados
2. ✅ Testar criação de viagem com consumo detalhado
3. ✅ Testar atualização de viagem
4. ✅ Testar consulta de viagem existente
5. ✅ Verificar se dados são persistidos corretamente
6. ✅ Testar deleção de viagem (verificar se ConsumoDetalhado é removido)

## Observações Importantes

- O `ConsumoDetalhadoDTO` já existia e já estava sendo calculado no `ViagemService`
- Agora os dados calculados são persistidos no banco junto com a viagem
- Não há impacto nas APIs existentes - o comportamento externo permanece o mesmo
- A única diferença é que agora os dados são salvos e podem ser recuperados posteriormente
