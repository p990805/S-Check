package com.scheck.scheck.dto.post;

import com.scheck.scheck.entity.post.Post;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostSummaryDto {
    private Long postId;
    private String title;
    private String authorNickname;
    private Integer fileCount;
    private LocalDateTime createdAt;

    public static PostSummaryDto from(Post post) {
        return PostSummaryDto.builder()
                .postId(post.getPostId())
                .title(post.getTitle())
                .authorNickname(post.getAuthor().getNickname())
                .fileCount(post.getFiles().size())
                .createdAt(post.getCreatedAt())
                .build();
    }
}