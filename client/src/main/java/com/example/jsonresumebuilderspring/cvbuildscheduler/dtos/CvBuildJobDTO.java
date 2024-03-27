package com.example.jsonresumebuilderspring.cvbuildscheduler.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CvBuildJobDTO {
    private String jsonContent;
    private Long templateId;
}
