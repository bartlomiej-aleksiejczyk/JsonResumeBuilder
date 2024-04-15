package com.example.jsonresumebuilderspring.cvlatextemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

public class CvLatexTemplateServiceTest {

    @Mock
    private CvLatexTemplateRepository templateRepository;

    @InjectMocks
    private CvLatexTemplateService cvLatexTemplateService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testGetAllTemplates() {
        CvLatexTemplate template1 = new CvLatexTemplate("Templatka1", "kontent-1");
        CvLatexTemplate template2 = new CvLatexTemplate("Testowatemplatka2", "Kontent2");
        List<CvLatexTemplate> templates = Arrays.asList(template1, template2);
        when(templateRepository.findByDeletedFalse()).thenReturn(templates);

        List<CvLatexTemplate> result = cvLatexTemplateService.getAllTemplates();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(templateRepository).findByDeletedFalse();
    }

    @Test
    public void testGetTemplateByIdFound() {
        Long id = 1L;
        Optional<CvLatexTemplate> expectedTemplate = Optional.of(new CvLatexTemplate("nazwa", "kontent"));
        when(templateRepository.findById(id)).thenReturn(expectedTemplate);

        Optional<CvLatexTemplate> result = cvLatexTemplateService.getTemplateById(id);

        assertTrue(result.isPresent());
        assertEquals("nazwa", result.get().getTemplateName());
        verify(templateRepository).findById(id);
    }

    @Test
    public void testGetTemplateByIdNotFound() {
        Long id = 1L;
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        Optional<CvLatexTemplate> result = cvLatexTemplateService.getTemplateById(id);

        assertFalse(result.isPresent());
        verify(templateRepository).findById(id);
    }

    @Test
    public void testEditTemplate() {
        Long id = 1L;
        CvLatexTemplate oldTemplate = new CvLatexTemplate("Stara nazwa", "Stara tresc");
        when(templateRepository.findById(id)).thenReturn(Optional.of(oldTemplate));
        when(templateRepository.save(any(CvLatexTemplate.class))).thenAnswer(i -> i.getArguments()[0]);

        CvLatexTemplate result = cvLatexTemplateService.editTemplate(id, "Nowa nazwa", "Nowa tresc");

        assertNotNull(result);
        assertTrue(oldTemplate.isDeleted());
        assertEquals("Nowa nazwa", result.getTemplateName());
        verify(templateRepository).save(oldTemplate);
        verify(templateRepository, times(2)).save(any(CvLatexTemplate.class));
    }

    @Test
    public void testEditTemplateNotFound() {
        Long id = 1L;
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class,
                () -> cvLatexTemplateService.editTemplate(id, "Nowa nazwa", "Nowa tresc"));
    }

    @Test
    public void testCreateTemplate() {
        String name = "New Template";
        String content = "New Content";
        CvLatexTemplate newTemplate = new CvLatexTemplate(name, content);
        when(templateRepository.save(any(CvLatexTemplate.class))).thenReturn(newTemplate);

        CvLatexTemplate result = cvLatexTemplateService.createTemplate(name, content);

        assertNotNull(result);
        assertEquals(name, result.getTemplateName());
        assertEquals(content, result.getTemplateContent());
        verify(templateRepository).save(any(CvLatexTemplate.class));
    }

    @Test
    public void testDeleteTemplate() {
        Long id = 1L;
        CvLatexTemplate template = new CvLatexTemplate("Template", "Content");
        when(templateRepository.findById(id)).thenReturn(Optional.of(template));
        doAnswer(invocation -> {
            CvLatexTemplate t = invocation.getArgument(0);
            t.setDeleted(true);
            return null;
        }).when(templateRepository).save(any(CvLatexTemplate.class));

        cvLatexTemplateService.deleteTemplate(id);

        assertTrue(template.isDeleted());
        verify(templateRepository).save(template);
    }

    @Test
    public void testDeleteTemplateNotFound() {
        Long id = 1L;
        when(templateRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> cvLatexTemplateService.deleteTemplate(id));
    }

}
