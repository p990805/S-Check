package com.scheck.scheck.repository.studyGroup;

import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import org.springframework.stereotype.Repository;

@Repository
public interface StudyGroupRepository extends JpaRepository<StudyGroup, Long> {
    List<StudyGroup> findByLeader(User leader);

    List<StudyGroup> findByLeaderUserId(Long leaderId);

    Page<StudyGroup> findByGroupNameContaining(String groupName, Pageable pageable);

    @Query("SELECT sg FROM StudyGroup sg WHERE sg.groupName LIKE %:keyword% OR sg.description LIKE %:keyword%")
    Page<StudyGroup> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);

    // 특정 사용자가 속한 스터디 그룹 조회
    @Query("SELECT sg FROM StudyGroup sg " +
            "JOIN GroupMember gm ON sg.groupId = gm.studyGroup.groupId " +
            "WHERE gm.user.userId = :userId AND gm.status = 'ACTIVE'")
    List<StudyGroup> findStudyGroupsByUserId(@Param("userId") Long userId);

    boolean existsByGroupName(String groupName);


}
