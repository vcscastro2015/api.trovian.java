--liquibase formatted sql

ALTER TABLE imagem_resposta
ALTER COLUMN conteudo_binario SET DATA TYPE oid
USING lo_from_bytea(0, conteudo_binario);