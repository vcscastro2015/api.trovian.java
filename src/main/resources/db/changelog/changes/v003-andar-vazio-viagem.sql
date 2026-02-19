--liquibase formatted sql

--changeset trovian:v003-andar-vazio-viagem
--comment: Adiciona flag andar_vazio_volta na tabela viagem para indicar retorno sem carga
ALTER TABLE viagem ADD COLUMN andar_vazio_volta BOOLEAN NOT NULL DEFAULT FALSE;