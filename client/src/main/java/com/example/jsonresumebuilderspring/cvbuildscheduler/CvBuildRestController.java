package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.cvbuildscheduler.dtos.CvBuildJobStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cv-build-job")
public class CvBuildRestController {

    private final CvBuildJobService cvBuildJobService;

    // TODO: Add pagination to list endpoints
    // TODO: Unify error handling, end http status generation
    @PatchMapping(value = "/{id}/status", consumes = { "multipart/form-data" })
    public ResponseEntity<CvBuildJob> updateJobStatus(
            @PathVariable Long id,
            @RequestPart("statusUpdate") CvBuildJobStatusUpdateDTO statusUpdate,
            @RequestPart(name = "compiledCvFile", required = false) MultipartFile compiledCvFile) {

        CvBuildJob updatedJob;
        if (compiledCvFile != null && !compiledCvFile.isEmpty()) {
            System.out.println(compiledCvFile.getName());
            updatedJob = cvBuildJobService.updateBuildJobStatus(id, statusUpdate.getStatus(), compiledCvFile);
        } else {
            updatedJob = cvBuildJobService.updateBuildJobStatus(id, statusUpdate.getStatus(), null);
        }

        if (updatedJob != null) {
            return ResponseEntity.ok(updatedJob);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
