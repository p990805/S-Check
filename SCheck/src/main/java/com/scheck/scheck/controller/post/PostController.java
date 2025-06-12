package com.scheck.scheck.controller.post;

import com.scheck.scheck.dto.post.*;
import com.scheck.scheck.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping("/groups/{groupId}")
    public ResponseEntity<PostResponseDto> createPost(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody PostCreateRequestDto requestDto) {
        PostResponseDto response = postService.createPost(groupId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 게시글 상세 조회
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getPost(
            @PathVariable Long postId,
            @RequestHeader("User-Id") Long userId) {
        PostResponseDto response = postService.getPost(postId, userId);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(
            @PathVariable Long postId,
            @RequestHeader("User-Id") Long userId,
            @RequestBody PostUpdateRequestDto requestDto) {
        PostResponseDto response = postService.updatePost(postId, userId, requestDto);
        return ResponseEntity.ok(response);
    }

    // 게시글 삭제
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            @RequestHeader("User-Id") Long userId) {
        postService.deletePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    // 특정 그룹의 게시글 목록 조회
    @GetMapping("/groups/{groupId}")
    public ResponseEntity<Page<PostSummaryDto>> getGroupPosts(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryDto> response = postService.getGroupPosts(groupId, userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 특정 그룹에서 게시글 검색
    @GetMapping("/groups/{groupId}/search")
    public ResponseEntity<Page<PostSummaryDto>> searchGroupPosts(
            @PathVariable Long groupId,
            @RequestParam String keyword,
            @RequestHeader("User-Id") Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryDto> response = postService.searchGroupPosts(groupId, keyword, userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 내가 속한 그룹들의 최신 게시글
    @GetMapping("/my-groups")
    public ResponseEntity<Page<PostSummaryDto>> getMyGroupsPosts(
            @RequestHeader("User-Id") Long userId,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<PostSummaryDto> response = postService.getMyGroupsPosts(userId, pageable);
        return ResponseEntity.ok(response);
    }

    // 파일 업로드
    @PostMapping("/{postId}/files")
    public ResponseEntity<FileUploadResponseDto> uploadFile(
            @PathVariable Long postId,
            @RequestParam("file") MultipartFile file,
            @RequestHeader("User-Id") Long userId) throws IOException {
        FileUploadResponseDto response = postService.uploadFile(postId, file, userId);
        return ResponseEntity.ok(response);
    }

    // 파일 삭제
    @DeleteMapping("/files/{fileId}")
    public ResponseEntity<Void> deleteFile(
            @PathVariable Long fileId,
            @RequestHeader("User-Id") Long userId) {
        postService.deleteFile(fileId, userId);
        return ResponseEntity.ok().build();
    }

    // 그룹 파일 통계 조회
    @GetMapping("/groups/{groupId}/file-stats")
    public ResponseEntity<GroupFileStatsDto> getGroupFileStats(
            @PathVariable Long groupId,
            @RequestHeader("User-Id") Long userId) {
        GroupFileStatsDto response = postService.getGroupFileStats(groupId, userId);
        return ResponseEntity.ok(response);
    }
}