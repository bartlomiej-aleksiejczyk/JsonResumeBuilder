--liquibase formatted sql

--changeset bartlomiej_aleksiejczyk:0001-01
CREATE SEQUENCE cv_build_job_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE cv_latex_template_seq START WITH 1 INCREMENT BY 1;

--rollback DROP SEQUENCE cv_build_job_seq;
--rollback DROP SEQUENCE cv_latex_template_seq;

--changeset bartlomiej_aleksiejczyk:0001-02
CREATE TABLE cv_build_job (
    id BIGINT DEFAULT nextval('cv_build_job_seq') PRIMARY KEY,
    template_id BIGINT,
    json_content TEXT NOT NULL,
    status VARCHAR(255) NOT NULL,
    cv_compilation_result BYTEA,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE cv_build_job;

--changeset bartlomiej_aleksiejczyk:0001-03
CREATE TABLE cv_latex_template (
    id BIGINT DEFAULT nextval('cv_latex_template_seq') PRIMARY KEY,
    template_name VARCHAR(255) NOT NULL,
    template_content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);
--rollback DROP TABLE cv_latex_template;

--changeset bartlomiej_aleksiejczyk:0001-04
ALTER TABLE cv_build_job ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id) REFERENCES cv_latex_template (id);
--rollback ALTER TABLE cv_build_job DROP CONSTRAINT fk_template_id;
