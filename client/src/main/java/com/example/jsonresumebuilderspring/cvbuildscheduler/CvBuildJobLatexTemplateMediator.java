package com.example.jsonresumebuilderspring.cvbuildscheduler;

import org.springframework.stereotype.Component;

import com.example.jsonresumebuilderspring.cvlatextemplate.CvLatexTemplateRepository;
import com.example.jsonresumebuilderspring.cvlatextemplate.CvLatexTemplate;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CvBuildJobLatexTemplateMediator {

    private final CvLatexTemplateRepository cvLatexTemplateRepository;

    public Optional<CvLatexTemplate> getTemplateById(Long id) {
        return cvLatexTemplateRepository.findById(id);
    }

    public List<CvLatexTemplate> getAllNonDeletedTemplates() {
        return cvLatexTemplateRepository.findByDeletedFalse();
    }
}
