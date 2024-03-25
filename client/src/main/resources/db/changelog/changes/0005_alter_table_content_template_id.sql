--changeset bartlomiej_aleksiejczyk:0005-01
--preConditions onFail:MARK_RAN
--preConditions tableExists(tableName: cv_build_job)
--preConditions columnExists(tableName: cv_build_job, columnName: template_content)
ALTER TABLE cv_build_job DROP COLUMN template_content;
--rollback ALTER TABLE cv_build_job ADD COLUMN template_content VARCHAR(255); 

--changeset bartlomiej_aleksiejczyk:0005-02
--preConditions onFail:MARK_RAN
--preConditions tableExists(tableName: cv_build_job)
--preConditions not(columnExists(tableName: cv_build_job, columnName: template_id))
ALTER TABLE cv_build_job ADD COLUMN template_id BIGINT;
ALTER TABLE cv_build_job ADD CONSTRAINT fk_cv_build_job_template_id FOREIGN KEY (template_id) REFERENCES cv_latex_template (id);
--rollback ALTER TABLE cv_build_job DROP CONSTRAINT fk_cv_build_job_template_id;
--rollback ALTER TABLE cv_build_job DROP COLUMN template_id;