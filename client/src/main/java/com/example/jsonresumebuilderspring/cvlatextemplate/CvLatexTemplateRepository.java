package com.example.jsonresumebuilderspring.cvlatextemplate;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CvLatexTemplateRepository extends JpaRepository<CvLatexTemplate, Long> {
    List<CvLatexTemplate> findByDeletedFalse();
}
