-- Template 1: Despesas por Veículo (Financeiro)
INSERT INTO relatorio_templates (nome, descricao, categoria, query_base, query_campos, query_joins, query_order_by, ativo, sistema_template)
VALUES (
    'Despesas por Veículo',
    'Relatório de todas as despesas agrupadas por veículo em um período',
    'FINANCEIRO',
    'contas_pagar cp',
    'SELECT v.placa, v.modelo, SUM(cp.valor) as total_despesas, COUNT(cp.id) as qtd_contas',
    'LEFT JOIN veiculos v ON cp.veiculo_id = v.id',
    'ORDER BY total_despesas DESC',
    TRUE,
    TRUE
);

-- Parâmetros do Template 1
INSERT INTO parametros_relatorio (template_id, nome, label, tipo, obrigatorio, ordem)
VALUES
    (1, 'data_inicio', 'Data Início', 'DATA', TRUE, 1),
    (1, 'data_fim', 'Data Fim', 'DATA', TRUE, 2),
    (1, 'veiculo_id', 'Veículo (Opcional)', 'SELECT', FALSE, 3);

-- Campos do Template 1
INSERT INTO campos_relatorio (template_id, nome, label, tipo, visivel, totalizavel, ordem)
VALUES
    (1, 'placa', 'Placa', 'TEXTO', TRUE, FALSE, 1),
    (1, 'modelo', 'Modelo', 'TEXTO', TRUE, FALSE, 2),
    (1, 'total_despesas', 'Total Despesas', 'MOEDA', TRUE, TRUE, 3),
    (1, 'qtd_contas', 'Quantidade de Contas', 'NUMERO', TRUE, TRUE, 4);

-- Template 2: Manutenções Preventivas Vencidas
INSERT INTO relatorio_templates (nome, descricao, categoria, query_base, query_campos, query_joins, query_order_by, ativo, sistema_template)
VALUES (
    'Manutenções Preventivas Vencidas',
    'Lista veículos com manutenções preventivas vencidas ou próximas do vencimento',
    'MANUTENCAO',
    'ordens_servico os',
    'SELECT v.placa, v.modelo, os.numero_os, os.descricao_problema, os.data_prevista, os.status',
    'INNER JOIN veiculos v ON os.veiculo_id = v.id',
    'ORDER BY os.data_prevista ASC',
    TRUE,
    TRUE
);

INSERT INTO parametros_relatorio (template_id, nome, label, tipo, obrigatorio, ordem)
VALUES
    (2, 'data_referencia', 'Data de Referência', 'DATA', TRUE, 1),
    (2, 'status', 'Status', 'SELECT', FALSE, 2);

INSERT INTO campos_relatorio (template_id, nome, label, tipo, visivel, ordem)
VALUES
    (2, 'placa', 'Placa', 'TEXTO', TRUE, 1),
    (2, 'modelo', 'Modelo', 'TEXTO', TRUE, 2),
    (2, 'numero_os', 'Número OS', 'TEXTO', TRUE, 3),
    (2, 'descricao_problema', 'Descrição', 'TEXTO', TRUE, 4),
    (2, 'data_prevista', 'Data Prevista', 'DATA', TRUE, 5),
    (2, 'status', 'Status', 'TEXTO', TRUE, 6);

-- Template 3: Checklists Não Realizados
INSERT INTO relatorio_templates (nome, descricao, categoria, query_base, query_campos, query_joins, query_order_by, ativo, sistema_template)
VALUES (
    'Checklists Pendentes',
    'Motoristas com checklists não realizados no período',
    'CHECKLIST',
    'checklists c',
    'SELECT m.nome as motorista, v.placa, c.data_prevista, c.tipo_checklist, c.status',
    'INNER JOIN motoristas m ON c.motorista_id = m.id INNER JOIN veiculos v ON c.veiculo_id = v.id',
    'ORDER BY c.data_prevista DESC',
    TRUE,
    TRUE
);

INSERT INTO parametros_relatorio (template_id, nome, label, tipo, obrigatorio, ordem)
VALUES
    (3, 'data_inicio', 'Data Início', 'DATA', TRUE, 1),
    (3, 'data_fim', 'Data Fim', 'DATA', TRUE, 2),
    (3, 'motorista_id', 'Motorista (Opcional)', 'SELECT', FALSE, 3);

INSERT INTO campos_relatorio (template_id, nome, label, tipo, visivel, ordem)
VALUES
    (3, 'motorista', 'Motorista', 'TEXTO', TRUE, 1),
    (3, 'placa', 'Placa', 'TEXTO', TRUE, 2),
    (3, 'data_prevista', 'Data Prevista', 'DATA', TRUE, 3),
    (3, 'tipo_checklist', 'Tipo', 'TEXTO', TRUE, 4),
    (3, 'status', 'Status', 'TEXTO', TRUE, 5);

-- Template 4: Fluxo de Caixa Mensal
INSERT INTO relatorio_templates (nome, descricao, categoria, query_base, query_campos, query_joins, query_order_by, ativo, sistema_template)
VALUES (
    'Fluxo de Caixa Mensal',
    'Resumo de receitas e despesas do mês',
    'FINANCEIRO',
    '(SELECT data_vencimento as data, valor, ''DESPESA'' as tipo FROM contas_pagar UNION ALL SELECT data_vencimento, valor, ''RECEITA'' FROM contas_receber) fluxo',
    'SELECT DATE(data) as data, SUM(CASE WHEN tipo = ''RECEITA'' THEN valor ELSE 0 END) as receitas, SUM(CASE WHEN tipo = ''DESPESA'' THEN valor ELSE 0 END) as despesas',
    '',
    'ORDER BY data ASC',
    TRUE,
    TRUE
);

INSERT INTO parametros_relatorio (template_id, nome, label, tipo, obrigatorio, ordem)
VALUES
    (4, 'mes_referencia', 'Mês de Referência', 'DATA', TRUE, 1);

INSERT INTO campos_relatorio (template_id, nome, label, tipo, visivel, totalizavel, ordem)
VALUES
    (4, 'data', 'Data', 'DATA', TRUE, FALSE, 1),
    (4, 'receitas', 'Receitas', 'MOEDA', TRUE, TRUE, 2),
    (4, 'despesas', 'Despesas', 'MOEDA', TRUE, TRUE, 3);
