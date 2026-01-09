 INSERT INTO usuarios (nome, email, senha, telefone, ativo, criado_em, atualizado_em)
  VALUES ('Administrador', 'admin@trovian.com', '$2a$10$XQekhPnKPNzTbLLxsB2SkeJLhVPyqF2f8O.IgLYjw8KXVn4o9D2EO', '11999999999', true, NOW(), NOW());

  INSERT INTO usuario_roles (usuario_id, role)
  SELECT id, 'ADMIN' FROM usuarios WHERE email = 'admin@trovian.com';
  (Senha padrão: admin123)
  
  
  INSERT INTO usuario_funcionalidade (usuario_id, funcionalidade_id)
SELECT
    1 AS usuario_id,
    f.id AS funcionalidade_id
FROM funcionalidade f;