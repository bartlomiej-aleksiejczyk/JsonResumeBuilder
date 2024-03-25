package com.example.jsonresumebuilderspring.cvlatextemplate;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/templates")
@RequiredArgsConstructor
public class CvLatexTemplateController {

    private final CvLatexTemplateService templateService;

    @GetMapping("/")
    public String listTemplates(Model model) {
        model.addAttribute("templates", templateService.getAllTemplates());
        return "routes/cv-templates/cv-template-list"; // Thymeleaf template path
    }

    @GetMapping("/new")
    public String newTemplateForm(Model model) {
        model.addAttribute("template", new CvLatexTemplate());
        return "routes/cv-templates/cv-template-new";
    }

    @PostMapping("/")
    public String createTemplate(@RequestParam("content") String content) {
        templateService.createTemplate(content);
        return "redirect:/templates/list";
    }

    @GetMapping("/{id}/edit")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        CvLatexTemplate template = templateService.getTemplateById(id).orElseThrow();
        model.addAttribute("template", template);
        return "routes/cv-templates/cv-template-edit";
    }

    @PostMapping("/{id}/edit")
    public String editTemplate(@PathVariable Long id, @RequestParam("content") String content) {
        templateService.editTemplate(id, content);
        return "redirect:/templates/list";
    }

    @PostMapping("/{id}/delete")
    public String deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return "redirect:/templates/list";
    }
}
