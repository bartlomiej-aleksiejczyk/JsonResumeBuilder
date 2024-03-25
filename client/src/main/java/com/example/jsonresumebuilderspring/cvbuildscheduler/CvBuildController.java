package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.jsonresumebuilderspring.cvbuildscheduler.exceptions.CelerySerializationException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class CvBuildController {

    private final CvBuildJobService cvBuildJobService;
    private final CvBuildJobLatexTemplateMediator cvBuildJobLatexTemplateMediator;

    @GetMapping("/new")
    public String showPublishJobForm(Model model) {
        model.addAttribute("job", new CvBuildJobDTO());
        model.addAttribute("templates", cvBuildJobLatexTemplateMediator.getAllNonDeletedTemplates());
        return "routes/jobs/publish-job-new";
    }

    @PostMapping("/new")
    public String publishJob(@ModelAttribute("jobDTO") CvBuildJobDTO jobDTO, Model model)
            throws CelerySerializationException {
        cvBuildJobService.publishJob(jobDTO);
        return "redirect:/jobs/new?success";
    }
}
