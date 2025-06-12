package com.scheck.scheck.controller.post;

import org.springframework.core.io.Resource;
import com.scheck.scheck.entity.post.PostFile;
import com.scheck.scheck.service.post.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Paths;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
public class FileController {

    private final PostService postService;

    // 파일 다운로드
    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(
            @PathVariable Long fileId,
            @RequestHeader("User-Id") Long userId) {

        PostFile postFile = postService.getFileForDownload(fileId, userId);

        Resource resource = new FileSystemResource(Paths.get(postFile.getFilePath()));

        if (!resource.exists()) {
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + postFile.getOriginalFilename() + "\"")
                .body(resource);
    }
}
