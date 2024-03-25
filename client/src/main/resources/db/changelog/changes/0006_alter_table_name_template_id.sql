--changeset bartlomiej_aleksiejczyk:0006-01
--preConditions onFail:MARK_RAN
--preConditions tableExists(tableName: cv_build_job)
--preConditions columnExists(tableName: cv_build_job, columnName: template_name)
ALTER TABLE cv_build_job DROP COLUMN template_name;
--rollback ALTER TABLE cv_build_job ADD COLUMN template_name VARCHAR(255);

--changeset bartlomiej_aleksiejczyk:0006-02
--preConditions onFail:MARK_RAN
--preConditions tableExists(tableName: cv_build_job)
--preConditions not(columnExists(tableName: cv_build_job, columnName: template_id))
ALTER TABLE cv_build_job ADD COLUMN template_id BIGINT;
ALTER TABLE cv_build_job ADD CONSTRAINT fk_cv_build_job_template_id FOREIGN KEY (template_id) REFERENCES cv_latex_template (id);
--rollback ALTER TABLE cv_build_job DROP CONSTRAINT fk_cv_build_job_template_id;
--rollback ALTER TABLE cv_build_job DROP COLUMN template_id;