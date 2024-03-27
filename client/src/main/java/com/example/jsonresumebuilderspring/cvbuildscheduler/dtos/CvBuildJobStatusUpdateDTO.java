package com.example.jsonresumebuilderspring.cvbuildscheduler.dtos;

import com.example.jsonresumebuilderspring.cvbuildscheduler.JobStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class CvBuildJobStatusUpdateDTO {
    private JobStatus status;
}
