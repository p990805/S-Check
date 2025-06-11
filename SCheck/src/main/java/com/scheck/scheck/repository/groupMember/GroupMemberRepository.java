package com.scheck.scheck.repository.groupMember;

import com.scheck.scheck.entity.groupMember.GroupMember;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByStudyGroupAndStatus(StudyGroup studyGroup, GroupMember.MemberStatus status);

    List<GroupMember> findByUserAndStatus(User user, GroupMember.MemberStatus status);

    Optional<GroupMember> findByStudyGroupAndUser(StudyGroup studyGroup, User user);

    Optional<GroupMember> findByStudyGroupAndUserAndStatus(StudyGroup studyGroup, User user, GroupMember.MemberStatus status);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.studyGroup.groupId = :groupId AND gm.status = 'ACTIVE' ORDER BY gm.appliedAt ASC")
    List<GroupMember> findActiveMembers(@Param("groupId") Long groupId);

    @Query("SELECT gm FROM GroupMember gm WHERE gm.user.userId = :userId AND gm.status = 'ACTIVE'")
    List<GroupMember> findActiveGroupsByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(gm) FROM GroupMember gm WHERE gm.studyGroup.groupId = :groupId AND gm.status = 'ACTIVE'")
    Integer countActiveMembers(@Param("groupId") Long groupId);

    boolean existsByStudyGroupAndUserAndStatus(StudyGroup studyGroup, User user, GroupMember.MemberStatus status);

    // 사용자가 특정 그룹의 활성 멤버인지 확인
    @Query("SELECT CASE WHEN COUNT(gm) > 0 THEN true ELSE false END FROM GroupMember gm " +
            "WHERE gm.studyGroup.groupId = :groupId AND gm.user.userId = :userId AND gm.status = 'ACTIVE'")
    boolean isActiveMember(@Param("groupId") Long groupId, @Param("userId") Long userId);
}