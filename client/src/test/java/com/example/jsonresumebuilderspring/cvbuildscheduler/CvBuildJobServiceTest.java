package com.example.jsonresumebuilderspring.cvbuildscheduler;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.jsonresumebuilderspring.cvbuildscheduler.dtos.CvBuildJobDTO;
import com.example.jsonresumebuilderspring.cvlatextemplate.CvLatexTemplate;
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

    @Test
    public void testPublishJob() throws Exception {
        ReflectionTestUtils.setField(cvBuildJobService, "celeryTaskName", "task_build_cv");
        ReflectionTestUtils.setField(cvBuildJobService, "jobExchange", "exchange");
        ReflectionTestUtils.setField(cvBuildJobService, "jobRoutingKey", "routingKey");

        CvBuildJobDTO dto = new CvBuildJobDTO();
        dto.setTemplateId(1L);
        dto.setJsonContent("json content");
        CvLatexTemplate template = new CvLatexTemplate();
        template.setTemplateContent("template content");

        when(cvBuildJobLatexTemplateMediator.getTemplateById(dto.getTemplateId())).thenReturn(Optional.of(template));
        when(cvBuildJobRepository.save(any(CvBuildJob.class))).thenAnswer(i -> i.getArgument(0));
        when(objectMapper.writeValueAsString(any())).thenReturn("serialized job");

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
        byte[] messageBody = "serialized job".getBytes();
        Message message = new Message(messageBody, messageProperties);

        cvBuildJobService.publishJob(dto);

        verify(rabbitTemplate).send(eq("exchange"), eq("routingKey"), eq(message));
        verify(objectMapper).writeValueAsString(any());
        verify(cvBuildJobRepository).save(any(CvBuildJob.class));
    }

    @Test
    public void testPublishJobTemplateNotFound() {
        CvBuildJobDTO dto = new CvBuildJobDTO();
        dto.setTemplateId(1L);
        when(cvBuildJobLatexTemplateMediator.getTemplateById(dto.getTemplateId())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> cvBuildJobService.publishJob(dto));
    }

}
