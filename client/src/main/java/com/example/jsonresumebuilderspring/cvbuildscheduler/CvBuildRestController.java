package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.cvbuildscheduler.dtos.CvBuildJobStatusUpdateDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/cv-build-job")
public class CvBuildRestController {

    private final CvBuildJobService cvBuildJobService;

    @PatchMapping(value = "/{id}/status", consumes = {"multipart/form-data"})
    public ResponseEntity<CvBuildJob> updateJobStatus(
            @PathVariable Long id,
            @RequestPart("statusUpdate") CvBuildJobStatusUpdateDTO statusUpdate,
            @RequestPart("file") MultipartFile file) {

        CvBuildJob updatedJob = cvBuildJobService.updateBuildJobStatus(id, statusUpdate.getStatus(), file);

        if (updatedJob != null) {
            return ResponseEntity.ok(updatedJob);
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
