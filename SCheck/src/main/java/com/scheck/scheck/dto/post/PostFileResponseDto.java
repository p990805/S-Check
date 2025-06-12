package com.scheck.scheck.dto.post;

import com.scheck.scheck.entity.post.PostFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostFileResponseDto {
    private Long fileId;
    private String originalFilename;
    private String storedFilename;
    private String filePath;
    private Long fileSize;
    private String formattedFileSize;
    private LocalDateTime uploadedAt;

    public static PostFileResponseDto from(PostFile postFile) {
        return PostFileResponseDto.builder()
                .fileId(postFile.getFileId())
                .originalFilename(postFile.getOriginalFilename())
                .storedFilename(postFile.getStoredFilename())
                .filePath(postFile.getFilePath())
                .fileSize(postFile.getFileSize())
                .formattedFileSize(postFile.getFormattedFileSize())
                .uploadedAt(postFile.getUploadedAt())
                .build();
    }
}
