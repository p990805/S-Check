package com.scheck.scheck.service.joinRequest;

import com.scheck.scheck.dto.groupMember.GroupMemberResponseDto;
import com.scheck.scheck.dto.groupMember.KickMemberRequestDto;
import com.scheck.scheck.entity.groupMember.GroupMember;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.groupMember.GroupMemberRepository;
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
public class GroupMemberService {

    private final GroupMemberRepository groupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    // 그룹 멤버 목록 조회
    public List<GroupMemberResponseDto> getGroupMembers(Long groupId) {
        List<GroupMember> members = groupMemberRepository.findActiveMembers(groupId);

        return members.stream()
                .map(GroupMemberResponseDto::from)
                .collect(Collectors.toList());
    }

    // 멤버 강퇴 (리더만 가능)
    @Transactional
    public void kickMember(Long groupId, Long targetUserId, Long leaderId, KickMemberRequestDto requestDto) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User leader = userRepository.findById(leaderId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new IllegalArgumentException("강퇴할 사용자를 찾을 수 없습니다."));

        // 리더 권한 확인
        if (!studyGroup.isLeader(leader)) {
            throw new IllegalArgumentException("스터디 그룹 리더만 멤버를 강퇴할 수 있습니다.");
        }

        // 자기 자신은 강퇴할 수 없음
        if (leaderId.equals(targetUserId)) {
            throw new IllegalArgumentException("리더는 자기 자신을 강퇴할 수 없습니다.");
        }

        GroupMember groupMember = groupMemberRepository.findByStudyGroupAndUserAndStatus(
                        studyGroup, targetUser, GroupMember.MemberStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자는 그룹의 활성 멤버가 아닙니다."));

        groupMember.kickOut(requestDto.getReason());
    }

    // 그룹 탈퇴
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 리더는 탈퇴할 수 없음
        if (studyGroup.isLeader(user)) {
            throw new IllegalArgumentException("리더는 그룹을 탈퇴할 수 없습니다. 그룹을 삭제하거나 리더를 변경해주세요.");
        }

        GroupMember groupMember = groupMemberRepository.findByStudyGroupAndUserAndStatus(
                        studyGroup, user, GroupMember.MemberStatus.ACTIVE)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹의 활성 멤버가 아닙니다."));

        groupMember.leave();
    }

    // 그룹 멤버 수 조회
    public Integer getGroupMemberCount(Long groupId) {
        return groupMemberRepository.countActiveMembers(groupId);
    }
}