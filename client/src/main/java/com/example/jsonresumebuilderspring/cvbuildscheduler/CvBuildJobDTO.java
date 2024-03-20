package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CvBuildJobDTO {
    private String jsonContent;
    private String templateName;
}
