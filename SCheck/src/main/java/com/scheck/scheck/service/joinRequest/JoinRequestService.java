package com.scheck.scheck.service.joinRequest;

import com.scheck.scheck.dto.joinRequest.JoinRequestCreateDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestProcessDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestResponseDto;
import com.scheck.scheck.dto.joinRequest.JoinRequestSummaryDto;
import com.scheck.scheck.entity.groupMember.GroupMember;
import com.scheck.scheck.entity.joinRequest.JoinRequest;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.groupMember.GroupMemberRepository;
import com.scheck.scheck.repository.joinRequest.JoinRequestRepository;
import com.scheck.scheck.repository.studyGroup.StudyGroupRepository;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class JoinRequestService {

    private final JoinRequestRepository joinRequestRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    // 가입 신청
    @Transactional
    public JoinRequestResponseDto createJoinRequest(Long userId, JoinRequestCreateDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        StudyGroup studyGroup = studyGroupRepository.findById(requestDto.getGroupId())
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        // 이미 멤버인지 확인
        if (groupMemberRepository.isActiveMember(requestDto.getGroupId(), userId)) {
            throw new IllegalArgumentException("이미 해당 그룹의 멤버입니다.");
        }

        // 이미 대기 중인 신청이 있는지 확인
        if (joinRequestRepository.existsByStudyGroupAndUserAndStatus(
                studyGroup, user, JoinRequest.RequestStatus.PENDING)) {
            throw new IllegalArgumentException("이미 대기 중인 가입 신청이 있습니다.");
        }

        // 자동 승인인 경우
        if (studyGroup.getIsAutoApprove()) {
            return autoApprove(studyGroup, user, requestDto.getMessage());
        }

        // 수동 승인인 경우
        JoinRequest joinRequest = JoinRequest.builder()
                .studyGroup(studyGroup)
                .user(user)
                .message(requestDto.getMessage())
                .build();

        JoinRequest savedRequest = joinRequestRepository.save(joinRequest);
        return JoinRequestResponseDto.from(savedRequest);
    }

    // 자동 승인 처리
    @Transactional
    public JoinRequestResponseDto autoApprove(StudyGroup studyGroup, User user, String message) {
        // 가입 신청 생성 및 즉시 승인
        JoinRequest joinRequest = JoinRequest.builder()
                .studyGroup(studyGroup)
                .user(user)
                .message(message)
                .build();

        joinRequest.approve(studyGroup.getLeader());
        JoinRequest savedRequest = joinRequestRepository.save(joinRequest);

        // 그룹 멤버에 추가
        GroupMember groupMember = GroupMember.builder()
                .studyGroup(studyGroup)
                .user(user)
                .approvedBy(studyGroup.getLeader())
                .build();

        groupMemberRepository.save(groupMember);

        return JoinRequestResponseDto.from(savedRequest);
    }

    // 가입 신청 처리 (승인/거절)
    @Transactional
    public JoinRequestResponseDto processJoinRequest(Long requestId, Long processerId, JoinRequestProcessDto processDto) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        User processor = userRepository.findById(processerId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더 권한 확인
        if (!joinRequest.getStudyGroup().isLeader(processor)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 가입 신청을 처리할 수 있습니다.");
        }

        // 대기 중인 신청인지 확인
        if (!joinRequest.isPending()) {
            throw new IllegalArgumentException("이미 처리된 가입 신청입니다.");
        }

        if ("APPROVE".equals(processDto.getAction())) {
            // 승인 처리
            joinRequest.approve(processor);

            // 그룹 멤버에 추가
            GroupMember groupMember = GroupMember.builder()
                    .studyGroup(joinRequest.getStudyGroup())
                    .user(joinRequest.getUser())
                    .approvedBy(processor)
                    .build();

            groupMemberRepository.save(groupMember);

        } else if ("REJECT".equals(processDto.getAction())) {
            // 거절 처리
            joinRequest.reject(processor, processDto.getRejectReason());
        } else {
            throw new IllegalArgumentException("잘못된 처리 액션입니다. APPROVE 또는 REJECT만 가능합니다.");
        }

        return JoinRequestResponseDto.from(joinRequest);
    }

    // 특정 그룹의 대기 중인 가입 신청 목록 (리더용)
    public List<JoinRequestSummaryDto> getPendingRequestsByGroup(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더 권한 확인
        if (!studyGroup.isLeader(user)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 가입 신청 목록을 조회할 수 있습니다.");
        }

        List<JoinRequest> pendingRequests = joinRequestRepository.findPendingRequestsByGroupId(groupId);

        return pendingRequests.stream()
                .map(JoinRequestSummaryDto::from)
                .collect(Collectors.toList());
    }

    // 내가 신청한 가입 요청 목록
    public List<JoinRequestSummaryDto> getMyJoinRequests(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        List<JoinRequest> myRequests = joinRequestRepository.findPendingRequestsByUserId(userId);

        return myRequests.stream()
                .map(JoinRequestSummaryDto::from)
                .collect(Collectors.toList());
    }

    // 가입 신청 취소
    @Transactional
    public void cancelJoinRequest(Long requestId, Long userId) {
        JoinRequest joinRequest = joinRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("가입 신청을 찾을 수 없습니다."));

        // 신청자 본인인지 확인
        if (!joinRequest.getUser().getUserId().equals(userId)) {
            throw new IllegalArgumentException("본인이 신청한 요청만 취소할 수 있습니다.");
        }

        // 대기 중인 신청인지 확인
        if (!joinRequest.isPending()) {
            throw new IllegalArgumentException("이미 처리된 가입 신청은 취소할 수 없습니다.");
        }

        joinRequestRepository.delete(joinRequest);
    }
}
