package com.scheck.scheck.dto.post;

import com.scheck.scheck.entity.post.PostFile;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class FileUploadResponseDto {
    private Long fileId;
    private String originalFilename;
    private String storedFilename;
    private String downloadUrl;
    private Long fileSize;
    private String formattedFileSize;
    private LocalDateTime uploadedAt;

    public static FileUploadResponseDto from(PostFile postFile, String downloadUrl) {
        return FileUploadResponseDto.builder()
                .fileId(postFile.getFileId())
                .originalFilename(postFile.getOriginalFilename())
                .storedFilename(postFile.getStoredFilename())
                .downloadUrl(downloadUrl)
                .fileSize(postFile.getFileSize())
                .formattedFileSize(postFile.getFormattedFileSize())
                .uploadedAt(postFile.getUploadedAt())
                .build();
    }
}