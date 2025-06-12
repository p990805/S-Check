package com.scheck.scheck.repository.post;

import com.scheck.scheck.entity.post.Post;
import com.scheck.scheck.entity.post.PostFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostFileRepository extends JpaRepository<PostFile, Long> {

    List<PostFile> findByPostOrderByUploadedAtAsc(Post post);

    List<PostFile> findByPostPostIdOrderByUploadedAtAsc(Long postId);

    // 특정 그룹의 모든 첨부파일 조회
    @Query("SELECT pf FROM PostFile pf WHERE pf.post.studyGroup.groupId = :groupId ORDER BY pf.uploadedAt DESC")
    List<PostFile> findByGroupId(@Param("groupId") Long groupId);

    // 파일 크기 합계 조회 (특정 그룹)
    @Query("SELECT COALESCE(SUM(pf.fileSize), 0) FROM PostFile pf WHERE pf.post.studyGroup.groupId = :groupId")
    Long getTotalFileSizeByGroupId(@Param("groupId") Long groupId);

    // 파일 개수 조회 (특정 그룹)
    @Query("SELECT COUNT(pf) FROM PostFile pf WHERE pf.post.studyGroup.groupId = :groupId")
    Long getFileCountByGroupId(@Param("groupId") Long groupId);
}