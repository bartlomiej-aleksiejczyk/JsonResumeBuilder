--liquibase formatted sql

--changeset your_name:0002-01
ALTER TABLE cv_build_job ADD COLUMN blob_content BYTEA;

--rollback ALTER TABLE cv_build_job DROP COLUMN blob_content;
