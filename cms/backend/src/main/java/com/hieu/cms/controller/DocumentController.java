package com.hieu.cms.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/documents")
public class DocumentController {

    @PostMapping("/upload")
    public List<String> upload(@RequestParam("file") MultipartFile[] files) {
        // Mock upload logic
        return List.of("https://minio.local/bucket/doc-" + UUID.randomUUID() + ".pdf");
    }
}
