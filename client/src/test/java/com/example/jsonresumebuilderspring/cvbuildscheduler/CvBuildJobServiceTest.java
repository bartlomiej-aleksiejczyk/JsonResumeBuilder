package com.example.jsonresumebuilderspring.cvbuildscheduler;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import java.util.Optional;

public class CvBuildJobServiceTest {

    @Mock
    private RabbitTemplate rabbitTemplate;
    @Mock
    private ObjectMapper objectMapper;
    @Mock
    private CvBuildJobRepository cvBuildJobRepository;
    @Mock
    private CvBuildJobLatexTemplateMediator cvBuildJobLatexTemplateMediator;
    @InjectMocks
    private CvBuildJobService cvBuildJobService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testFindCvBuildJobByIdFound() {
        Long id = 1L;
        CvBuildJob job = new CvBuildJob();
        when(cvBuildJobRepository.findById(id)).thenReturn(Optional.of(job));

        CvBuildJob result = cvBuildJobService.findCvBuildJobById(id);

        assertNotNull(result);
        verify(cvBuildJobRepository).findById(id);
    }

    @Test
    public void testFindCvBuildJobByIdNotFound() {
        Long id = 1L;
        when(cvBuildJobRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cvBuildJobService.findCvBuildJobById(id));
    }
}
