package com.example.jsonresumebuilderspring.cvbuildscheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import java.util.List;


@Controller
@RequestMapping("/jobs")
@RequiredArgsConstructor
public class CvBuildController {

    private final CvBuildJobMessageSenderService cvBuildJobMessageSenderService;

    @GetMapping("/publish")
    public String showPublishJobForm(Model model) {
        model.addAttribute("jobDTO", new CvBuildJobDTO());
        model.addAttribute("templates", List.of("Template1", "Template2", "Template3"));
        return "routes/publish-job-form";
    }

    @PostMapping("/publish")
    public String publishJob(@ModelAttribute("jobDTO") CvBuildJobDTO jobDTO, Model model) {
        cvBuildJobMessageSenderService.publishJob(jobDTO);
        return "redirect:/jobs/publish?success";
    }
}
