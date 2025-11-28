# Histórico de Desenvolvimento - API Trovian

## 📋 Informações do Projeto

**Nome**: API Trovian
**Linguagem**: Java 17
**Framework**: Spring Boot 3.2.1
**Banco de Dados**: PostgreSQL 15
**Build Tool**: Maven
**Arquitetura**: Camadas (Controller → Service → Repository → Entity)

---

## 🏗️ Estrutura do Projeto

```
api.trovian.java/
├── src/main/java/com/trovian/
│   ├── Application.java                    # Entry point Spring Boot
│   ├── config/
│   │   ├── JmsConfig.java                 # Configuração ActiveMQ/JMS
│   │   └── OpenApiConfig.java             # Configuração Swagger/OpenAPI
│   ├── controller/                         # Camada REST
│   │   ├── ProductController.java
│   │   ├── CooperativaController.java
│   │   └── ClienteController.java
│   ├── dto/                                # Data Transfer Objects
│   │   ├── ProductDTO.java
│   │   ├── CooperativaDTO.java
│   │   └── ClienteDTO.java
│   ├── entity/                             # Entidades JPA
│   │   ├── Product.java
│   │   ├── Cooperativa.java
│   │   └── Cliente.java
│   ├── jms/                                # Mensageria
│   │   ├── MessageProducer.java
│   │   └── MessageListener.java
│   ├── repository/                         # Camada de dados
│   │   ├── ProductRepository.java
│   │   ├── CooperativaRepository.java
│   │   └── ClienteRepository.java
│   └── service/                            # Lógica de negócio
│       ├── ProductService.java
│       ├── CooperativaService.java
│       └── ClienteService.java
├── src/main/resources/
│   └── application.yml                     # Configurações da aplicação
├── docker-compose.yml                      # Serviços Docker (PostgreSQL, ActiveMQ)
└── pom.xml                                 # Dependências Maven
```

---

## 🛠️ Tecnologias e Dependências

### Core Framework
- **Spring Boot 3.2.1** - Framework principal
- **Spring Web** - REST APIs
- **Spring Data JPA** - Persistência com Hibernate
- **Spring Validation** - Validação de dados (Jakarta Validation)
- **Spring Actuator** - Monitoramento e métricas
- **Spring ActiveMQ** - Mensageria JMS

### Banco de Dados
- **PostgreSQL Driver** - Conexão com PostgreSQL
- **Hibernate** - ORM
- **HikariCP** - Pool de conexões (10 max, 5 min)

### Ferramentas
- **Lombok** - Redução de boilerplate (getters, setters, constructors)
- **SpringDoc OpenAPI 2.3.0** - Documentação Swagger/OpenAPI 3
- **SLF4J** - Logging

### Testes
- **Spring Boot Test** - Testes integrados
- **JUnit 5** - Framework de testes
- **Mockito** - Mocks para testes

---

## 📦 Implementações Realizadas

### Listar Todos os Veículos (Paginado)
```bash
curl -X GET "http://localhost:8080/veiculo?page=0&size=10&sortBy=placa&direction=ASC"
```

### Buscar Veículo por ID
```bash
curl -X GET http://localhost:8080/veiculo/1
```

### Buscar Veículos por Cliente (Paginado)
```bash
curl -X GET "http://localhost:8080/veiculo/cliente/1?page=0&size=20&sortBy=placa&direction=ASC"
```

### Atualizar Veículo
```bash
curl -X PUT http://localhost:8080/veiculo/1 \
  -H "Content-Type: application/json" \
  -d '{
    "anoFabricacao": "2023",
    "anoModelo": "2024",
    "chassi": "9BWZZZ377VT004251",
    "cor": "Azul",
    "placa": "ABC-1234",
    "renavam": "12345678901",
    "tipo": "Carro",
    "status": true,
    "observacao": "Veículo atualizado - nova cor",
    "velocidadeMaxima": 130,
    "combustivel": "Gasolina",
    "modeloId": 1,
    "equipamentoId": 2,
    "clienteId": 1
  }'
```

### Deletar Veículo
```bash
curl -X DELETE http://localhost:8080/veiculo/1
```

---

#### Características Técnicas

1. **Modelo de Dados Completo**: 50+ atributos para configuração detalhada de veículos
2. **Relacionamentos Múltiplos**: ManyToOne com Modelo, Cliente e Equipamento
3. **Equipamento Opcional**: Veículo pode existir sem equipamento associado
4. **Validações Robustas**: Tipo de veículo e combustível validados
5. **Paginação Nativa**: Todos endpoints de listagem suportam paginação
6. **Configurações de RPM**: Suporte completo para faixas de RPM (azul, verde, amarela, econômica, marcha lenta)
7. **Alertas Configuráveis**: Diversos alertas (velocidade, bateria, comunicação, etc.)
8. **Configurações de Rota**: Validação de rota, cerca virtual, geração de endereço automático
9. **Auditoria Completa**: Rastreamento de criação e atualização
10. **Documentação Swagger**: API totalmente documentada com exemplos

---

## 🎯 Padrões e Convenções Adotados

### Nomenclatura
- **Entities**: Singular (Product, Cooperativa)
- **DTOs**: Sufixo "DTO" (ProductDTO, CooperativaDTO)
- **Repositories**: Sufixo "Repository"
- **Services**: Sufixo "Service"
- **Controllers**: Sufixo "Controller"

### Estrutura de Camadas
```
Controller (REST)
    ↓
Service (Lógica de Negócio + DTO)
    ↓
Repository (Spring Data JPA)
    ↓
Entity (JPA/Hibernate)
    ↓
Database (PostgreSQL)
```

### Annotations Lombok
- `@Data` - Gera getters, setters, toString, equals, hashCode
- `@NoArgsConstructor` - Construtor vazio
- `@AllArgsConstructor` - Construtor com todos os campos
- `@RequiredArgsConstructor` - Injeção de dependências (final fields)
- `@Slf4j` - Logger SLF4J

### Validações
- Jakarta Validation (Bean Validation 3.0)
- `@NotBlank` - Strings não vazias
- `@NotNull` - Campos obrigatórios
- `@Size` - Tamanho min/max
- `@Positive` - Números positivos
- `@Valid` - Ativa validação no Controller

### Transações
- `@Transactional` - Operações de escrita (create, update, delete)
- `@Transactional(readOnly = true)` - Operações de leitura (otimização)

### HTTP Status Codes
- **200 OK** - Sucesso em GET/PUT
- **201 Created** - Sucesso em POST
- **204 No Content** - Sucesso em DELETE
- **404 Not Found** - Recurso não encontrado
- **400 Bad Request** - Validação falhou

### Logging
```java
log.info("Mensagem informativa");
log.error("Mensagem de erro", exception);
log.debug("Mensagem de debug");
```

### Documentação Swagger
- `@Tag` - Agrupa endpoints
- `@Operation` - Descreve operação
- `@ApiResponses` - Possíveis respostas HTTP
- `@Parameter` - Documenta parâmetros
- `@Schema` - Documenta DTOs

---

## 🔧 Configurações

### Banco de Dados (application.yml)
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/trovian_db
    username: trovian_user
    password: trovian_pass
  jpa:
    hibernate:
      ddl-auto: update  # Cria/atualiza schema automaticamente
    show-sql: true
```

### Servidor
```yaml
server:
  port: 8080
  servlet:
    context-path: /api
```

### Swagger UI
- URL: `http://localhost:8080/api/swagger-ui/index.html`

### Actuator
- Health: `http://localhost:8080/api/actuator/health`
- Metrics: `http://localhost:8080/api/actuator/metrics`

---

## 📝 Notas de Desenvolvimento

### Decisões Técnicas

1. **Jakarta Persistence vs javax.persistence**
   - Projeto usa Jakarta (Spring Boot 3+)
   - Migração do javax para jakarta

2. **Conversão DTO ↔ Entity**
   - Conversão manual nos Services
   - Alternativa futura: MapStruct ou ModelMapper

3. **Exception Handling**
   - Atualmente: RuntimeException com mensagens descritivas
   - Melhoria futura: Criar exceptions customizadas e @ControllerAdvice

4. **CNPJ como String**
   - Armazenado como String para manter formatação
   - Validação de formato pode ser adicionada futuramente

5. **Endpoint Base Path**
   - Product: `/api/products`
   - Cooperativa: `/cooperativa` (sem `/api` - já está no context-path)

### Melhorias Futuras Sugeridas

1. **Exception Handling Global**
   - Criar `@ControllerAdvice` para tratamento centralizado
   - Exceptions customizadas (CooperativaNaoEncontradaException, etc.)
   - Response padronizada de erros

2. **Paginação**
   - Adicionar suporte a Pageable nos métodos `findAll()`
   - Retornar Page<T> ao invés de List<T>

3. **Validação de CNPJ**
   - Implementar validação de formato e dígitos verificadores
   - Criar annotation customizada `@CNPJ`

4. **Testes Unitários e Integração**
   - Testes para Services (Mockito)
   - Testes para Controllers (MockMvc)
   - Testes de integração com banco H2

5. **Auditoria Avançada**
   - Implementar Spring Data JPA Auditing
   - Campos `createdBy` e `updatedBy`

6. **Soft Delete**
   - Implementar deleção lógica ao invés de física
   - Adicionar campo `deleted` (Boolean) e `deletedAt` (LocalDateTime)

7. **Cache**
   - Implementar Spring Cache para buscas frequentes
   - Redis como cache distribuído

8. **Mensageria**
   - Enviar eventos JMS quando cooperativa for criada/atualizada
   - Integração com sistema de notificações

9. **Segurança**
   - Adicionar Spring Security
   - Autenticação JWT
   - Autorização por roles (ADMIN, USER)

10. **Internacionalização**
    - Mensagens de erro em português/inglês
    - `MessageSource` para i18n

---

## 🚀 Como Executar

### Pré-requisitos
```bash
# Java 17+
java -version

# Maven 3.6+
mvn -version

# Docker (para PostgreSQL e ActiveMQ)
docker --version
```

### Iniciar Serviços Docker
```bash
docker-compose up -d
```

### Compilar e Executar
```bash
# Compilar
mvn clean install

# Executar
mvn spring-boot:run

# Ou executar JAR
java -jar target/api-trovian-1.0.0.jar
```

### Acessar Aplicação
- **API Base**: http://localhost:8080/api
- **Swagger UI**: http://localhost:8080/api/swagger-ui/index.html
- **Health Check**: http://localhost:8080/api/actuator/health

---

## 🧪 Exemplos de Uso (cURL)

### Criar Cooperativa
```bash
curl -X POST http://localhost:8080/cooperativa \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cooperativa Central do Brasil",
    "cnpj": "12.345.678/0001-90",
    "endereco": "Av. Paulista, 1000",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100",
    "ativa": true
  }'
```

### Listar Todas Cooperativas
```bash
curl -X GET http://localhost:8080/cooperativa
```

### Buscar por ID
```bash
curl -X GET http://localhost:8080/cooperativa/1
```

### Buscar por CNPJ
```bash
curl -X GET http://localhost:8080/cooperativa/cnpj/12.345.678/0001-90
```

### Atualizar Cooperativa
```bash
curl -X PUT http://localhost:8080/cooperativa/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Cooperativa Central do Brasil - Atualizada",
    "cnpj": "12.345.678/0001-90",
    "endereco": "Av. Paulista, 2000",
    "cidade": "São Paulo",
    "uf": "SP",
    "cep": "01310-100",
    "ativa": true
  }'
```

### Deletar Cooperativa
```bash
curl -X DELETE http://localhost:8080/cooperativa/1
```

### Buscar por Cidade
```bash
curl -X GET http://localhost:8080/cooperativa/cidade/São%20Paulo
```

### Buscar por UF
```bash
curl -X GET http://localhost:8080/cooperativa/uf/SP
```

### Buscar Ativas
```bash
curl -X GET http://localhost:8080/cooperativa/ativa/true
```

---

### 6. CRUD Local
**Data**: 02/11/2025
**Branch**: `feature/local`

#### Atributos Implementados

**Dados Básicos**
- **id** (Long) - Chave primária, auto-gerado
- **nome** (String, max 255) - Obrigatório
- **ativo** (Boolean) - Obrigatório, Default: true
- **codigoUnico** (Integer) - Opcional
- **mostrarNoMapaPrincipal** (Boolean) - Opcional
- **mostrarNomeNoMapa** (Boolean) - Opcional
- **notificaEvento** (Boolean) - Opcional
- **permiteDescanso** (Boolean) - Opcional

**Endereço**
- **endereco** (String) - Opcional
- **bairro** (String) - Opcional
- **complemento** (String) - Opcional
- **cidade** (String) - Opcional
- **uf** (String, 2 chars) - Opcional

**Enums**
- **funcao** (FuncaoLocal) - CARGA, DESCARGA, OUTROS
- **tipo** (TipoLocal) - EMPRESA, OFICINA, POSTO_DE_ABASTECIMENTO, POSTO_DE_FISCALIZACAO

**Relacionamentos**
- **cliente** (ManyToOne com Cliente) - Obrigatório
- **listaDeCoordenadas** (OneToMany com Coordenada) - Lista de coordenadas do local
- **parametroLocal** (OneToOne com ParametroLocal) - Parâmetros do local

**Auditoria**
- **dataCadastro** (Date) - Auto-gerado
- **updatedAt** (LocalDateTime) - Auto-atualizado

#### Entidade Coordenada

**Atributos**:
- **id** (Long) - Chave primária
- **sequencia** (Integer) - Ordem da coordenada
- **latitude** (Double) - Coordenada de latitude
- **longitude** (Double) - Coordenada de longitude
- **isRaio** (Boolean) - Indica se usa raio
- **raio** (Double) - Raio em metros

**Relacionamento**:
- **local** (ManyToOne com Local) - Local ao qual pertence

#### Entidade ParametroLocal

**Atributos**:
- **id** (Long) - Chave primária
- **limiteVeiculosMesmoLocal** (Integer) - Limite de veículos
- **tempoMinimoDePermanencia** (Integer) - Tempo mínimo em minutos
- **tempoMaximoDePermanencia** (Integer) - Tempo máximo em minutos

#### Arquivos Criados

**Enums**
```java
src/main/java/com/trovian/enums/FuncaoLocal.java
src/main/java/com/trovian/enums/TipoLocal.java
```
- Enumerações para função (CARGA, DESCARGA, OUTROS)
- Enumerações para tipo (EMPRESA, OFICINA, POSTO_DE_ABASTECIMENTO, POSTO_DE_FISCALIZACAO)

**Entities**
```java
src/main/java/com/trovian/entity/ParametroLocal.java
src/main/java/com/trovian/entity/Coordenada.java
src/main/java/com/trovian/entity/Local.java
```
- Anotações JPA (@Entity, @Table, @Id, @GeneratedValue)
- Validações Jakarta (@NotBlank, @NotNull, @Size)
- Relacionamentos:
  - Local → Cliente (ManyToOne, obrigatório)
  - Local → Coordenada (OneToMany, cascade ALL, orphanRemoval)
  - Local → ParametroLocal (OneToOne, cascade ALL)
  - Coordenada → Local (ManyToOne)
- Auditoria com @PrePersist e @PreUpdate
- Helper methods para gerenciar coordenadas (addCoordenada, removeCoordenada)
- Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)

**DTOs**
```java
src/main/java/com/trovian/dto/ParametroLocalDTO.java
src/main/java/com/trovian/dto/CoordenadaDTO.java
src/main/java/com/trovian/dto/LocalDTO.java
```
- Validações de entrada completas
- Documentação Swagger (@Schema)
- Campos read-only (id, dataCadastro, updatedAt, clienteNome)
- Expõe relacionamento via clienteId + nome do cliente
- Listas de coordenadas e parâmetro local aninhados

**Repository**
```java
src/main/java/com/trovian/repository/LocalRepository.java
```
- Extende JpaRepository<Local, Long>
- Query method customizado:
  - `findByClienteId(Long clienteId, Pageable pageable)` - Busca locais por cliente com paginação

**Service**
```java
src/main/java/com/trovian/service/LocalService.java
```
- CRUD completo com transações
- Validação de existência do Cliente antes de salvar/atualizar
- Gerenciamento de coordenadas (OneToMany com cascade)
- Gerenciamento de parâmetro local (OneToOne com cascade)
- Logs SLF4J em todas operações
- Conversão manual DTO ↔ Entity
- Métodos de conversão para entidades aninhadas (Coordenada, ParametroLocal)

**Controller**
```java
src/main/java/com/trovian/controller/LocalController.java
```
- Base path: `/local`
- Documentação Swagger completa
- Response entities com status HTTP corretos
- Suporte a paginação e ordenação customizável
- 3 endpoints GET (conforme especificado)

#### Endpoints REST - Local

| Método | Endpoint | Descrição | Status Code |
|--------|----------|-----------|-------------|
| GET | `/local` | Lista todos locais (paginado) | 200 |
| GET | `/local/{id}` | Busca local por ID | 200 / 404 |
| GET | `/local/cliente/{clienteId}` | Busca locais por cliente (paginado) | 200 |
| POST | `/local` | Cria novo local | 201 |
| PUT | `/local/{id}` | Atualiza local | 200 / 404 |
| DELETE | `/local/{id}` | Deleta local | 204 / 404 |

#### Parâmetros de Paginação

Os endpoints `/local` e `/local/cliente/{clienteId}` suportam os seguintes parâmetros:

| Parâmetro | Tipo | Padrão | Descrição |
|-----------|------|--------|-----------|
| page | int | 0 | Número da página (inicia em 0) |
| size | int | 10 | Tamanho da página |
| sortBy | String | id | Campo para ordenação |
| direction | String | ASC | Direção da ordenação (ASC ou DESC) |

#### Regras de Negócio

1. **Campos Obrigatórios**: id, nome e ativo
2. **Cliente Obrigatório**: Todo local deve estar vinculado a um cliente válido
3. **Validação de Enums**:
   - Função aceita apenas: CARGA, DESCARGA, OUTROS
   - Tipo aceita apenas: EMPRESA, OFICINA, POSTO_DE_ABASTECIMENTO, POSTO_DE_FISCALIZACAO
4. **Status Padrão**: Local criado como ativo (true) por padrão
5. **Coordenadas em Cascata**:
   - Coordenadas são salvas/atualizadas/removidas junto com o local
   - Orphan removal ativo (coordenadas órfãs são removidas)
6. **Parâmetro Local em Cascata**: ParametroLocal é salvo/atualizado junto com o local
7. **Auditoria Automática**:
   - `dataCadastro` definido no momento da criação (Date)
   - `updatedAt` atualizado em cada modificação (LocalDateTime)

---

## 🧪 Exemplos de Uso - Local (cURL)

### Criar Local Completo
```bash
curl -X POST http://localhost:8080/local \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Armazém Central SP",
    "ativo": true,
    "codigoUnico": 1001,
    "mostrarNoMapaPrincipal": true,
    "mostrarNomeNoMapa": true,
    "notificaEvento": true,
    "funcao": "CARGA",
    "endereco": "Av. Paulista, 1000",
    "bairro": "Bela Vista",
    "complemento": "Galpão 5",
    "cidade": "São Paulo",
    "uf": "SP",
    "tipo": "EMPRESA",
    "permiteDescanso": false,
    "clienteId": 1,
    "listaDeCoordenadas": [
      {
        "sequencia": 1,
        "latitude": -23.5505,
        "longitude": -46.6333,
        "isRaio": true,
        "raio": 500.0
      },
      {
        "sequencia": 2,
        "latitude": -23.5510,
        "longitude": -46.6340,
        "isRaio": false
      }
    ],
    "parametroLocal": {
      "limiteVeiculosMesmoLocal": 10,
      "tempoMinimoDePermanencia": 30,
      "tempoMaximoDePermanencia": 120
    }
  }'
```

### Criar Local Simples (apenas campos obrigatórios)
```bash
curl -X POST http://localhost:8080/local \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Posto de Combustível BR",
    "ativo": true,
    "clienteId": 1
  }'
```

### Listar Todos os Locais (Paginado)
```bash
curl -X GET "http://localhost:8080/local?page=0&size=10&sortBy=nome&direction=ASC"
```

### Buscar Local por ID
```bash
curl -X GET http://localhost:8080/local/1
```

### Buscar Locais por Cliente (Paginado)
```bash
curl -X GET "http://localhost:8080/local/cliente/1?page=0&size=20&sortBy=nome&direction=ASC"
```

### Atualizar Local
```bash
curl -X PUT http://localhost:8080/local/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Armazém Central SP - Atualizado",
    "ativo": true,
    "codigoUnico": 1001,
    "mostrarNoMapaPrincipal": true,
    "mostrarNomeNoMapa": true,
    "notificaEvento": false,
    "funcao": "DESCARGA",
    "endereco": "Av. Paulista, 2000",
    "bairro": "Bela Vista",
    "complemento": "Galpão 7",
    "cidade": "São Paulo",
    "uf": "SP",
    "tipo": "EMPRESA",
    "permiteDescanso": true,
    "clienteId": 1,
    "listaDeCoordenadas": [
      {
        "sequencia": 1,
        "latitude": -23.5515,
        "longitude": -46.6350,
        "isRaio": true,
        "raio": 800.0
      }
    ],
    "parametroLocal": {
      "limiteVeiculosMesmoLocal": 15,
      "tempoMinimoDePermanencia": 45,
      "tempoMaximoDePermanencia": 180
    }
  }'
```

### Deletar Local
```bash
curl -X DELETE http://localhost:8080/local/1
```

---

#### Características Técnicas

1. **Relacionamentos Complexos**: OneToMany com Coordenada e OneToOne com ParametroLocal
2. **Cascade Operations**: Coordenadas e parâmetros salvos/atualizados/removidos em cascata
3. **Orphan Removal**: Coordenadas órfãs são automaticamente removidas
4. **Enums Tipados**: FuncaoLocal e TipoLocal para valores controlados
5. **Paginação Nativa**: Todos endpoints de listagem suportam paginação
6. **Helper Methods**: Métodos auxiliares para gerenciar coordenadas (addCoordenada, removeCoordenada)
7. **Validações Robustas**: Nome obrigatório, cliente obrigatório
8. **Flexibilidade**: Coordenadas e parâmetros são opcionais
9. **Auditoria Completa**: Rastreamento de criação e atualização
10. **Documentação Swagger**: API totalmente documentada com exemplos

---

### 7. CRUD Motorista
**Data**: 06/11/2025
**Branch**: `feature/motorista`

#### Atributos Implementados

**Dados Pessoais**
- **id** (Long) - Chave primária, auto-gerado
- **nome** (String, 100 chars) - Obrigatório
- **dataNascimento** (Date) - Obrigatório
- **sexo** (Enum Sexo) - MASCULINO ou FEMININO
- **cpf** (String, 11 chars) - Obrigatório

**Documentação CNH**
- **numeroCnh** (String, 15 chars) - Obrigatório
- **validadeCnh** (Date) - Obrigatório
- **categoriaCnh** (String, 2 chars) - Obrigatório (A, B, C, D, E, AB, AC, AD, AE)
- **dataAdmissao** (Date) - Opcional

**Contato**
- **telefone** (String, 15 chars) - Opcional

**Endereço**
- **logradouro** (String) - Opcional
- **numero** (String) - Opcional
- **bairro** (String, 45 chars) - Opcional
- **cep** (String, 9 chars) - Opcional
- **complemento** (String, 50 chars) - Opcional
- **cidade** (String) - Opcional
- **uf** (String, 2 chars) - Opcional

**Relacionamentos**
- **cliente** (ManyToOne com Cliente) - Obrigatório

**Auditoria**
- **dataCadastro** (Date) - Auto-gerado
- **updatedAt** (LocalDateTime) - Auto-atualizado

#### Arquivos Criados

**Enum**
```java
src/main/java/com/trovian/enums/Sexo.java
```
- Valores: MASCULINO, FEMININO
- Campo descricao para exibição

**Entity**
```java
src/main/java/com/trovian/entity/Motorista.java
```
- Anotações JPA (@Entity, @Table, @Id, @GeneratedValue)
- Validações Jakarta (@NotBlank, @NotNull, @Size)
- Relacionamento ManyToOne com Cliente (FetchType.LAZY, obrigatório)
- Enum Sexo com @Enumerated(EnumType.STRING)
- Auditoria com @PrePersist e @PreUpdate
- @Temporal para campos Date
- Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)

**DTO**
```java
src/main/java/com/trovian/dto/MotoristaDTO.java
```
- Validações de entrada completas
- Documentação Swagger (@Schema)
- Campos read-only (id, dataCadastro, updatedAt, clienteNome)
- Expõe relacionamento via clienteId + nome do cliente
- allowableValues para campo sexo

**Repository**
```java
src/main/java/com/trovian/repository/MotoristaRepository.java
```
- Extende JpaRepository<Motorista, Long>
- Query method customizado:
  - `findByClienteId(Long clienteId, Pageable pageable)` - Busca motoristas por cliente com paginação

**Service**
```java
src/main/java/com/trovian/service/MotoristaService.java
```
- CRUD completo com transações
- Validação de existência do Cliente antes de salvar/atualizar
- Logs SLF4J em todas operações
- Conversão manual DTO ↔ Entity
- Métodos de busca com paginação
- toDTO inclui informações do Cliente relacionado

**Controller**
```java
src/main/java/com/trovian/controller/MotoristaController.java
```
- Base path: `/motorista`
- Documentação Swagger completa
- Response entities com status HTTP corretos
- Suporte a paginação e ordenação customizável
- 3 endpoints GET (conforme especificado)

#### Endpoints REST - Motorista

| Método | Endpoint | Descrição | Status Code |
|--------|----------|-----------|-------------|
| GET | `/motorista` | Lista todos motoristas (paginado) | 200 |
| GET | `/motorista/{id}` | Busca motorista por ID | 200 / 404 |
| GET | `/motorista/cliente/{clienteId}` | Busca motoristas por cliente (paginado) | 200 |
| POST | `/motorista` | Cria novo motorista | 201 |
| PUT | `/motorista/{id}` | Atualiza motorista | 200 / 404 |
| DELETE | `/motorista/{id}` | Deleta motorista | 204 / 404 |

#### Parâmetros de Paginação

Os endpoints `/motorista` e `/motorista/cliente/{clienteId}` suportam os seguintes parâmetros:

| Parâmetro | Tipo | Padrão | Descrição |
|-----------|------|--------|-----------|
| page | int | 0 | Número da página (inicia em 0) |
| size | int | 10 | Tamanho da página |
| sortBy | String | id | Campo para ordenação |
| direction | String | ASC | Direção da ordenação (ASC ou DESC) |

#### Regras de Negócio

1. **Campos Obrigatórios**: nome, dataNascimento, cpf, numeroCnh, validadeCnh, categoriaCnh e cliente
2. **Cliente Obrigatório**: Todo motorista deve estar vinculado a um cliente válido
3. **Validação de Sexo**: Campo aceita apenas MASCULINO ou FEMININO
4. **Auditoria Automática**:
   - `dataCadastro` definido no momento da criação (Date)
   - `updatedAt` atualizado em cada modificação (LocalDateTime)

---

## 🧪 Exemplos de Uso - Motorista (cURL)

### Criar Motorista Completo
```bash
curl -X POST http://localhost:8080/motorista \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva",
    "dataNascimento": "1985-03-15",
    "sexo": "MASCULINO",
    "cpf": "12345678900",
    "numeroCnh": "12345678901",
    "validadeCnh": "2026-12-31",
    "dataAdmissao": "2020-01-10",
    "categoriaCnh": "D",
    "telefone": "11987654321",
    "logradouro": "Rua das Flores",
    "numero": "123",
    "bairro": "Centro",
    "cep": "12345-678",
    "complemento": "Apto 101",
    "cidade": "São Paulo",
    "uf": "SP",
    "clienteId": 1
  }'
```

### Criar Motorista Simples (apenas campos obrigatórios)
```bash
curl -X POST http://localhost:8080/motorista \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "Maria Santos",
    "dataNascimento": "1990-07-20",
    "sexo": "FEMININO",
    "cpf": "98765432100",
    "numeroCnh": "98765432109",
    "validadeCnh": "2027-06-30",
    "categoriaCnh": "C",
    "clienteId": 1
  }'
```

### Listar Todos os Motoristas (Paginado)
```bash
curl -X GET "http://localhost:8080/motorista?page=0&size=10&sortBy=nome&direction=ASC"
```

### Buscar Motorista por ID
```bash
curl -X GET http://localhost:8080/motorista/1
```

### Buscar Motoristas por Cliente (Paginado)
```bash
curl -X GET "http://localhost:8080/motorista/cliente/1?page=0&size=20&sortBy=nome&direction=ASC"
```

### Atualizar Motorista
```bash
curl -X PUT http://localhost:8080/motorista/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nome": "João da Silva - Atualizado",
    "dataNascimento": "1985-03-15",
    "sexo": "MASCULINO",
    "cpf": "12345678900",
    "numeroCnh": "12345678901",
    "validadeCnh": "2028-12-31",
    "dataAdmissao": "2020-01-10",
    "categoriaCnh": "E",
    "telefone": "11999887766",
    "logradouro": "Rua das Flores",
    "numero": "456",
    "bairro": "Centro",
    "cep": "12345-678",
    "complemento": "Apto 202",
    "cidade": "São Paulo",
    "uf": "SP",
    "clienteId": 1
  }'
```

### Deletar Motorista
```bash
curl -X DELETE http://localhost:8080/motorista/1
```

---

#### Características Técnicas

1. **Enum Sexo**: Tipagem forte para gênero (MASCULINO/FEMININO)
2. **Relacionamento ManyToOne**: Vários motoristas podem pertencer a um cliente
3. **Lazy Loading**: Cliente carregado somente quando acessado
4. **Validações Robustas**: Campos obrigatórios e tamanhos máximos
5. **Paginação Nativa**: Todos endpoints de listagem suportam paginação
6. **Endereço Completo**: Campos separados para endereço detalhado
7. **Documentação CNH**: Validação de CNH com número, validade e categoria
8. **Auditoria Completa**: Rastreamento de criação e atualização
9. **Documentação Swagger**: API totalmente documentada com exemplos

---

## 📊 Status do Projeto

### ✅ Concluído
- [x] CRUD Product (exemplo base)
- [x] CRUD Cooperativa completo
- [x] CRUD Cliente completo com UUID e relacionamento
- [x] CRUD Modelo completo (Equipamentos e Veículos)
- [x] CRUD Equipamento completo com relacionamento ManyToOne
- [x] CRUD Veículo completo com relacionamentos múltiplos
- [x] CRUD Local completo com relacionamentos OneToMany e OneToOne
- [x] CRUD Motorista completo com enum Sexo e relacionamento ManyToOne
- [x] Validações de dados
- [x] Documentação Swagger
- [x] Logs SLF4J
- [x] Transações JPA
- [x] Auditoria básica (timestamps)
- [x] Buscas customizadas
- [x] Paginação (Product, Cooperativa, Cliente, Modelo, Equipamento, Veículo, Local e Motorista)
- [x] Validação de tipo para Modelo (Equipamento/Veiculo)
- [x] Validação de tipo para Equipamento (PR/PA)
- [x] Validação de tipo para Veículo (Moto, Carro, Onibus, Caminhao, Carreta, Implemento)
- [x] Validação de combustível para Veículo (Gasolina, Alcool, Diesel)
- [x] Validação de enums para Local (FuncaoLocal e TipoLocal)
- [x] Validação de enum Sexo para Motorista (MASCULINO/FEMININO)
- [x] Relacionamento Equipamento-Modelo
- [x] Relacionamento Veiculo-Modelo-Equipamento-Cliente
- [x] Relacionamento Local-Cliente-Coordenada-ParametroLocal
- [x] Relacionamento Motorista-Cliente
- [x] Cascade operations (OneToMany e OneToOne)
- [x] Orphan removal para coordenadas

### 🔄 Em Desenvolvimento
- [ ] Testes unitários e integração
- [ ] Exception handling global
- [ ] Validação de CNPJ

### 📋 Backlog
- [ ] Cache
- [ ] Segurança (JWT)
- [ ] Soft delete
- [ ] Auditoria avançada
- [ ] Internacionalização
- [ ] Mensageria para eventos de cooperativa

---

## 📚 Referências

- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Jakarta Validation](https://jakarta.ee/specifications/bean-validation/)
- [Lombok](https://projectlombok.org/)
- [SpringDoc OpenAPI](https://springdoc.org/)

---

## 🔄 Changelog

### v1.8.1 - 06/11/2025
- ✨ Adicionados 3 novos campos no modelo Veículo:
  - `capacidadeTanque` (BigDecimal) - Capacidade do tanque em litros
  - `numeroEixos` (Integer) - Número de eixos do veículo
  - `tara` (BigDecimal) - Tara do veículo em toneladas
- 🔄 Atualizada Entity Veiculo com novos campos
- 🔄 Atualizado DTO VeiculoDTO com documentação Swagger
- 🔄 Atualizado VeiculoService com conversões DTO ↔ Entity

### v1.8.0 - 06/11/2025
- ✨ Implementado CRUD completo de Motorista
- ✨ Enum Sexo (MASCULINO, FEMININO) com tipagem forte
- ✨ Relacionamento ManyToOne com Cliente (obrigatório)
- ✨ Validação de dados pessoais (nome, CPF, data de nascimento)
- ✨ Validação de documentação CNH (número, validade, categoria)
- ✨ Endereço completo (logradouro, número, bairro, CEP, complemento, cidade, UF)
- ✨ 3 endpoints GET com paginação (todos, por ID, por cliente)
- ✨ Suporte completo a paginação e ordenação
- ✨ Validações robustas (campos obrigatórios e tamanhos máximos)
- ✨ Auditoria com Date (dataCadastro) e LocalDateTime (updatedAt)
- ✨ Lazy Loading para relacionamento com Cliente
- 📝 Documentação Swagger completa
- 📝 Exemplos de uso (cURL) no histórico

### v1.7.0 - 02/11/2025
- ✨ Implementado CRUD completo de Local
- ✨ Entidades aninhadas: Coordenada e ParametroLocal
- ✨ Relacionamentos complexos: OneToMany com Coordenada e OneToOne com ParametroLocal
- ✨ Relacionamento ManyToOne com Cliente (obrigatório)
- ✨ Cascade ALL para coordenadas e parâmetro local
- ✨ Orphan removal para coordenadas (remoção automática de órfãs)
- ✨ Enums FuncaoLocal (CARGA, DESCARGA, OUTROS)
- ✨ Enums TipoLocal (EMPRESA, OFICINA, POSTO_DE_ABASTECIMENTO, POSTO_DE_FISCALIZACAO)
- ✨ Helper methods para gerenciar coordenadas (addCoordenada, removeCoordenada)
- ✨ Lista de coordenadas com sequência, latitude, longitude e raio
- ✨ Parâmetros de local (limite de veículos, tempo mínimo e máximo de permanência)
- ✨ 3 endpoints GET com paginação (todos, por ID, por cliente)
- ✨ Suporte completo a paginação e ordenação
- ✨ Validações robustas (nome obrigatório, cliente obrigatório)
- ✨ Auditoria com Date (dataCadastro) e LocalDateTime (updatedAt)
- ✨ Status padrão (true) para novos locais
- 📝 Documentação Swagger completa
- 📝 Exemplos de uso (cURL) no histórico

### v1.6.0 - 31/10/2025
- ✨ Implementado CRUD completo de Veículo
- ✨ Relacionamentos múltiplos: ManyToOne com Modelo, Cliente e Equipamento
- ✨ 50+ atributos para configuração detalhada de veículos
- ✨ Validação de tipo de veículo (Moto, Carro, Onibus, Caminhao, Carreta, Implemento)
- ✨ Validação de tipo de combustível (Gasolina, Alcool, Diesel)
- ✨ Configurações de RPM (faixas azul, verde, amarela, econômica, marcha lenta)
- ✨ Configurações de velocidade (máxima, chuva, desaceleração, curva)
- ✨ Alertas configuráveis (velocidade, bateria, comunicação)
- ✨ Configurações de rota e validação (cerca virtual, rota, iButton)
- ✨ Entradas digitais configuráveis (1-4)
- ✨ Equipamento opcional com cascade ALL
- ✨ 3 endpoints GET com paginação (todos, por ID, por cliente)
- ✨ Suporte a paginação e ordenação customizável
- ✨ Auditoria com Date (dataCadastro) e LocalDateTime (updatedAt)
- ✨ Status padrão (true) para novos veículos
- 📝 Documentação Swagger completa
- 📝 Exemplos de uso (cURL) no histórico

### v1.5.0 - 31/10/2025
- ✨ Implementado CRUD completo de Equipamento
- ✨ Relacionamento ManyToOne Equipamento-Modelo
- ✨ Validação de tipoEquipamento (apenas "PR" ou "PA")
- ✨ Validação de tipoChip (apenas "PR" ou "PA")
- ✨ Validação de existência do Modelo antes de salvar/atualizar
- ✨ DTO expõe informações do Modelo relacionado (marca e fabricante)
- ✨ Suporte a paginação e ordenação customizável
- ✨ Auditoria com Date (dataCadastro) e LocalDateTime (updatedAt)
- ✨ Campo observacao como TEXT
- ✨ Status e equipamentoAlocado com valores padrão
- 📝 Documentação Swagger completa
- 📝 Exemplos de uso (cURL) no histórico

### v1.4.0 - 31/10/2025
- ✨ Implementado CRUD completo de Modelo
- ✨ Validação de tipo (apenas "Equipamento" ou "Veiculo")
- ✨ Endpoints GET especializados por tipo com paginação
- ✨ Suporte a ordenação customizável por qualquer campo
- ✨ Auditoria automática (createdAt, updatedAt)
- ✨ Logs SLF4J em todas operações
- ✨ Status padrão (true) para novos modelos
- 📝 Documentação Swagger completa
- 📝 Exemplos de uso (cURL) no histórico

### v1.3.0 - 30/10/2025
- ✨ Implementado CRUD completo de Cliente
- ✨ Relacionamento ManyToOne Cliente-Cooperativa
- ✨ UUID único e automático para cada cliente
- ✨ Validação de CNPJ/CPF único
- ✨ Validação de cooperativa obrigatória para cooperados
- ✨ Endereço completo (rua, bairro, complemento, número, CEP, cidade, UF)
- ✨ Campos de contato (telefones e contatos)
- ✨ 3 endpoints GET com paginação (todos, por ID, por cooperativa)
- ✨ Paginação nativa em todos endpoints de listagem
- 📝 Documentação completa no histórico de desenvolvimento

### v1.2.0 - 30/10/2025
- ✨ Implementada paginação completa em Product e Cooperativa
- ✨ Adicionados 8 novos endpoints paginados
- ✨ Suporte a ordenação customizável (ASC/DESC)
- ✨ Resposta com metadata completa (total de páginas, elementos, etc.)
- ✨ Endpoints retrocompatíveis (mantidos endpoints originais)
- 📝 Documentação Swagger atualizada com endpoints paginados
- 📝 Histórico de desenvolvimento atualizado

### v1.1.0 - 29/10/2025
- ✨ Adicionado CRUD completo de Cooperativa
- ✨ Endpoints REST para gerenciamento de cooperativas
- ✨ Validação de CNPJ único
- ✨ Múltiplas opções de busca (nome, CNPJ, cidade, UF, status)
- 📝 Documentação Swagger completa

### v1.0.0 - Inicial
- ✨ Estrutura base do projeto Spring Boot
- ✨ CRUD de Product (exemplo)
- ✨ Configuração PostgreSQL
- ✨ Configuração ActiveMQ/JMS
- ✨ Swagger/OpenAPI
- ✨ Spring Actuator

---

**Última Atualização**: 06/11/2025
**Desenvolvedor**: Claude Code
**Branch Atual**: feature/motorista
