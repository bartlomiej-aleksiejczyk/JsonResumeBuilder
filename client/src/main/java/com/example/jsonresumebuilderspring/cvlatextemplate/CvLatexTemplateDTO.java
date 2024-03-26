package com.example.jsonresumebuilderspring.cvlatextemplate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class CvLatexTemplateDTO {
    private Long id;
    private String templateContent;
    private boolean deleted;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    public static CvLatexTemplateDTO fromEntity(CvLatexTemplate template) {
        CvLatexTemplateDTO dto = new CvLatexTemplateDTO();
        dto.setId(template.getId());
        dto.setTemplateContent(template.getTemplateContent());
        dto.setDeleted(template.isDeleted());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setModifiedAt(template.getModifiedAt());
        return dto;
    }
}
