--changeset bartlomiej_aleksiejczyk:0003-01
CREATE TABLE cv_latex_template (
    id BIGINT DEFAULT nextval('id_seq') PRIMARY KEY,
    template_content TEXT NOT NULL,
    deleted BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP NOT NULL,
    modified_at TIMESTAMP WITHOUT TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

--changeset bartlomiej_aleksiejczyk:0003-02
ALTER TABLE cv_build_job ADD COLUMN template_id BIGINT;
ALTER TABLE cv_build_job ADD CONSTRAINT fk_template_id FOREIGN KEY (template_id) REFERENCES cv_latex_template (id);
ALTER TABLE cv_build_job DROP COLUMN template_name;

--rollback ALTER TABLE cv_build_job ADD COLUMN template_name VARCHAR(255);
--rollback ALTER TABLE cv_build_job DROP COLUMN template_id;
--rollback DROP TABLE cv_latex_template;
