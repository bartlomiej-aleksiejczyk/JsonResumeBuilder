package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.common.BaseEntity;
import com.example.jsonresumebuilderspring.cvlatextemplate.CvLatexTemplate;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "cv_build_job")

@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CvBuildJob extends BaseEntity {
    private String jsonContent;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private CvLatexTemplate template;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    byte[] cvCompilationResult;

    public CvBuildJob(String jsonContent, CvLatexTemplate template, JobStatus status) {
        this.jsonContent = jsonContent;
        this.template = template;
        this.status = status;
    }
}
