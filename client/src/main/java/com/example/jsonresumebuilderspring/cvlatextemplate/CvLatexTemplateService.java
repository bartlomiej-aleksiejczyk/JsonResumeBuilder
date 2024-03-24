package com.example.jsonresumebuilderspring.cvlatextemplate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CvLatexTemplateService {

    private final CvLatexTemplateRepository templateRepository;

    public List<CvLatexTemplate> getAllTemplates() {
        return templateRepository.findByDeletedFalse();
    }

    public Optional<CvLatexTemplate> getTemplateById(Long id) {
        return templateRepository.findById(id);
    }

    public CvLatexTemplate createTemplate(String content) {
        CvLatexTemplate newTemplate = new CvLatexTemplate(content);
        return templateRepository.save(newTemplate);
    }

    public CvLatexTemplate editTemplate(Long id, String newContent) {
        CvLatexTemplate oldTemplate = templateRepository.findById(id).orElseThrow();
        oldTemplate.setDeleted(true);
        templateRepository.save(oldTemplate);

        return createTemplate(newContent);
    }

    public void deleteTemplate(Long id) {
        CvLatexTemplate template = templateRepository.findById(id).orElseThrow();
        template.setDeleted(true);
        templateRepository.save(template);
    }
}
