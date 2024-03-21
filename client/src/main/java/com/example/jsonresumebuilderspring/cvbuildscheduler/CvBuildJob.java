package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.common.BaseEntity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class CvBuildJob extends BaseEntity {
    private String jsonContent;
    private String templateName;

    @Enumerated(EnumType.STRING)
    private JobStatus status;
}
