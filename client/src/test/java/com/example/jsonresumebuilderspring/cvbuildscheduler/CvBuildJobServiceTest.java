package com.example.jsonresumebuilderspring.cvbuildscheduler;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.persistence.EntityNotFoundException;

import java.io.IOException;
import java.util.List;
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

    @Test
    public void testUpdateBuildJobStatusWithFile() throws IOException {
        Long id = 1L;
        JobStatus status = JobStatus.COMPLETED;
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn(new byte[] { 1, 2, 3 });
        CvBuildJob job = new CvBuildJob();
        when(cvBuildJobRepository.findById(id)).thenReturn(Optional.of(job));

        CvBuildJob result = cvBuildJobService.updateBuildJobStatus(id, status, file);

        assertEquals(JobStatus.COMPLETED, result.getStatus());
        assertNotNull(result.getCvCompilationResult());
        verify(cvBuildJobRepository).saveAndFlush(job);
    }

    @Test
    public void testUpdateBuildJobStatusWithoutFile() {
        Long id = 1L;
        JobStatus status = JobStatus.IN_PROGRESS;
        CvBuildJob job = new CvBuildJob();
        when(cvBuildJobRepository.findById(id)).thenReturn(Optional.of(job));

        CvBuildJob result = cvBuildJobService.updateBuildJobStatus(id, status, null);

        assertEquals(JobStatus.IN_PROGRESS, result.getStatus());
        assertNull(result.getCvCompilationResult());
        verify(cvBuildJobRepository).saveAndFlush(job);
    }

    @Test
    public void testGetAllJobs() {
        List<CvBuildJob> jobs = List.of(new CvBuildJob(), new CvBuildJob());
        when(cvBuildJobRepository.findAll()).thenReturn(jobs);

        List<CvBuildJob> result = cvBuildJobService.getAllJobs();

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(cvBuildJobRepository).findAll();
    }
}
