# 백엔드 세부 작업 계획서 (3단계 산출물)

이 문서는 역할 4(할 일 등록 및 분배) 백엔드 모듈 개발을 위해 쪼갠 세부 작업 계획서입니다.

---

## 1. 구현 대상 및 클래스별 상세 계획

### [Task 1] `Task` 도메인 및 DTO 개발
- **Domain (`Task.java`)**
  - 속성: `id` (Long), `projectId` (Long), `title` (String), `description` (String), `assignee` (String), `category` (String), `priority` (String), `dueDate` (LocalDate), `completed` (Boolean), `createdAt` (LocalDateTime), `updatedAt` (LocalDateTime).
  - 로직: 생성일/수정일 자동 지정을 위한 로직 내장.
- **DTO (`TaskCreateRequestDto.java`, `TaskResponseDto.java`)**
  - **유연성 적용**: `@JsonIgnoreProperties(ignoreUnknown = true)` 어노테이션 적용하여 외부의 불필요한 필드를 무시.
  - **검증**: `title`(NotBlank), `dueDate`(NotNull) 필수값 검증 적용.

### [Task 2] 인메모리 격리 저장소 개발
- **Interface (`TaskRepository.java`)**
  - `save(Task task)` 시그니처 정의.
- **Implementation (`MemoryTaskRepository.java`)**
  - `CopyOnWriteArrayList<Task>`를 활용해 데이터 보관 (동시성 제어).
  - `AtomicLong`을 사용해 ID 순차 발급.
  - `@PostConstruct`를 사용하여 서버 가동 시 3개 내외의 Mock 할 일 데이터를 미리 저장소에 주입.

### [Task 3] 외부 연동 클라이언트 모킹 개발
- **Interface (`ProjectServiceClient.java`)**
  - `validateProjectId(Long projectId)` 함수 정의.
- **Mock Implementation (`MockProjectServiceClient.java`)**
  - 다른 팀원의 프로젝트 데이터 구조를 알지 못하므로, 프로젝트 검증 요청에 대해 항상 `true`를 반환하는 Stub 로직 작성.

### [Task 4] 비즈니스 서비스 및 컨트롤러 개발
- **Service (`TaskService.java`)**
  - D-Day 계산 로직 구현 (`ChronoUnit.DAYS` 활용).
    - 남은 일수 계산 후 `D-X`, `D-Day`, `D+X` 형식의 문자열 생성.
  - 등록 메소드 구현: 프로젝트 ID 유효성 검증 -> D-Day 계산 -> DB 저장 -> ResponseDTO 반환.
- **Controller (`TaskController.java`)**
  - `POST /api/v1/projects/{projectId}/tasks` 매핑.
  - `@Valid`를 활용한 바인딩 예외 처리 및 `@CrossOrigin`을 사용한 CORS 정책 설정.

---

## 2. 단위 테스트 계획 (JUnit 5)

- **D-Day 계산 정확성 검증**:
  - `dueDate`가 오늘인 경우 ➔ `D-Day` 반환 검증.
  - `dueDate`가 5일 뒤인 경우 ➔ `D-5` 반환 검증.
  - `dueDate`가 2일 전인 경우 ➔ `D+2` 반환 검증.
- **DTO 탄력성 검증**:
  - Jackson ObjectMapper를 활용해 `{"title":"태스크", "dueDate":"2026-06-20", "unknownField":"val"}` 형식의 미정의 필드가 유입될 때 예외 없이 DTO 객체로 정상 변환되는지 테스트 수행.

---

## 2. [2라운드 추가] 2차 기능 보강 개발 계획 (Task 5~8)

### [Task 5] 프로젝트 및 멤버 조회 Mock API 구현
- `MockProjectServiceClient`에 프로젝트 2개 및 소속 멤버 셋 정의.
- `getProjects()`, `getMembers(Long projectId)` 구현.

### [Task 6] 다중 쿼리 파라미터 필터링 및 정렬 구현 (`TaskService`)
- `assignee`, `dueDate`, `priority`, `category`, `completed` 다중 쿼리 필터 추가.
- `sortBy` (dueDate, priority, createdAt) 및 `sortOrder` (asc, desc) 정렬 조건 Comparator 로직 구현.
- 우선순위 등급(`높음` ➔ 3, `보통` ➔ 2, `낮음` ➔ 1) 가중치 변환 정렬 지원.

### [Task 7] 진행률 및 마감임박 API 구현
- `/projects/{projectId}/progress` 진행률 계산 기능.
- `/tasks/urgent` 미완료 D-2 이하 전체 할 일 필터 조회 기능.

### [Task 8] JUnit 5 테스트 및 컨트롤러 추가 매핑
- `@RequestParam` 바인딩 적용.
- 다중 필터 및 우선순위/마감일 정렬이 포함된 `TaskService` 단위 테스트 작성 및 통과 검증.

