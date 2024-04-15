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
}
