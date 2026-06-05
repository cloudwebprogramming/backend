package com.example.demo.deployment.task.external;

import com.example.demo.deployment.task.dto.ProjectMockDto;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 타 조원 영역(프로젝트/멤버)과 무관하게 동작하도록 하는 테스트/데모용 Stub 구현.
 * 실제 빈으로는 RealProjectServiceClient(@Primary)가 사용되며, 본 클래스는 단위 테스트에서 직접 생성하여 사용한다.
 */
public class MockProjectServiceClient implements ProjectServiceClient {

    @Override
    public boolean validateProjectId(Long projectId) {
        // 데모용 프로젝트 1, 2번을 유효한 것으로 취급
        return projectId != null && (projectId == 1L || projectId == 2L);
    }

    @Override
    public List<ProjectMockDto> getProjects() {
        return Arrays.asList(
                new ProjectMockDto(1L, "클라우드웹개발", "클라우드기반웹개발", "클라우드웹개발 팀프로젝트"),
                new ProjectMockDto(2L, "데이터베이스", "데이터베이스", "데이터베이스 발표 과제")
        );
    }

    @Override
    public List<String> getMembers(Long projectId) {
        if (projectId == null) {
            return Collections.emptyList();
        }
        if (projectId == 1L) {
            return Arrays.asList("홍길동", "김철수");
        }
        if (projectId == 2L) {
            return Arrays.asList("이영희", "박민수");
        }
        return Collections.emptyList();
    }
}
