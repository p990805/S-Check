package com.scheck.scheck.dto.post;

import com.scheck.scheck.dto.user.UserResponseDto;
import com.scheck.scheck.entity.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class PostResponseDto {
    private Long postId;
    private Long groupId;
    private String groupName;
    private UserResponseDto author;
    private String title;
    private String content;
    private List<PostFileResponseDto> files;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponseDto from(Post post) {
        return PostResponseDto.builder()
                .postId(post.getPostId())
                .groupId(post.getStudyGroup().getGroupId())
                .groupName(post.getStudyGroup().getGroupName())
                .author(UserResponseDto.from(post.getAuthor()))
                .title(post.getTitle())
                .content(post.getContent())
                .files(post.getFiles().stream()
                        .map(PostFileResponseDto::from)
                        .collect(Collectors.toList()))
                .createdAt(post.getCreatedAt())
                .updatedAt(post.getUpdatedAt())
                .build();
    }
}