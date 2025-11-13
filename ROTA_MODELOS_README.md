# Modelos e Serviços para Persistência de Rotas

## Data de Criação: 10/11/2025

---

## Visão Geral

Sistema completo de modelos e serviços para persistir rotas calculadas no backend, incluindo todos os dados de elevação, segmentos, pedágios e estatísticas.

---

## Arquivos Criados

### 1. `src/app/models/rota.model.ts`

Contém todas as interfaces TypeScript necessárias para representar uma rota completa.

#### Interfaces Principais

**`Rota`** - Modelo principal de rota
```typescript
{
  id?: number;
  nome: string;
  descricao?: string;
  ativa: boolean;
  dataCadastro?: Date;
  dataAtualizacao?: Date;
  distanciaTotal: number; // metros
  clienteId?: number;
  veiculoId?: number;
  cooperativaId?: number;
  pontos: PontoRota[];
  estatisticas?: RotaEstatisticas;
  segmentos?: RotaSegmento[];
  pedagios?: RotaPedagio[];
  geoJson?: string;
  // Campos de relacionamento (somente leitura)
  clienteNome?: string;
  veiculoPlaca?: string;
  cooperativaNome?: string;
}
```

**`PontoRota`** - Ponto de parada (origem, destino ou waypoint)
```typescript
{
  sequencia: number;
  tipo: TipoPonto; // ORIGEM, DESTINO, WAYPOINT
  latitude: number;
  longitude: number;
  endereco?: string;
  nome?: string;
}
```

**`RotaEstatisticas`** - Estatísticas de elevação
```typescript
{
  ganhoTotalElevacao: number; // metros
  perdaTotalElevacao: number; // metros
  elevacaoMaxima: number; // metros
  elevacaoMinima: number; // metros
  elevacaoMedia: number; // metros
  dificuldade: string; // Fácil, Moderada, Difícil, Muito Difícil
  distanciaSubida: number; // metros
  distanciaDescida: number; // metros
  distanciaPlano: number; // metros
  pontoElevacaoMaxima?: { latitude, longitude, elevacao };
  pontoElevacaoMinima?: { latitude, longitude, elevacao };
}
```

**`RotaSegmento`** - Segmento colorido da rota
```typescript
{
  sequencia: number;
  latitudeInicio: number;
  longitudeInicio: number;
  elevacaoInicio: number;
  latitudeFim: number;
  longitudeFim: number;
  elevacaoFim: number;
  direcao: string; // SUBIDA, DESCIDA, PLANO
  nivelInclinacao: string; // PLANO, LEVE, MODERADO, FORTE, MUITO_FORTE
  porcentagemInclinacao: number;
  variacaoElevacao: number; // metros
  distancia: number; // metros
  cor: string; // hex color
}
```

**`RotaPedagio`** - Pedágio encontrado na rota
```typescript
{
  nome: string;
  rodovia: string;
  km: string;
  municipio: string;
  uf: string;
  concessionaria: string;
  latitude: number;
  longitude: number;
  distanciaRota: number; // metros
}
```

**`RotaDTO`** - DTO para criar/atualizar rota
- Versão simplificada sem campos de leitura (id, datas, nomes de relacionamento)

**`RotaFiltros`** - Filtros para busca de rotas
```typescript
{
  nome?: string;
  clienteId?: number;
  veiculoId?: number;
  cooperativaId?: number;
  ativa?: boolean;
  distanciaMinima?: number; // metros
  distanciaMaxima?: number; // metros
  dificuldade?: string;
}
```

**`PagedRotaResponse`** - Resposta paginada
```typescript
{
  content: Rota[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
```

#### Enums

**`TipoPonto`**
- `ORIGEM` - Ponto de partida
- `DESTINO` - Ponto de chegada
- `WAYPOINT` - Ponto intermediário

---

### 2. `src/app/services/rota.service.ts`

Serviço para comunicação com a API REST do backend.

#### Métodos Principais

**CRUD Básico**
- `getAll(page, size, sortBy, direction, filtros)` - Lista todas as rotas com paginação e filtros
- `getById(id)` - Busca uma rota específica
- `create(rotaDTO)` - Cria nova rota
- `update(id, rotaDTO)` - Atualiza rota existente
- `delete(id)` - Remove rota

**Busca Especializada**
- `getByCliente(clienteId, page, size)` - Rotas de um cliente
- `getByVeiculo(veiculoId, page, size)` - Rotas de um veículo
- `getByCooperativa(cooperativaId, page, size)` - Rotas de uma cooperativa
- `getAtivas(page, size)` - Apenas rotas ativas
- `searchByNome(nome, page, size)` - Busca por nome (parcial)

**Funcionalidades Extras**
- `exportGeoJson(id)` - Exporta rota em formato GeoJSON
- `duplicate(id, novoNome)` - Duplica uma rota existente

#### Configuração da API

**Base URL**: `${environment.apiUrl}/rota`

**Desenvolvimento**: `/api/rota` → proxy → `http://localhost:8080/api/rota`

**Produção**: `https://api.librasistemas.com.br/rota`

---

### 3. `src/app/services/rota-converter.service.ts`

Serviço auxiliar para converter dados calculados no frontend para formato de persistência.

#### Métodos Principais

**`toRotaDTO(...)`** - Converte rota calculada para DTO
```typescript
toRotaDTO(
  nome: string,
  descricao: string | undefined,
  ativa: boolean,
  pointA: L.LatLng,
  pointB: L.LatLng,
  waypoints: L.LatLng[],
  routeData: RouteWithElevation,
  pedagios: PedagioMapa[],
  clienteId?: number,
  veiculoId?: number,
  cooperativaId?: number,
  enderecoA?: string,
  enderecoB?: string
): RotaDTO
```

**Métodos Auxiliares**
- `formatDistance(meters)` - Formata distância (km ou m)
- `formatElevation(meters)` - Formata elevação (m)
- `calculateEstimatedTime(distanceMeters, avgSpeedKmh)` - Calcula tempo estimado
- `formatTime(minutes)` - Formata tempo (horas e minutos)

**Conversões Internas (privadas)**
- `convertPontos()` - Converte origem, destino e waypoints
- `convertEstatisticas()` - Converte estatísticas de elevação
- `convertSegmentos()` - Converte segmentos da rota
- `convertPedagios()` - Converte pedágios encontrados
- `generateGeoJson()` - Gera GeoJSON da rota

---

## Estrutura do Banco de Dados (Backend)

### Tabela Principal: `rota`

```sql
CREATE TABLE rota (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  nome VARCHAR(255) NOT NULL,
  descricao TEXT,
  ativa BOOLEAN DEFAULT TRUE,
  data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  data_atualizacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  distancia_total DOUBLE NOT NULL, -- em metros

  -- Relacionamentos opcionais
  cliente_id BIGINT,
  veiculo_id BIGINT,
  cooperativa_id BIGINT,

  -- GeoJSON para visualização rápida
  geo_json TEXT,

  FOREIGN KEY (cliente_id) REFERENCES cliente(id),
  FOREIGN KEY (veiculo_id) REFERENCES veiculo(id),
  FOREIGN KEY (cooperativa_id) REFERENCES cooperativa(id)
);
```

### Tabela: `ponto_rota`

```sql
CREATE TABLE ponto_rota (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rota_id BIGINT NOT NULL,
  sequencia INT NOT NULL,
  tipo VARCHAR(20) NOT NULL, -- ORIGEM, DESTINO, WAYPOINT
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  endereco VARCHAR(500),
  nome VARCHAR(255),

  FOREIGN KEY (rota_id) REFERENCES rota(id) ON DELETE CASCADE
);
```

### Tabela: `rota_estatisticas`

```sql
CREATE TABLE rota_estatisticas (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rota_id BIGINT NOT NULL UNIQUE,
  ganho_total_elevacao DOUBLE,
  perda_total_elevacao DOUBLE,
  elevacao_maxima DOUBLE,
  elevacao_minima DOUBLE,
  elevacao_media DOUBLE,
  dificuldade VARCHAR(50),
  distancia_subida DOUBLE,
  distancia_descida DOUBLE,
  distancia_plano DOUBLE,

  -- Pontos críticos
  ponto_elevacao_maxima_lat DOUBLE,
  ponto_elevacao_maxima_lng DOUBLE,
  ponto_elevacao_maxima_elev DOUBLE,
  ponto_elevacao_minima_lat DOUBLE,
  ponto_elevacao_minima_lng DOUBLE,
  ponto_elevacao_minima_elev DOUBLE,

  FOREIGN KEY (rota_id) REFERENCES rota(id) ON DELETE CASCADE
);
```

### Tabela: `rota_segmento` (Opcional - pode ser grande)

```sql
CREATE TABLE rota_segmento (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rota_id BIGINT NOT NULL,
  sequencia INT NOT NULL,
  latitude_inicio DOUBLE NOT NULL,
  longitude_inicio DOUBLE NOT NULL,
  elevacao_inicio DOUBLE NOT NULL,
  latitude_fim DOUBLE NOT NULL,
  longitude_fim DOUBLE NOT NULL,
  elevacao_fim DOUBLE NOT NULL,
  direcao VARCHAR(20), -- SUBIDA, DESCIDA, PLANO
  nivel_inclinacao VARCHAR(50),
  porcentagem_inclinacao DOUBLE,
  variacao_elevacao DOUBLE,
  distancia DOUBLE,
  cor VARCHAR(20),

  FOREIGN KEY (rota_id) REFERENCES rota(id) ON DELETE CASCADE
);
```

### Tabela: `rota_pedagio`

```sql
CREATE TABLE rota_pedagio (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  rota_id BIGINT NOT NULL,
  nome VARCHAR(255),
  rodovia VARCHAR(100),
  km VARCHAR(50),
  municipio VARCHAR(255),
  uf VARCHAR(2),
  concessionaria VARCHAR(255),
  latitude DOUBLE NOT NULL,
  longitude DOUBLE NOT NULL,
  distancia_rota DOUBLE,

  FOREIGN KEY (rota_id) REFERENCES rota(id) ON DELETE CASCADE
);
```

---

## Exemplo de Uso no Frontend

### 1. Salvar Rota Calculada

```typescript
import { RotaService } from './services/rota.service';
import { RotaConverterService } from './services/rota-converter.service';

// No componente de rota (rota.ts)
constructor(
  private rotaService: RotaService,
  private rotaConverter: RotaConverterService
) {}

salvarRota() {
  if (!this.routeData || !this.pointA || !this.pointB) {
    console.error('Rota não calculada');
    return;
  }

  // Converte dados calculados para DTO
  const rotaDTO = this.rotaConverter.toRotaDTO(
    'Rota São Paulo - Rio de Janeiro', // nome
    'Rota via Dutra', // descrição
    true, // ativa
    this.pointA,
    this.pointB,
    this.waypoints, // array de waypoints intermediários
    this.routeData,
    this.pedagios,
    1, // clienteId (opcional)
    undefined, // veiculoId (opcional)
    undefined, // cooperativaId (opcional)
    'São Paulo, SP', // endereço origem (opcional)
    'Rio de Janeiro, RJ' // endereço destino (opcional)
  );

  // Salva no backend
  this.rotaService.create(rotaDTO).subscribe({
    next: (rota) => {
      console.log('Rota salva com sucesso!', rota);
      alert(`Rota "${rota.nome}" salva com ID: ${rota.id}`);
    },
    error: (error) => {
      console.error('Erro ao salvar rota', error);
      alert('Erro ao salvar rota');
    }
  });
}
```

### 2. Listar Rotas

```typescript
listarRotas() {
  this.rotaService.getAll(0, 10, 'nome', 'ASC').subscribe({
    next: (response) => {
      console.log('Rotas encontradas:', response.content);
      console.log('Total:', response.totalElements);
    },
    error: (error) => {
      console.error('Erro ao listar rotas', error);
    }
  });
}
```

### 3. Buscar Rotas de um Cliente

```typescript
listarRotasCliente(clienteId: number) {
  this.rotaService.getByCliente(clienteId).subscribe({
    next: (response) => {
      console.log('Rotas do cliente:', response.content);
    },
    error: (error) => {
      console.error('Erro ao buscar rotas do cliente', error);
    }
  });
}
```

### 4. Carregar Rota Salva e Exibir no Mapa

```typescript
carregarRota(id: number) {
  this.rotaService.getById(id).subscribe({
    next: (rota) => {
      console.log('Rota carregada:', rota);

      // Configura pontos no mapa
      const pontoOrigem = rota.pontos.find(p => p.tipo === 'ORIGEM');
      const pontoDestino = rota.pontos.find(p => p.tipo === 'DESTINO');
      const waypoints = rota.pontos.filter(p => p.tipo === 'WAYPOINT');

      if (pontoOrigem && pontoDestino) {
        this.pointA = L.latLng(pontoOrigem.latitude, pontoOrigem.longitude);
        this.pointB = L.latLng(pontoDestino.latitude, pontoDestino.longitude);
        this.waypoints = waypoints.map(w => L.latLng(w.latitude, w.longitude));

        // Recalcula rota no mapa
        this.calculateRoute();
      }
    },
    error: (error) => {
      console.error('Erro ao carregar rota', error);
    }
  });
}
```

### 5. Buscar Rotas com Filtros

```typescript
buscarRotasAvancado() {
  const filtros: RotaFiltros = {
    nome: 'São Paulo',
    ativa: true,
    distanciaMinima: 100000, // 100 km
    distanciaMaxima: 500000, // 500 km
    dificuldade: 'Moderada'
  };

  this.rotaService.getAll(0, 10, 'distanciaTotal', 'ASC', filtros).subscribe({
    next: (response) => {
      console.log('Rotas filtradas:', response.content);
    }
  });
}
```

---

## Fluxo de Dados Completo

```
┌─────────────────────────────────────┐
│   Usuário define pontos A e B       │
│   + waypoints intermediários         │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   OSRM calcula rota (coordenadas)   │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   Open-Meteo calcula elevação       │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   Sistema calcula segmentos coloridos│
│   (direção, inclinação, cores)       │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   ANTT detecta pedágios na rota     │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   RotaConverterService.toRotaDTO()  │
│   Converte para formato backend     │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   RotaService.create(rotaDTO)       │
│   POST /api/rota                    │
└─────────────┬───────────────────────┘
              │
              ▼
┌─────────────────────────────────────┐
│   Backend persiste no banco         │
│   - Tabela rota                     │
│   - Tabela ponto_rota               │
│   - Tabela rota_estatisticas        │
│   - Tabela rota_segmento (opcional) │
│   - Tabela rota_pedagio             │
└─────────────────────────────────────┘
```

---

## Dados Persistidos

### O que é Salvo

✅ **Informações Básicas**
- Nome e descrição da rota
- Status ativo/inativo
- Distância total

✅ **Pontos de Parada**
- Origem (Ponto A) com coordenadas e endereço
- Destino (Ponto B) com coordenadas e endereço
- Todos os waypoints intermediários

✅ **Estatísticas de Elevação**
- Ganho e perda total de elevação
- Elevação máxima, mínima e média
- Dificuldade calculada
- Distâncias em subida, descida e plano
- Pontos críticos (maior e menor elevação)

✅ **Segmentos Detalhados** (opcional)
- Cada segmento da rota com:
  - Coordenadas inicio/fim
  - Elevação inicio/fim
  - Direção (subida/descida/plano)
  - Nível de inclinação
  - Porcentagem de inclinação
  - Variação de elevação
  - Distância do segmento
  - Cor do segmento

✅ **Pedágios Encontrados**
- Nome, rodovia, km
- Município e UF
- Concessionária
- Coordenadas GPS
- Distância até a rota

✅ **GeoJSON**
- Representação completa da rota em formato GeoJSON
- Útil para visualização rápida sem recalcular

✅ **Relacionamentos Opcionais**
- Cliente (se a rota for específica de um cliente)
- Veículo (se a rota for específica de um veículo)
- Cooperativa (se a rota for de uma cooperativa)

### O que NÃO é Salvo

❌ Marcadores visuais do mapa (são recriados ao carregar)
❌ Estado temporário da interface (loading, mensagens de erro)
❌ Cache de APIs externas

---

## Próximos Passos

### 1. Backend - API REST

Implementar endpoints no backend (Spring Boot / Node.js / etc):

**Endpoints Necessários:**
- `POST /api/rota` - Criar rota
- `GET /api/rota` - Listar rotas (com paginação e filtros)
- `GET /api/rota/{id}` - Buscar rota por ID
- `PUT /api/rota/{id}` - Atualizar rota
- `DELETE /api/rota/{id}` - Deletar rota
- `GET /api/rota/{id}/geojson` - Exportar GeoJSON
- `POST /api/rota/{id}/duplicate` - Duplicar rota

**Queries Adicionais:**
- Buscar por cliente/veículo/cooperativa
- Buscar por nome (like)
- Filtrar por distância (min/max)
- Filtrar por dificuldade
- Filtrar por ativa

### 2. Frontend - Interface de Gerenciamento

Criar páginas CRUD de rotas:
- `src/app/pages/rota-list/` - Listagem de rotas salvas
- `src/app/pages/rota-form/` - Formulário de edição de rota
- Integrar botão "Salvar Rota" no componente de cálculo (`rota.ts`)
- Modal para escolher nome, descrição e relacionamentos

### 3. Melhorias Futuras

**Interface:**
- Botão "Salvar Rota" no componente de cálculo
- Modal para preencher nome, descrição e relacionamentos
- Lista de rotas salvas com preview no mapa
- Comparar múltiplas rotas lado a lado

**Funcionalidades:**
- Importar/exportar rotas em formatos diversos (GPX, KML)
- Compartilhar rotas entre usuários
- Favoritar rotas
- Histórico de rotas calculadas
- Estimativa de custo (combustível + pedágios)
- Integração com valores reais de pedágios

**Performance:**
- Opção de salvar sem segmentos detalhados (economiza espaço)
- Compressão de GeoJSON
- Cache de rotas frequentes

---

## Observações Técnicas

### Tamanho dos Dados

**Rota Pequena (até 100 km):**
- Pontos: ~100-200 pontos
- Segmentos: ~100-200 segmentos
- Tamanho estimado: ~50-100 KB

**Rota Média (100-500 km):**
- Pontos: ~500-1000 pontos
- Segmentos: ~500-1000 segmentos
- Tamanho estimado: ~200-500 KB

**Rota Longa (>500 km):**
- Pontos: simplificados para ~1000 pontos máximo
- Segmentos: simplificados para ~1000 segmentos máximo
- Tamanho estimado: ~500 KB - 1 MB

### Recomendações de Armazenamento

**Segmentos Detalhados:**
- Se a aplicação exige análise detalhada: SALVAR
- Se apenas visualização: considerar OMITIR ou COMPRIMIR
- Pode ser recalculado a partir dos pontos se necessário

**GeoJSON:**
- Sempre salvar (útil para exportação e visualização rápida)
- Considerar compressão (gzip)

**Índices de Banco:**
```sql
CREATE INDEX idx_rota_cliente ON rota(cliente_id);
CREATE INDEX idx_rota_veiculo ON rota(veiculo_id);
CREATE INDEX idx_rota_cooperativa ON rota(cooperativa_id);
CREATE INDEX idx_rota_ativa ON rota(ativa);
CREATE INDEX idx_rota_distancia ON rota(distancia_total);
CREATE INDEX idx_ponto_rota ON ponto_rota(rota_id, sequencia);
CREATE INDEX idx_rota_segmento ON rota_segmento(rota_id, sequencia);
```

---

## Conclusão

Sistema completo de modelos e serviços para persistir rotas calculadas, incluindo:
- ✅ Modelos TypeScript bem estruturados
- ✅ Serviço REST para comunicação com backend
- ✅ Serviço conversor para transformar dados calculados
- ✅ Estrutura de banco de dados relacional
- ✅ Exemplos de uso práticos
- ✅ Sugestões de esquema SQL
- ✅ Fluxo de dados completo

Pronto para implementação do backend!

---

**Arquivos Criados:**
1. `src/app/models/rota.model.ts`
2. `src/app/services/rota.service.ts`
3. `src/app/services/rota-converter.service.ts`
4. `ROTA_MODELOS_README.md` (este arquivo)
