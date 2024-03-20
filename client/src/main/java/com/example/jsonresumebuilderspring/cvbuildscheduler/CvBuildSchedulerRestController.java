package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cv-build-job")
public class CvBuildSchedulerRestController {

    //TODO: Remove direct repository use
    private final CvBuildJobRepository cvBuildJobRepository;
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateJobStatus(@PathVariable Long id, @RequestParam("status") JobStatus status) {
        return cvBuildJobRepository.findById(id).map(job -> {
            job.setStatus(status);
            cvBuildJobRepository.save(job);
            return ResponseEntity.ok().build();
        }).orElse(ResponseEntity.notFound().build());
    }
}
