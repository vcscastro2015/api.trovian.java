alter table usuarios add column cliente_id int8;
alter table usuarios add CONSTRAINT fk_usuario_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table cooperativas add column cliente_id int8;
alter table cooperativas add CONSTRAINT fk_cooperativas_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table equipamento add column cliente_id int8;
alter table equipamento add CONSTRAINT fk_equipamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table modelo add column cliente_id int8;
alter table modelo add CONSTRAINT fk_modelo_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table fornecedor add column cliente_id int8;
alter table fornecedor add CONSTRAINT fk_fornecedor_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table categoria_conta add column cliente_id int8;
alter table categoria_conta add CONSTRAINT fk_categoria_conta_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table forma_pagamento add column cliente_id int8;
alter table forma_pagamento add CONSTRAINT fk_forma_pagamento_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table ordem_servico add column cliente_id int8;
alter table ordem_servico add CONSTRAINT fk_ordem_servico_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table peca add column cliente_id int8;
alter table peca add CONSTRAINT fk_peca_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table movimentacao_estoque add column cliente_id int8;
alter table movimentacao_estoque add CONSTRAINT fk_movimentacao_estoque_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table alerta_manutencao add column cliente_id int8;
alter table alerta_manutencao add CONSTRAINT fk_alerta_manutencao_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);

alter table checklist_realizado add column cliente_id int8;
alter table checklist_realizado add CONSTRAINT fk_checklist_realizado_cliente FOREIGN KEY (cliente_id) REFERENCES cliente(id);






