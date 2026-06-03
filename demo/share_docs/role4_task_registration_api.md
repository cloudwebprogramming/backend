# [역할 4] 할일 등록 및 분배 백엔드 API 연동 명세 (2라운드)

이 문서는 역할 4(할 일 등록 및 분배) 개발 영역과 다른 팀원들의 도메인(프로젝트 관리, 팀원 관리, 마이페이지 등) 간의 원활한 데이터 통신 및 필드명 합의를 위해 작성된 공동 작업 가이드라인입니다.

---

## 1. 아키텍처 개요 및 메모리 DB 정책
- **독립성 유지**: 본 모듈은 ArrayList 기반의 인메모리 저장소(`MemoryTaskRepository`)를 독립적으로 유지합니다.
- **연동 방식**: 다른 팀원이 개발하는 도메인 데이터와는 API 네트워크 통신을 통해서만 데이터를 주고받으며, 개발 단계에서는 인터페이스 Stubbing을 통해 상호 독립적으로 구동됩니다.

---

## 2. [파트 A] 제공받아야 할 API 명세 (타 팀원이 구현해야 하는 스펙)

우리 모달 화면에서 프로젝트 목록을 조회하고, 선택된 프로젝트의 팀원 목록을 불러오기 위해 다음 API 및 데이터 구조를 제공받아야 합니다.

### 2.1. 전체 프로젝트 목록 조회 API
- **Method**: `GET`
- **URL**: `/api/v1/projects`
- **상태**: 프로젝트 등록 담당자(역할 2)가 구현할 영역.
- **합의된 응답 바디 구조**:
```json
[
  {
    "id": 1,
    "title": "클라우드기반웹개발 팀프로젝트",
    "subject": "클라우드웹개발",
    "description": "Todo 기능 확장 프로젝트"
  },
  {
    "id": 2,
    "title": "데이터베이스 발표 과제",
    "subject": "데이터베이스",
    "description": "발표 및 기획 보고서 작성"
  }
]
```
| 필드명 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- |
| `id` | Long | **필수** | 프로젝트 고유 식별자 |
| `title` | String | **필수** | 프로젝트 제목 |
| `subject` | String | 선택 | 과목명 |
| `description` | String | 선택 | 프로젝트 세부 설명 |

### 2.2. 프로젝트 소속 팀원 목록 조회 API
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/members`
- **상태**: 로그인/회원 및 팀원 초대 코드 담당자(역할 1, 2)가 구현할 영역.
- **합의된 응답 바디 구조**:
```json
[
  {
    "username": "홍길동",
    "role": "팀장"
  },
  {
    "username": "김철수",
    "role": "팀원"
  }
]
```
| 필드명 | 타입 | 필수 여부 | 설명 |
| :--- | :--- | :--- | :--- |
| `username` | String | **필수** | 팀원 이름 (할 일 배분 시 담당자 지정 필드 `assignee`와 매핑됨) |
| `role` | String | 선택 | 프로젝트 내 역할 |

---

## 3. [파트 B] 제공할 API 명세 (우리가 타 팀원에게 제공하는 스펙)

프로젝트 상세 화면(역할 3), 할 일 상세 관리(역할 5), 마이페이지(역할 6) 담당 팀원들이 우리 태스크 데이터를 조회 및 연동할 수 있도록 아래 API들을 제공합니다.

### 3.1. 할 일 등록 API
- **Method**: `POST`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **Content-Type**: `application/json`
- **요청 바디**:
```json
{
  "title": "발표 PPT 초안 작성",
  "description": "발표용 1차 PPT 템플릿 및 내용 작성",
  "assignee": "홍길동",
  "category": "발표자료",
  "priority": "높음",
  "dueDate": "2026-06-15"
}
```
- **응답 바디 (201 Created)**:
```json
{
  "taskId": 12,
  "projectId": 1,
  "title": "발표 PPT 초안 작성",
  "assignee": "홍길동",
  "category": "발표자료",
  "priority": "높음",
  "dueDate": "2026-06-15",
  "dday": "D-12",
  "completed": false
}
```

### 3.2. 특정 프로젝트의 할 일 목록 조회 API
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **설명**: 프로젝트 상세 조회 및 할일 관리 팀원(역할 3, 5)이 화면에 할 일 리스트를 렌더링하기 위해 호출하는 API입니다.
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
### 3.2. 특정 프로젝트의 할 일 목록 조회 (전체 조회)
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **설명**: 필터 조건 없이 특정 프로젝트의 전체 할 일을 마감일 기준으로 정렬하여 반환합니다.
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
    "dday": "D-3",
    "completed": false
  }
]
```

### 3.3. 특정 인원이 담당한 할 일 목록 조회 API (3.5 요구사항)
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **쿼리 파라미터**: `assignee={assigneeName}`
- **설명**: 특정 프로젝트 내에서 특정 담당자에게 배분된 할 일 목록만 필터링하여 조회합니다.
- **요청 예시**: `GET /api/v1/projects/1/tasks?assignee=홍길동`
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
    "dday": "D-3",
    "completed": false
  }
]
```

### 3.4. 특정 날짜의 할 일 목록 조회 API (3.6 요구사항)
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **쿼리 파라미터**: `dueDate={yyyy-MM-dd}`
- **설명**: 특정 마감일에 완료해야 하는 할 일 목록만 필터링하여 조회합니다.
- **요청 예시**: `GET /api/v1/projects/1/tasks?dueDate=2026-06-06`
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
    "dday": "D-3",
    "completed": false
  }
]
```

### 3.5. 우선순위별 할 일 목록 조회 API (3.7 요구사항)
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **쿼리 파라미터**: `priority={높음|보통|낮음}`
- **설명**: 중요도/우선순위에 따라 할 일 목록을 필터링하여 조회합니다.
- **요청 예시**: `GET /api/v1/projects/1/tasks?priority=높음`
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
    "dday": "D-3",
    "completed": false
  }
]
```

### 3.6. 다중 조건 필터 및 정렬 API (3.8 요구사항)
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/tasks`
- **쿼리 파라미터**:
  - `assignee` (담당자 필터, 선택)
  - `dueDate` (마감일 필터, 선택)
  - `priority` (우선순위 필터, 선택)
  - `completed` (완료 상태 필터, 선택)
  - `sortBy` (정렬 기준: `dueDate`, `priority`, `createdAt` 중 선택)
  - `sortOrder` (정렬 방향: `asc`, `desc`)
- **설명**: 담당자, 날짜, 우선순위 등의 필터를 조합하여 동적으로 다중 필터링하고 지정된 정렬 기준(우선순위 가중치 정렬 등)으로 정렬하여 결과를 반환합니다.
- **요청 예시**: `GET /api/v1/projects/1/tasks?assignee=홍길동&priority=높음&sortBy=priority&sortOrder=desc`
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 1,
    "projectId": 1,
    "title": "발표 PPT 템플릿 제작",
    "assignee": "홍길동",
    "category": "발표자료",
    "priority": "높음",
    "dueDate": "2026-06-06",
    "dday": "D-3",
    "completed": false
  }
]
```

### 3.7. 특정 프로젝트 진행률 조회 API
- **Method**: `GET`
- **URL**: `/api/v1/projects/{projectId}/progress`
- **설명**: 메인 화면이나 프로젝트 목록 화면에서 진행률 바(CSS `width`용)를 그리기 위해 호출하는 API입니다.
- **응답 바디 (200 OK)**:
```json
{
  "projectId": 1,
  "progress": 66.7,
  "totalTasks": 3,
  "completedTasks": 2
}
```
| 필드명 | 타입 | 설명 |
| :--- | :--- | :--- |
| `projectId` | Long | 대상 프로젝트 ID |
| `progress` | Double | 완료 백분율 (완료수 / 전체수 * 100). 소수점 첫째짜리 반올림 |
| `totalTasks` | Integer | 프로젝트의 전체 할 일 개수 |
| `completedTasks` | Integer | 완료 처리된 할 일 개수 |

### 3.8. 마감 임박 미완료 할 일 전체 조회 API
- **Method**: `GET`
- **URL**: `/api/v1/tasks/urgent`
- **설명**: 마이페이지(역할 6) 담당자가 로그인한 사용자나 전체 팀의 마감 임박(D-2 이하 및 미완료) 태스크를 경고 배너로 띄울 때 호출하는 API입니다.
- **응답 바디 (200 OK)**:
```json
[
  {
    "taskId": 3,
    "projectId": 2,
    "title": "요구사항 분석 보고서 작성",
    "assignee": "이영희",
    "category": "보고서",
    "priority": "보통",
    "dueDate": "2026-06-05",
    "dday": "D-2",
    "completed": false
  }
]
```
