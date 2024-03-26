--liquibase formatted sql

--changeset bartlomiej_aleksiejczyk:0002-01
ALTER TABLE cv_build_job ADD COLUMN cv_compilation_result BYTEA;

--rollback ALTER TABLE cv_build_job DROP COLUMN blob_content;
