# Guia de Testes - Trovian API

## Visão Geral

Este documento descreve a estrutura de testes unitários implementada na API Trovian, incluindo ferramentas utilizadas, estratégia adotada, arquivos criados e instruções para execução.

---

## Stack de Testes

| Ferramenta | Versão | Finalidade |
|---|---|---|
| **JUnit 5** | gerenciado pelo Spring Boot | Framework base para todos os testes |
| **Mockito** | gerenciado pelo Spring Boot | Mocking de dependências em testes unitários |
| **AssertJ** | gerenciado pelo Spring Boot | Asserções fluentes e legíveis |
| **MockMvc** | gerenciado pelo Spring Boot | Teste de controllers sem subir servidor |
| **H2 Database** | gerenciado pelo Spring Boot | Banco in-memory para testes de repositório |
| **Testcontainers** | 1.19.3 | PostgreSQL real para testes de integração futuros |
| **JaCoCo** | 0.8.11 | Relatório de cobertura de código |

> Todas as ferramentas acima são incluídas via `spring-boot-starter-test`, exceto H2, Testcontainers e JaCoCo que foram adicionados ao `pom.xml`.

---

## Estratégia: Pirâmide de Testes

```
         /\
        /  \   10% — Integração futura
       /----\         @SpringBootTest + Testcontainers
      /      \
     /--------\  20% — Slice Tests
    /          \       @WebMvcTest (Controllers)
   /------------\      @DataJpaTest (Repositories)
  /              \
 /----------------\  70% — Unitários
/                  \       @ExtendWith(MockitoExtension.class)
\__________________/       Services com Mockito puro
```

### Regra por camada

| Camada | Anotação | O que não carrega |
|---|---|---|
| **Services** | `@ExtendWith(MockitoExtension.class)` | Nenhum contexto Spring |
| **Controllers** | `@WebMvcTest` sem Security | Banco, segurança JWT, MongoDB |
| **Repositories** | `@DataJpaTest` + H2 | Web, Security, ActiveMQ, MongoDB |

---

## Estrutura de Arquivos

```
src/test/
├── java/com/trovian/
│   ├── controller/
│   │   └── ViagemControllerTest.java       # Testes HTTP: GET, POST, DELETE /viagem
│   ├── repository/
│   │   └── ViagemRepositoryTest.java       # Queries JPA com H2 in-memory
│   ├── service/
│   │   ├── AuthServiceTest.java            # Login, registro, logout, refresh, recuperação de senha
│   │   ├── JwtServiceTest.java             # Geração, validação e expiração de tokens JWT
│   │   └── ViagemServiceTest.java          # Cálculos financeiros de viagem (regra de negócio central)
│   └── util/
│       └── builders/
│           ├── ClienteBuilder.java         # Dados de teste para Cliente
│           ├── MotoristaBuilder.java        # Dados de teste para Motorista
│           ├── RotaBuilder.java            # Dados de teste para Rota + RotaEstatisticas
│           ├── UsuarioBuilder.java         # Dados de teste para Usuario
│           ├── VeiculoBuilder.java         # Dados de teste para Veiculo
│           └── ViagemDTOBuilder.java       # Dados de teste para ViagemDTO (campos obrigatórios pré-preenchidos)
└── resources/
    └── application-test.yml                # Configuração isolada para testes
```

---

## Configuração de Testes (`application-test.yml`)

O arquivo `src/test/resources/application-test.yml` isola os testes de toda infraestrutura externa:

| Componente | Configuração em teste |
|---|---|
| **Banco de dados** | H2 in-memory (`jdbc:h2:mem:testdb`) |
| **Liquibase** | Desabilitado (`enabled: false`) |
| **ActiveMQ** | Broker embarcado (`vm://embedded`) |
| **MongoDB** | Não conecta (configuração local padrão) |
| **JWT Secret** | Valor fixo (mesma chave do `application.yml`) |
| **Email (SMTP)** | localhost:25 sem autenticação |

---

## Detalhamento dos Testes

### `ViagemServiceTest` — 15 testes

Testa a lógica de negócio central da aplicação: o motor de cálculo de viagens.

| Cenário | Método testado |
|---|---|
| Retorna página vazia quando não há viagens | `findAll` |
| Mapeia entidade para DTO corretamente | `findAll` |
| Retorna DTO quando viagem existe | `findById` |
| Lança exceção quando viagem não existe | `findById` |
| Retorna viagens do veículo informado | `findByVeiculo` |
| Filtra por status corretamente | `findByStatusViagem` |
| Deleta quando viagem existe | `delete` |
| Lança exceção ao deletar ID inexistente | `delete` |
| Lança exceção quando cliente não encontrado | `calcular` |
| Lança exceção quando veículo não encontrado | `calcular` |
| Lança exceção quando motorista não encontrado | `calcular` |
| Lança exceção quando rota de ida não encontrada | `calcular` |
| Receita bruta positiva com dados válidos | `calcular` |
| Desconta imposto da receita bruta da ida | `calcular` |
| Veículo sem carga máxima usa fator de carga 1.0 | `calcular` |
| Motorista sem comissão define comissão como zero | `calcular` |
| Gera resultado final com lucro líquido | `calcular` |

---

### `JwtServiceTest` — 7 testes

Testa a geração e validação de tokens JWT sem contexto Spring (usa `ReflectionTestUtils` para injetar `@Value`).

| Cenário |
|---|
| `generateToken` retorna token não-nulo para usuário válido |
| `generateToken` inclui email no subject do token |
| `validateToken` retorna `true` para token válido e usuário correto |
| `validateToken` retorna `false` para email diferente |
| `validateToken` retorna `false` para token expirado |
| `extractUsername` retorna email do token |
| `extractExpiration` retorna data de expiração válida |
| Dois usuários diferentes geram tokens distintos |

---

### `AuthServiceTest` — 10 testes

Testa o fluxo completo de autenticação.

| Cenário | Método testado |
|---|---|
| Retorna `LoginResponse` com tokens quando usuário existe | `login` |
| Lança exceção quando usuário não encontrado | `login` |
| Salva usuário e retorna mensagem de sucesso | `registrar` |
| Lança exceção quando email já cadastrado | `registrar` |
| Limpa token e revoga refresh tokens | `logout` |
| Lança exceção quando usuário não encontrado | `logout` |
| Retorna novo access token com refresh token válido | `refreshToken` |
| Lança exceção com refresh token inexistente | `refreshToken` |
| Gera token e envia email de recuperação | `solicitarRecuperacaoSenha` |
| Atualiza senha quando token é válido | `redefinirSenha` |
| Lança exceção quando token de redefinição expirado | `redefinirSenha` |

---

### `ViagemControllerTest` — 9 testes

Testa o contrato HTTP dos endpoints REST. Usa `@WebMvcTest` com segurança desabilitada para focar exclusivamente nos status codes e payloads.

| Endpoint | Cenário | Status esperado |
|---|---|---|
| `GET /viagem` | Lista com viagens existentes | `200 OK` |
| `GET /viagem` | Lista quando vazio | `200 OK` com página vazia |
| `GET /viagem/{id}` | Viagem encontrada | `200 OK` |
| `GET /viagem/{id}` | Viagem não encontrada | `400 Bad Request` |
| `GET /viagem/veiculo/{id}` | Lista por veículo | `200 OK` |
| `POST /viagem/calcular` | Dados válidos | `200 OK` |
| `POST /viagem/calcular` | Campos obrigatórios faltando | `400 Bad Request` |
| `POST /viagem` | Criação bem-sucedida | `201 Created` |
| `DELETE /viagem/{id}` | Deleção bem-sucedida | `204 No Content` |
| `GET /viagem/status-viagem/FECHADA` | Filtra por status | `200 OK` |

---

### `ViagemRepositoryTest` — 7 testes

Testa queries JPA derivadas com banco H2 in-memory. Não executa queries QueryDSL (nativas).

| Cenário |
|---|
| Repository é injetado corretamente (contexto carrega) |
| `findByStatusViagem` retorna vazio quando banco vazio |
| `findByStatusViagem` com status `FECHADA` retorna vazio |
| `findByVeiculoId` com veículo inexistente retorna vazio |
| `findByMotoristaId` com motorista inexistente retorna vazio |
| `findByClienteId` com cliente inexistente retorna vazio |
| `existsById` retorna `false` para ID inexistente |
| `count` retorna zero quando banco vazio |

---

## Padrão Test Data Builder

Todos os builders seguem o padrão fluente com valores-padrão pré-configurados:

```java
// Uso básico com defaults
Viagem dto = ViagemDTOBuilder.umaViagem().build();

// Customizando campos específicos
Veiculo veiculo = VeiculoBuilder.umVeiculo()
        .comPlaca("XYZ-9999")
        .comNumeroEixos(9)
        .semCargaMaxima()
        .build();

// Rota já vem com RotaEstatisticas configurada (distâncias não-zero)
// necessário para ViagemService.calcularFatorTerreno() funcionar corretamente
Rota rota = RotaBuilder.umaRota()
        .comNome("SP - BH (Teste)")
        .comDistanciaTotal(600_000.0) // 600 km em metros
        .build();
```

---

## Convenção de Nomenclatura

```
nomeDoMetodo_cenarioTestado_resultadoEsperado()
```

**Exemplos:**
```java
calcular_quandoClienteNaoEncontrado_deveLancarExcecao()
login_comUsuarioExistente_deveRetornarLoginResponse()
findByStatusViagem_quandoBancoVazio_deveRetornarPaginaVazia()
validateToken_comTokenExpirado_deveRetornarFalse()
```

---

## Como Executar os Testes

### Rodar todos os testes

```bash
mvn test
```

### Rodar testes de uma classe específica

```bash
# Apenas ViagemServiceTest
mvn test -Dtest=ViagemServiceTest

# Apenas testes de service
mvn test -Dtest="*ServiceTest"

# Apenas testes de controller
mvn test -Dtest="*ControllerTest"
```

### Rodar um método de teste específico

```bash
mvn test -Dtest="ViagemServiceTest#calcular_comDadosValidos_deveRetornarReceitaBrutaPositiva"
```

### Gerar relatório de cobertura JaCoCo

```bash
mvn test jacoco:report
```

O relatório HTML será gerado em:
```
target/site/jacoco/index.html
```

### Pular testes ao buildar

```bash
mvn package -DskipTests
```

### Gerar relatório de cobertura no Jenkins

```bash
mvn verify jacoco:report
```

O relatório HTML pode ser publicado como artefato do build em `target/site/jacoco/index.html`.

---

## Execução no Jenkins (CI/CD)

Os testes podem e **devem** ser mantidos habilitados no pipeline do Jenkins. Eles são totalmente autocontidos e não dependem de nenhuma infraestrutura externa:

| Dependência | Status em CI |
|---|---|
| PostgreSQL | Não necessário — substituído por H2 in-memory |
| MongoDB | Não conecta durante testes unitários |
| ActiveMQ | Broker embarcado (`vm://embedded`) |
| SMTP | Localhost sem autenticação (não envia emails reais) |

### Por que é seguro rodar no Jenkins

- Os testes de **service** usam Mockito puro — nenhum contexto Spring é inicializado
- Os testes de **repository** sobem o H2 em memória automaticamente, sem configuração adicional
- Os testes de **controller** usam MockMvc — nenhum servidor real é iniciado
- O banco H2 é criado no início e destruído ao final de cada execução (`create-drop`)

### Exemplo de estágio no Jenkinsfile

```groovy
stage('Test') {
    steps {
        sh 'mvn test'
    }
    post {
        always {
            junit 'target/surefire-reports/**/*.xml'
        }
    }
}

stage('Coverage') {
    steps {
        sh 'mvn jacoco:report'
        publishHTML(target: [
            reportDir: 'target/site/jacoco',
            reportFiles: 'index.html',
            reportName: 'JaCoCo Coverage'
        ])
    }
}
```

---

## Cobertura de Código (JaCoCo)

### Metas de cobertura definidas

| Camada | Meta |
|---|---|
| `service/` | ≥ 80% de linhas |
| `controller/` | ≥ 70% de linhas |
| `repository/` | ≥ 60% (apenas queries customizadas) |
| `security/` | ≥ 60% |

### Classes excluídas do relatório

As seguintes classes são excluídas por não conterem lógica de negócio testável:

- `com/trovian/entity/**` — Entidades JPA (apenas mapeamento)
- `com/trovian/dto/**` — DTOs (apenas dados)
- `com/trovian/enums/**` — Enumerações
- `com/trovian/Application.class` — Classe main

### Verificar cobertura no terminal

```bash
# Gera relatório e verifica meta mínima (falha o build se abaixo de 70%)
mvn verify
```

---

## Adicionando Novos Testes

### Para um novo Service

```java
@ExtendWith(MockitoExtension.class)          // SEM contexto Spring
class NovoServiceTest {

    @InjectMocks
    private NovoService novoService;

    @Mock
    private AlgumRepository algumRepository;  // mocka dependências

    @Test
    @DisplayName("metodo_cenario_resultado")
    void metodo_cenario_resultado() {
        // given
        given(algumRepository.findById(1L)).willReturn(Optional.of(...));

        // when
        ResultDTO resultado = novoService.metodo(1L);

        // then
        assertThat(resultado.getCampo()).isEqualTo("valorEsperado");
    }
}
```

### Para um novo Controller

```java
@WebMvcTest(
    value = NovoController.class,
    excludeAutoConfiguration = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class}
)
class NovoControllerTest {

    @Autowired MockMvc mockMvc;
    @MockBean NovoService novoService;

    @Test
    void endpoint_deveRetornar200() throws Exception {
        given(novoService.listar()).willReturn(List.of(...));

        mockMvc.perform(get("/endpoint"))
                .andExpect(status().isOk());
    }
}
```

### Para um novo Repository

```java
@DataJpaTest
@TestPropertySource(properties = {
    "spring.liquibase.enabled=false",
    "spring.jpa.hibernate.ddl-auto=create-drop"
})
class NovoRepositoryTest {

    @Autowired NovoRepository novoRepository;
    @Autowired TestEntityManager entityManager;

    @Test
    void findByAlgo_deveRetornarEntidades() {
        // persistir dados de teste no H2
        entityManager.persistAndFlush(new Entidade(...));

        List<Entidade> resultado = novoRepository.findByAlgo("valor");

        assertThat(resultado).hasSize(1);
    }
}
```

---

## Dependências Adicionadas ao `pom.xml`

```xml
<!-- H2 para @DataJpaTest -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>test</scope>
</dependency>

<!-- Testcontainers -->
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>junit-jupiter</artifactId>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.testcontainers</groupId>
    <artifactId>postgresql</artifactId>
    <scope>test</scope>
</dependency>

<!-- BOM Testcontainers -->
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.testcontainers</groupId>
            <artifactId>testcontainers-bom</artifactId>
            <version>1.19.3</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<!-- JaCoCo plugin (em <build><plugins>) -->
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.11</version>
</plugin>
```
