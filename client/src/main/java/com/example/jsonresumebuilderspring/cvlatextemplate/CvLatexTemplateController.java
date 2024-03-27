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

    //TODO: Validate if name is unique but only if deleted==true
    @PostMapping("/")
    public String createTemplate(@RequestParam("name") String name, @RequestParam("content") String content) {
        templateService.createTemplate(name, content);
        return "redirect:/templates/";
    }

    @GetMapping("/{id}/edit")
    public String editTemplateForm(@PathVariable Long id, Model model) {
        CvLatexTemplate template = templateService.getTemplateById(id).orElseThrow();
        model.addAttribute("template", template);
        return "routes/cv-templates/cv-template-edit";
    }

    //TODO: Validate if name is unique but only if deleted==true
    //TODO: Validate if new name or content is different
    @PostMapping("/{id}/edit")
    public String editTemplate(@PathVariable Long id, @RequestParam("name") String name, @RequestParam("content") String content) {
        templateService.editTemplate(id, name, content);
        return "redirect:/templates/";
    }

    @PostMapping("/{id}/delete")
    public String deleteTemplate(@PathVariable Long id) {
        templateService.deleteTemplate(id);
        return "redirect:/templates/";
    }
}
