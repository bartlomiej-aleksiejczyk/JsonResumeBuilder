package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.common.BaseEntity;
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
    private String templateName;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Lob
    byte[] cvCompilationResult;

    public CvBuildJob(String jsonContent, String templateName, JobStatus status) {
        this.jsonContent = jsonContent;
        this.templateName = templateName;
        this.status = status;
    }
}
