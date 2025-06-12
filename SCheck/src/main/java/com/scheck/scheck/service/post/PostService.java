package com.scheck.scheck.service.post;

import com.scheck.scheck.dto.post.*;
import com.scheck.scheck.entity.post.Post;
import com.scheck.scheck.entity.post.PostFile;
import com.scheck.scheck.entity.studyGroup.StudyGroup;
import com.scheck.scheck.entity.user.User;
import com.scheck.scheck.repository.groupMember.GroupMemberRepository;
import com.scheck.scheck.repository.post.PostFileRepository;
import com.scheck.scheck.repository.post.PostRepository;
import com.scheck.scheck.repository.studyGroup.StudyGroupRepository;
import com.scheck.scheck.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final PostFileRepository postFileRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    private final GroupMemberRepository groupMemberRepository;

    @Value("${app.file.upload-dir:/uploads}")
    private String uploadDir;

    @Value("${app.file.max-size:10485760}") // 10MB
    private long maxFileSize;

    // 게시글 생성
    @Transactional
    public PostResponseDto createPost(Long groupId, Long authorId, PostCreateRequestDto requestDto) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(groupId, authorId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 게시글을 작성할 수 있습니다.");
        }

        Post post = Post.builder()
                .studyGroup(studyGroup)
                .author(author)
                .title(requestDto.getTitle())
                .content(requestDto.getContent())
                .build();

        Post savedPost = postRepository.save(post);
        return PostResponseDto.from(savedPost);
    }

    // 게시글 조회
    public PostResponseDto getPost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(post.getStudyGroup().getGroupId(), userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 게시글을 조회할 수 있습니다.");
        }

        return PostResponseDto.from(post);
    }

    // 게시글 수정
    @Transactional
    public PostResponseDto updatePost(Long postId, Long userId, PostUpdateRequestDto requestDto) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 작성자 본인인지 확인
        if (!post.isAuthor(user)) {
            throw new IllegalArgumentException("게시글 작성자만 수정할 수 있습니다.");
        }

        post.updatePost(requestDto.getTitle(), requestDto.getContent());
        return PostResponseDto.from(post);
    }

    // 게시글 삭제
    @Transactional
    public void deletePost(Long postId, Long userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 작성자 본인이거나 그룹 리더인지 확인
        if (!post.isAuthor(user) && !post.getStudyGroup().isLeader(user)) {
            throw new IllegalArgumentException("게시글 작성자 또는 그룹 리더만 삭제할 수 있습니다.");
        }

        // 첨부파일들도 물리적으로 삭제
        for (PostFile file : post.getFiles()) {
            deletePhysicalFile(file.getFilePath());
        }

        postRepository.delete(post);
    }

    // 특정 그룹의 게시글 목록 조회
    public Page<PostSummaryDto> getGroupPosts(Long groupId, Long userId, Pageable pageable) {
        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 게시글 목록을 조회할 수 있습니다.");
        }

        Page<Post> posts = postRepository.findByGroupIdOrderByCreatedAtDesc(groupId, pageable);
        return posts.map(PostSummaryDto::from);
    }

    // 특정 그룹에서 게시글 검색
    public Page<PostSummaryDto> searchGroupPosts(Long groupId, String keyword, Long userId, Pageable pageable) {
        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 게시글을 검색할 수 있습니다.");
        }

        Page<Post> posts = postRepository.searchInGroup(groupId, keyword, pageable);
        return posts.map(PostSummaryDto::from);
    }

    // 내가 속한 그룹들의 최신 게시글
    public Page<PostSummaryDto> getMyGroupsPosts(Long userId, Pageable pageable) {
        Page<Post> posts = postRepository.findMyGroupsPosts(userId, pageable);
        return posts.map(PostSummaryDto::from);
    }

    // 파일 업로드
    @Transactional
    public FileUploadResponseDto uploadFile(Long postId, MultipartFile file, Long userId) throws IOException {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("게시글을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 작성자 본인인지 확인
        if (!post.isAuthor(user)) {
            throw new IllegalArgumentException("게시글 작성자만 파일을 업로드할 수 있습니다.");
        }

        // 파일 크기 체크
        if (file.getSize() > maxFileSize) {
            throw new IllegalArgumentException("파일 크기가 제한을 초과했습니다. (최대 " + (maxFileSize / 1024 / 1024) + "MB)");
        }

        // 파일 저장
        String storedFilename = generateStoredFilename(file.getOriginalFilename());
        String filePath = saveFile(file, storedFilename);

        PostFile postFile = PostFile.builder()
                .originalFilename(file.getOriginalFilename())
                .storedFilename(storedFilename)
                .filePath(filePath)
                .fileSize(file.getSize())
                .build();

        post.addFile(postFile);
        PostFile savedFile = postFileRepository.save(postFile);

        String downloadUrl = "/api/files/download/" + savedFile.getFileId();
        return FileUploadResponseDto.from(savedFile, downloadUrl);
    }

    // 파일 삭제
    @Transactional
    public void deleteFile(Long fileId, Long userId) {
        PostFile postFile = postFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));

        // 작성자 본인이거나 그룹 리더인지 확인
        if (!postFile.getPost().isAuthor(user) && !postFile.getPost().getStudyGroup().isLeader(user)) {
            throw new IllegalArgumentException("파일 작성자 또는 그룹 리더만 삭제할 수 있습니다.");
        }

        // 물리적 파일 삭제
        deletePhysicalFile(postFile.getFilePath());

        // 연관관계 제거 및 삭제
        postFile.getPost().removeFile(postFile);
        postFileRepository.delete(postFile);
    }

    // 그룹 파일 통계 조회
    public GroupFileStatsDto getGroupFileStats(Long groupId, Long userId) {
        StudyGroup studyGroup = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("스터디 그룹을 찾을 수 없습니다."));

        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(groupId, userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 파일 통계를 조회할 수 있습니다.");
        }

        Long fileCount = postFileRepository.getFileCountByGroupId(groupId);
        Long totalSize = postFileRepository.getTotalFileSizeByGroupId(groupId);

        return GroupFileStatsDto.of(groupId, studyGroup.getGroupName(), fileCount, totalSize);
    }

    // 파일 다운로드 정보 조회
    public PostFile getFileForDownload(Long fileId, Long userId) {
        PostFile postFile = postFileRepository.findById(fileId)
                .orElseThrow(() -> new IllegalArgumentException("파일을 찾을 수 없습니다."));

        // 그룹 멤버인지 확인
        if (!groupMemberRepository.isActiveMember(postFile.getPost().getStudyGroup().getGroupId(), userId)) {
            throw new IllegalArgumentException("해당 그룹의 활성 멤버만 파일을 다운로드할 수 있습니다.");
        }

        return postFile;
    }

    // 헬퍼 메서드들
    private String generateStoredFilename(String originalFilename) {
        String extension = "";
        int lastDotIndex = originalFilename.lastIndexOf(".");
        if (lastDotIndex > 0) {
            extension = originalFilename.substring(lastDotIndex);
        }

        return UUID.randomUUID().toString() + "_" +
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) +
                extension;
    }

    private String saveFile(MultipartFile file, String storedFilename) throws IOException {
        // 업로드 디렉토리 생성
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 날짜별 하위 디렉토리 생성 (예: /uploads/2025/01/15/)
        String dateDir = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        Path datePath = uploadPath.resolve(dateDir);
        if (!Files.exists(datePath)) {
            Files.createDirectories(datePath);
        }

        // 파일 저장
        Path filePath = datePath.resolve(storedFilename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return filePath.toString();
    }

    private void deletePhysicalFile(String filePath) {
        try {
            Path path = Paths.get(filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            // 로그 남기고 계속 진행 (파일 삭제 실패가 전체 트랜잭션을 롤백시키지 않도록)
            System.err.println("파일 삭제 실패: " + filePath + ", 오류: " + e.getMessage());
        }
    }
}
