package com.scheck.scheck.repository.joinRequest;

import com.scheck.scheck.entity.joinRequest.JoinRequest;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JoinRequestRepository extends JpaRepository<JoinRequest, Long> {

    List<JoinRequest> findByStudyGroupAndStatus(StudyGroup studyGroup, JoinRequest.RequestStatus status);

    List<JoinRequest> findByUserAndStatus(User user, JoinRequest.RequestStatus status);

    Optional<JoinRequest> findByStudyGroupAndUserAndStatus(StudyGroup studyGroup, User user, JoinRequest.RequestStatus status);

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.studyGroup.groupId = :groupId AND jr.status = 'PENDING' ORDER BY jr.requestedAt ASC")
    List<JoinRequest> findPendingRequestsByGroupId(@Param("groupId") Long groupId);

    @Query("SELECT jr FROM JoinRequest jr WHERE jr.user.userId = :userId AND jr.status = 'PENDING' ORDER BY jr.requestedAt DESC")
    List<JoinRequest> findPendingRequestsByUserId(@Param("userId") Long userId);

    boolean existsByStudyGroupAndUserAndStatus(StudyGroup studyGroup, User user, JoinRequest.RequestStatus status);

    // 특정 사용자가 특정 그룹에 대한 모든 요청 (상태 무관)
    List<JoinRequest> findByStudyGroupAndUserOrderByRequestedAtDesc(StudyGroup studyGroup, User user);
}
