# 백엔드 세부 구현 사항 및 인터페이스 설계서 (내부용)

- **작성일**: 2026-06-03
- **대상 범위**: 역할 4 백엔드 내부 상세 설계
- **보관 위치**: `backend/demo/src/main/java/com/example/demo/deployment/docs/`

---

## 1. 패키지 및 클래스 구조

모든 백엔드 코드는 `com.example.demo.deployment` 패키지 하위에서만 독립적으로 구성됩니다.

```
com.example.demo.deployment
 ┗ task
   ┣ controller
   ┃ ┗ TaskController.java
   ┣ domain
   ┃ ┗ Task.java
   ┣ dto
   ┃ ┣ TaskCreateRequestDto.java
   ┃ ┗ TaskResponseDto.java
   ┣ external
   ┃ ┣ ProjectServiceClient.java
   ┃ ┗ MockProjectServiceClient.java
   ┣ repository
   ┃ ┣ TaskRepository.java
   ┃ ┗ MemoryTaskRepository.java
   ┗ service
     ┗ TaskService.java
```

---

## 2. 세부 설계 및 구현 전략

### 2.1. 인메모리 격리 저장소 (`MemoryTaskRepository`)
- **목적**: 타 작업자의 인메모리 상태나 구현 여부와 무관하게 동작을 테스트할 수 있도록 독자적인 `TaskRepository` 인터페이스를 두고 `MemoryTaskRepository`에서 `CopyOnWriteArrayList`와 `AtomicLong`으로 데이터 상태를 독립적으로 관리합니다.
- **DB 배제**: JPA/RDB 연동 없이, 순수한 인메모리 동작 안정성만 확보합니다.

### 2.2. 외부 도메인 불확실성 회피 (`ProjectServiceClient`)
- **목적**: 타 팀원이 담당하는 프로젝트 도메인의 데이터 포맷이나 자료구조(ArrayList, Map 등)가 어떻게 완성될지 알 수 없습니다.
- **어댑터 격리**: 프로젝트의 존재 여부를 묻는 비즈니스 검증은 `ProjectServiceClient` 인터페이스로 정의하며, 우리 영역 내부적으로는 `MockProjectServiceClient`를 통해 항상 성공하도록 스텁(Stub) 처리해둡니다.
- **연동 유연성**: 추후 프로젝트 팀원 측에서 구조나 필드명 변경이 발생해도, 이 `ProjectServiceClient` 인터페이스의 어댑터 레이어 하나만 수정하면 우리 도메인의 핵심 로직(`TaskService`)은 전혀 영향을 받지 않습니다.

### 2.3. 유연한 DTO 설계 (스펙 변경 대응)
- **추가 필드 무시**: `TaskCreateRequestDto` 및 응답 DTO에 Jackson의 `@JsonIgnoreProperties(ignoreUnknown = true)`를 선언하여, 연동 파트너가 예상 스펙 외의 추가 필드를 포함하여 JSON 요청을 보내도 에러를 발생시키지 않고 안전하게 처리합니다.

### 2.4. D-Day 및 비즈니스 로직 구현 (`TaskService`)
- **D-Day 계산 공식**:
  - `LocalDate.now()`와 `dueDate`를 비교하여 `ChronoUnit.DAYS.between`을 통해 계산합니다.
  - 마감일 당일: `D-Day`
  - 마감일 이전: `D-X` (예: `D-5`)
  - 마감일 경과: `D+X` (예: `D+2`)
- **할 일 등록 로직**:
  1. `ProjectServiceClient`를 통해 `projectId`의 유효성 검증 (Mock 대응).
  2. D-Day 계산.
  3. `Task` Entity 생성 후 `MemoryTaskRepository`에 저장.
  4. 결과를 `TaskResponseDto`로 변환하여 반환.

---

## 3. [2라운드 추가] 2차 기능 추가 설계 및 구현 전략

### 3.1. 프로젝트/멤버 외부 Mock 서비스 고도화
- **`ProjectServiceClient` 인터페이스 확장**:
  - `List<ProjectMockDto> getProjects()`: 전체 프로젝트 조회.
  - `List<String> getMembers(Long projectId)`: 프로젝트에 소속된 멤버(이름 목록) 조회.
- **`MockProjectServiceClient` 구현**:
  - 내부에 가상의 프로젝트 2개와 프로젝트별 멤버 리스트를 사전 적재하여 호출 시 반환합니다.

### 3.2. 다중 조건 필터링 및 정렬 구현 (TaskService)
- **다중 필터링 (Stream API)**:
  - `assignee` 필터: 담당자 일치 여부 검사.
  - `dueDate` 필터: 특정 마감일 검사.
  - `priority` 필터: 우선순위 일치 여부 검사.
  - `category` 필터: 카테고리 일치 여부 검사.
  - `completed` 필터: 완료 여부 검사.
- **다중 정렬 (Comparator)**:
  - `sortBy` 파라미터에 따라 정렬 기준 동적 지정:
    - `dueDate`: 마감일 순서.
    - `priority`: 우선순위 등급에 따른 가중치 기준 정렬.
      - 가중치 맵핑: `높음` (3) ➔ `보통` (2) ➔ `낮음` (1). 내림차순(높음 ➔ 낮음) 또는 오름차순 지원.
    - `createdAt`: 등록 생성 시간 순서.
  - `sortOrder`가 `desc`일 경우 정렬 방향 반대로 구성.

### 3.3. 진행률 및 마감 임박 API 로직
- **진행률 (`GET /projects/{projectId}/progress`)**:
  - `projectId`에 매핑된 전체 할 일 개수와 완료된 할 일 개수를 카운트하여 비율 계산.
  - `(completedTasks / totalTasks) * 100` 계산 (소수점 첫째자리 반올림 포맷 지정).
- **마감 임박 (`GET /tasks/urgent`)**:
  - 전체 태스크 중 `completed == false` 이고 마감일이 오늘을 포함하여 2일 이하로 남은 모든 할 일 리스트 반환.

