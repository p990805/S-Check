package com.scheck.scheck.service.studyGroup;

import com.scheck.scheck.dto.studyGroup.StudyGroupCreateRequestDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupResponseDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupSearchResponseDto;
import com.scheck.scheck.dto.studyGroup.StudyGroupUpdateRequestDto;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.studyGroup.StudyGroupRepository;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    // 스터디 그룹 생성
    @Transactional
    public StudyGroupResponseDto createStudyGroup(Long leaderId, StudyGroupCreateRequestDto requestDto) {
        // 그룹명 중복 체크
        if (studyGroupRepository.existsByGroupName(requestDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        StudyGroup studyGroup = StudyGroup.builder()
                .groupName(requestDto.getGroupName())
                .description(requestDto.getDescription())
                .leader(leader)
                .attendanceTime(requestDto.getAttendanceTime())
                .attendanceDays(requestDto.getAttendanceDays())
                .isAutoApprove(requestDto.getIsAutoApprove())
                .build();

        StudyGroup savedGroup = studyGroupRepository.save(studyGroup);
        return StudyGroupResponseDto.from(savedGroup);
    }

    // 스터디 그룹 조회
    public StudyGroupResponseDto getStudyGroup(Long groupId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        return StudyGroupResponseDto.from(studyGroup);
    }

    // 스터디 그룹 수정 (리더만 가능)
    @Transactional
    public StudyGroupResponseDto updateStudyGroup(Long groupId, Long userId, StudyGroupUpdateRequestDto requestDto) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더 권한 체크
        if (!studyGroup.isLeader(user)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 수정할 수 있습니다.");
        }

        // 그룹명 중복 체크 (기존 그룹명과 다를 경우에만)
        if (requestDto.getGroupName() != null &&
                !requestDto.getGroupName().equals(studyGroup.getGroupName()) &&
                studyGroupRepository.existsByGroupName(requestDto.getGroupName())) {
            throw new IllegalArgumentException("이미 존재하는 그룹명입니다.");
        }

        studyGroup.updateGroupInfo(
                requestDto.getGroupName(),
                requestDto.getDescription(),
                requestDto.getAttendanceTime(),
                requestDto.getAttendanceDays()
        );

        if (requestDto.getIsAutoApprove() != null) {
            studyGroup.changeAutoApprove(requestDto.getIsAutoApprove());
        }

        return StudyGroupResponseDto.from(studyGroup);
    }

    // 스터디 그룹 삭제 (리더만 가능)
    @Transactional
    public void deleteStudyGroup(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더 권한 체크
        if (!studyGroup.isLeader(user)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 삭제할 수 있습니다.");
        }

        studyGroupRepository.delete(studyGroup);
    }

    // 내가 리더인 스터디 그룹 목록
    public List<StudyGroupResponseDto> getMyLeadGroups(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findByLeaderUserId(userId);

        return studyGroups.stream()
                .map(StudyGroupResponseDto::from)
                .collect(Collectors.toList());
    }

    // 내가 속한 스터디 그룹 목록
    public List<StudyGroupResponseDto> getMyJoinedGroups(Long userId) {
        List<StudyGroup> studyGroups = studyGroupRepository.findStudyGroupsByUserId(userId);

        return studyGroups.stream()
                .map(StudyGroupResponseDto::from)
                .collect(Collectors.toList());
    }

    // 스터디 그룹 검색
    public Page<StudyGroupSearchResponseDto> searchStudyGroups(String keyword, Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupRepository.searchByKeyword(keyword, pageable);

        return studyGroups.map(group -> StudyGroupSearchResponseDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .leaderNickname(group.getLeader().getNickname())
                .isAutoApprove(group.getIsAutoApprove())
                .createdAt(group.getCreatedAt())
                // memberCount는 별도 쿼리로 조회 필요 (성능상 여기서는 제외)
                .build());
    }

    // 전체 스터디 그룹 목록 (페이징)
    public Page<StudyGroupSearchResponseDto> getAllStudyGroups(Pageable pageable) {
        Page<StudyGroup> studyGroups = studyGroupRepository.findAll(pageable);

        return studyGroups.map(group -> StudyGroupSearchResponseDto.builder()
                .groupId(group.getGroupId())
                .groupName(group.getGroupName())
                .description(group.getDescription())
                .leaderNickname(group.getLeader().getNickname())
                .isAutoApprove(group.getIsAutoApprove())
                .createdAt(group.getCreatedAt())
                .build());
    }
}