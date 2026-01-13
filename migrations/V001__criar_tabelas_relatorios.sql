-- Tabela principal de templates
CREATE TABLE relatorio_templates (
    id BIGSERIAL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    descricao VARCHAR(500),
    categoria VARCHAR(50) NOT NULL,
    query_base TEXT NOT NULL,
    query_campos TEXT,
    query_joins TEXT,
    query_group_by TEXT,
    query_order_by TEXT,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    sistema_template BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Parâmetros dos templates
CREATE TABLE parametros_relatorio (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES relatorio_templates(id) ON DELETE CASCADE,
    nome VARCHAR(50) NOT NULL,
    label VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    obrigatorio BOOLEAN NOT NULL DEFAULT TRUE,
    valor_padrao VARCHAR(255),
    opcoes VARCHAR(500),
    ordem INTEGER DEFAULT 0
);

-- Campos dos relatórios
CREATE TABLE campos_relatorio (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES relatorio_templates(id) ON DELETE CASCADE,
    nome VARCHAR(100) NOT NULL,
    label VARCHAR(100) NOT NULL,
    tipo VARCHAR(30) NOT NULL,
    visivel BOOLEAN NOT NULL DEFAULT TRUE,
    totalizavel BOOLEAN NOT NULL DEFAULT FALSE,
    formato VARCHAR(50),
    ordem INTEGER DEFAULT 0,
    largura INTEGER
);

-- Histórico de relatórios gerados
CREATE TABLE relatorios_gerados (
    id BIGSERIAL PRIMARY KEY,
    template_id BIGINT NOT NULL REFERENCES relatorio_templates(id),
    nome_arquivo VARCHAR(200) NOT NULL,
    formato VARCHAR(20) NOT NULL,
    parametros_usados TEXT,
    total_registros BIGINT NOT NULL,
    caminho_arquivo VARCHAR(500),
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_id BIGINT
);

-- Índices
CREATE INDEX idx_template_categoria ON relatorio_templates(categoria);
CREATE INDEX idx_template_ativo ON relatorio_templates(ativo);
CREATE INDEX idx_parametro_template ON parametros_relatorio(template_id);
CREATE INDEX idx_campo_template ON campos_relatorio(template_id);
CREATE INDEX idx_gerado_template ON relatorios_gerados(template_id);
CREATE INDEX idx_gerado_usuario ON relatorios_gerados(usuario_id);
