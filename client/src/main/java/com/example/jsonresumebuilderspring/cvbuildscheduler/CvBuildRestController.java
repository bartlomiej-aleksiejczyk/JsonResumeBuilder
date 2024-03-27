package com.example.jsonresumebuilderspring.cvbuildscheduler;

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
    public ResponseEntity<CvBuildJob> updateJobStatus(@PathVariable Long id, @RequestParam("status") JobStatus status) {
        return ResponseEntity.ok(cvBuildJobService.updateBuildJobStatus(id, status));
    }
}
