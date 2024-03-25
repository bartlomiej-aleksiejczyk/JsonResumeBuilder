--changeset bartlomiej_aleksiejczyk:0004-01
ALTER TABLE cv_latex_template ALTER COLUMN template_content TYPE TEXT;

--rollback ALTER TABLE cv_latex_template ALTER COLUMN template_content TYPE VARCHAR(255);
