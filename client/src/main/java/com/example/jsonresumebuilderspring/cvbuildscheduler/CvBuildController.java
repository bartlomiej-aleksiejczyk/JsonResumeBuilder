package com.example.jsonresumebuilderspring.cvbuildscheduler;

import com.example.jsonresumebuilderspring.cvbuildscheduler.dtos.CvBuildJobDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import com.example.jsonresumebuilderspring.cvbuildscheduler.exceptions.CelerySerializationException;

import org.springframework.ui.Model;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class CvBuildController {

    private final CvBuildJobService cvBuildJobService;
    private final CvBuildJobLatexTemplateMediator cvBuildJobLatexTemplateMediator;

    @GetMapping("")
    public String listJobs(Model model) {
        model.addAttribute("jobs", cvBuildJobService.getAllJobs());
        return "routes/jobs/cv-build-job-list";
    }

    @GetMapping("/{id}")
    public String showJobDetails(@PathVariable Long id, Model model) {
        model.addAttribute("job", cvBuildJobService.findCvBuildJobById(id));
        return "routes/jobs/cv-build-job-preview";
    }

    @GetMapping("/{jobId}/compilation-result")
    public ResponseEntity<byte[]> downloadCompiledFile(@PathVariable Long jobId) {
        CvBuildJob job = cvBuildJobService.findCvBuildJobById(jobId);

        byte[] content = job.getCvCompilationResult();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("filename", "compiled_job_" + jobId + ".pdf");
        headers.setContentType(MediaType.APPLICATION_PDF);

        return new ResponseEntity<>(content, headers, HttpStatus.OK);
    }

    //TODO: Validate form
    @GetMapping("/new")
    public String showPublishJobForm(Model model) {
        model.addAttribute("job", new CvBuildJobDTO());
        model.addAttribute("templates", cvBuildJobLatexTemplateMediator.getAllNonDeletedTemplates());
        return "routes/jobs/cv-build-job-new";
    }

    @PostMapping("")
    public String publishJob(@ModelAttribute("jobDTO") CvBuildJobDTO jobDTO, Model model)
            throws CelerySerializationException {
        cvBuildJobService.publishJob(jobDTO);
        return "redirect:/jobs/new?success";
    }
}
