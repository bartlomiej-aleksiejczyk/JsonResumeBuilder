package com.example.jsonresumebuilderspring.cvlatextemplate;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

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

}
