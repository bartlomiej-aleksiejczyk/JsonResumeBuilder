package com.example.jsonresumebuilderspring.cvlatextemplate;

import com.example.jsonresumebuilderspring.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Entity
@Table(name = "cv_latex_template")
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CvLatexTemplate extends BaseEntity {

    @Column(length = 10485760)
    private String templateContent;
    private String templateName;

    @Column(nullable = false)
    private boolean deleted = false;

    public CvLatexTemplate(String templateName, String templateContent) {
        this.templateName = templateName;
        this.templateContent = templateContent;
    }
}
