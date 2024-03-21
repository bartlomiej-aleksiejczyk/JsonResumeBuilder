--liquibase formatted sql

--changeset bartlomiej_aleksiejczyk:0001-01
CREATE SEQUENCE id_seq START WITH 1 INCREMENT BY 1;

--changeset bartlomiej_aleksiejczyk:0001-02
CREATE TABLE cv_build_job (
    id BIGINT DEFAULT nextval('id_seq') PRIMARY KEY,
    json_content TEXT NOT NULL,
    template_name VARCHAR(255) NOT NULL,
    status VARCHAR(255) NOT NULL,
    cv_compilation_result BYTEA.
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--rollback DROP TABLE cv_build_job;
--rollback DROP SEQUENCE id_seq;
