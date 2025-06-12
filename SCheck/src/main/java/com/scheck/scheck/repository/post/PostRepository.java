package com.scheck.scheck.repository.post;

import com.scheck.scheck.entity.post.Post;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // 특정 그룹의 게시글 목록 (페이징)
    Page<Post> findByStudyGroupOrderByCreatedAtDesc(StudyGroup studyGroup, Pageable pageable);

    // 특정 그룹의 게시글 목록 (그룹 ID로)
    @Query("SELECT p FROM Post p WHERE p.studyGroup.groupId = :groupId ORDER BY p.createdAt DESC")
    Page<Post> findByGroupIdOrderByCreatedAtDesc(@Param("groupId") Long groupId, Pageable pageable);

    // 특정 사용자가 작성한 게시글 목록
    Page<Post> findByAuthorOrderByCreatedAtDesc(User author, Pageable pageable);

    // 특정 그룹에서 특정 사용자가 작성한 게시글
    Page<Post> findByStudyGroupAndAuthorOrderByCreatedAtDesc(StudyGroup studyGroup, User author, Pageable pageable);

    // 제목이나 내용으로 검색 (특정 그룹 내)
    @Query("SELECT p FROM Post p WHERE p.studyGroup.groupId = :groupId AND " +
            "(p.title LIKE %:keyword% OR p.content LIKE %:keyword%) ORDER BY p.createdAt DESC")
    Page<Post> searchInGroup(@Param("groupId") Long groupId, @Param("keyword") String keyword, Pageable pageable);

    // 최근 게시글 조회 (특정 그룹)
    @Query("SELECT p FROM Post p WHERE p.studyGroup.groupId = :groupId AND p.createdAt >= :since ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(@Param("groupId") Long groupId, @Param("since") LocalDateTime since);

    // 특정 그룹의 게시글 수
    Long countByStudyGroup(StudyGroup studyGroup);

    // 내가 속한 그룹들의 최신 게시글 조회
    @Query("SELECT p FROM Post p " +
            "JOIN GroupMember gm ON p.studyGroup.groupId = gm.studyGroup.groupId " +
            "WHERE gm.user.userId = :userId AND gm.status = 'ACTIVE' " +
            "ORDER BY p.createdAt DESC")
    Page<Post> findMyGroupsPosts(@Param("userId") Long userId, Pageable pageable);
}