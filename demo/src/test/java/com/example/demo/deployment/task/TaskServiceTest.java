package com.example.demo.deployment.task;

import com.example.demo.deployment.task.dto.TaskCreateRequestDto;
import com.example.demo.deployment.task.dto.TaskResponseDto;
import com.example.demo.deployment.task.external.MockProjectServiceClient;
import com.example.demo.deployment.task.repository.MemoryTaskRepository;
import com.example.demo.deployment.task.service.TaskService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TaskServiceTest {

    private TaskService taskService;
    private MemoryTaskRepository taskRepository;

    @BeforeEach
    void setUp() {
        taskRepository = new MemoryTaskRepository();
        // PostConstruct를 테스트에서는 수동으로 실행해주거나, clear 처리하여 깨끗한 상태에서 시작
        taskRepository.clear();
        
        MockProjectServiceClient projectServiceClient = new MockProjectServiceClient();
        taskService = new TaskService(taskRepository, projectServiceClient);
    }

    @Test
    void testCalculateDDay_Today() {
        LocalDate today = LocalDate.now();
        String dday = taskService.calculateDDay(today);
        assertEquals("D-Day", dday);
    }

    @Test
    void testCalculateDDay_Future() {
        LocalDate future = LocalDate.now().plusDays(5);
        String dday = taskService.calculateDDay(future);
        assertEquals("D-5", dday);
    }

    @Test
    void testCalculateDDay_Past() {
        LocalDate past = LocalDate.now().minusDays(2);
        String dday = taskService.calculateDDay(past);
        assertEquals("D+2", dday);
    }

    @Test
    void testCreateTask_Success() {
        TaskCreateRequestDto request = new TaskCreateRequestDto(
                "테스트 할 일",
                "테스트 설명",
                "홍길동",
                "자료조사",
                "보통",
                LocalDate.now().plusDays(3)
        );

        TaskResponseDto response = taskService.createTask(1L, request);

        assertNotNull(response.getTaskId());
        assertEquals("테스트 할 일", response.getTitle());
        assertEquals("D-3", response.getDday());
        assertFalse(response.getCompleted());
        assertEquals(1, taskRepository.findAll().size());
    }

    @Test
    void testCreateTask_InvalidInput() {
        TaskCreateRequestDto emptyTitleRequest = new TaskCreateRequestDto(
                "",
                "설명",
                "담당자",
                "카테고리",
                "보통",
                LocalDate.now()
        );

        assertThrows(IllegalArgumentException.class, () -> 
            taskService.createTask(1L, emptyTitleRequest)
        );
    }

    @Test
    void testDtoDeserializationResilience() throws Exception {
        // JSON 문자열에 정의되지 않은 unknown_field가 포함되어도 역직렬화에 성공하는지 검증
        String json = "{\"title\":\"테스크\",\"dueDate\":\"2026-06-20\",\"unknown_field\":\"ignored_value\"}";
        ObjectMapper objectMapper = new ObjectMapper();
        
        // LocalDate 역직렬화를 지원하기 위해 javaTimeModule을 명시적으로 등록하지 않아도 에러 없이 처리되는지 확인
        // 만약 javaTimeModule이 없어 LocalDate 파싱에 문제가 생긴다면 ObjectMapper 설정을 보완해야 함.
        // Spring Boot 환경에서는 자동 주입된 ObjectMapper가 알아서 지원함.
        // 여기서는 수동 테스트이므로 String 타입 변환 등을 추가 확인.
        try {
            objectMapper.findAndRegisterModules(); // LocalDate 지원 등록
            TaskCreateRequestDto dto = objectMapper.readValue(json, TaskCreateRequestDto.class);
            assertEquals("테스크", dto.getTitle());
            assertEquals(LocalDate.of(2026, 6, 20), dto.getDueDate());
        } catch (Exception e) {
            fail("Jackson 역직렬화 예외 발생: " + e.getMessage());
        }
    }

    @Test
    void testGetTasksFiltered_ByAssignee() {
        taskService.createTask(1L, new TaskCreateRequestDto("Task 1", "D1", "홍길동", "카테고리", "높음", LocalDate.now().plusDays(2)));
        taskService.createTask(1L, new TaskCreateRequestDto("Task 2", "D2", "김철수", "카테고리", "보통", LocalDate.now().plusDays(3)));

        List<TaskResponseDto> results = taskService.getTasksFiltered(1L, "홍길동", null, null, null, null, "dueDate", "asc");
        assertEquals(1, results.size());
        assertEquals("Task 1", results.get(0).getTitle());
    }

    @Test
    void testGetTasksFiltered_SortingPriorityDesc() {
        taskService.createTask(1L, new TaskCreateRequestDto("Task Low", "D1", "홍길동", "카테고리", "낮음", LocalDate.now().plusDays(5)));
        taskService.createTask(1L, new TaskCreateRequestDto("Task High", "D2", "김철수", "카테고리", "높음", LocalDate.now().plusDays(2)));
        taskService.createTask(1L, new TaskCreateRequestDto("Task Normal", "D3", "이영희", "카테고리", "보통", LocalDate.now().plusDays(3)));

        // 우선순위 내림차순 정렬 (높음(3) -> 보통(2) -> 낮음(1))
        List<TaskResponseDto> results = taskService.getTasksFiltered(1L, null, null, null, null, null, "priority", "desc");
        assertEquals(3, results.size());
        assertEquals("Task High", results.get(0).getTitle());
        assertEquals("Task Normal", results.get(1).getTitle());
        assertEquals("Task Low", results.get(2).getTitle());
    }

    @Test
    void testGetProgress_Calculation() {
        taskService.createTask(1L, new TaskCreateRequestDto("Task 1", "D1", "홍길동", "카테고리", "높음", LocalDate.now().plusDays(2)));
        taskService.createTask(1L, new TaskCreateRequestDto("Task 2", "D2", "김철수", "카테고리", "보통", LocalDate.now().plusDays(3)));

        // 임의로 1개 완료 처리
        taskRepository.findAll().get(0).setCompleted(true);

        com.example.demo.deployment.task.dto.ProjectProgressDto progressDto = taskService.getProgress(1L);
        assertEquals(50.0, progressDto.getProgress());
        assertEquals(2, progressDto.getTotalTasks());
        assertEquals(1, progressDto.getCompletedTasks());
    }

    @Test
    void testGetUrgentTasks() {
        taskService.createTask(1L, new TaskCreateRequestDto("Urgent Task", "D1", "홍길동", "카테고리", "높음", LocalDate.now().plusDays(1)));
        taskService.createTask(1L, new TaskCreateRequestDto("Far Task", "D2", "김철수", "카테고리", "보통", LocalDate.now().plusDays(10)));

        List<TaskResponseDto> urgentList = taskService.getUrgentTasks();
        assertEquals(1, urgentList.size());
        assertEquals("Urgent Task", urgentList.get(0).getTitle());
    }
}
