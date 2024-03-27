package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.cvbuildscheduler.dtos.CvBuildJobStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cv-build-job")
public class CvBuildRestController {

    private final CvBuildJobService cvBuildJobService;

    @PatchMapping("/{id}/status")
    public ResponseEntity<CvBuildJob> updateJobStatus(@PathVariable Long id, @RequestBody CvBuildJobStatusUpdateDTO statusUpdate) {
        return ResponseEntity.ok(cvBuildJobService.updateBuildJobStatus(id, statusUpdate.getStatus()));
    }
}
