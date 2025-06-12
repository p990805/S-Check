package com.scheck.scheck.dto.post;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GroupFileStatsDto {
    private Long groupId;
    private String groupName;
    private Long totalFileCount;
    private Long totalFileSize;
    private String formattedTotalSize;

    public static GroupFileStatsDto of(Long groupId, String groupName, Long fileCount, Long totalSize) {
        String formattedSize = formatFileSize(totalSize);

        return GroupFileStatsDto.builder()
                .groupId(groupId)
                .groupName(groupName)
                .totalFileCount(fileCount)
                .totalFileSize(totalSize)
                .formattedTotalSize(formattedSize)
                .build();
    }

    private static String formatFileSize(Long size) {
        if (size < 1024) {
            return size + " B";
        } else if (size < 1024 * 1024) {
            return String.format("%.1f KB", size / 1024.0);
        } else if (size < 1024 * 1024 * 1024) {
            return String.format("%.1f MB", size / (1024.0 * 1024.0));
        } else {
            return String.format("%.1f GB", size / (1024.0 * 1024.0 * 1024.0));
        }
    }
}