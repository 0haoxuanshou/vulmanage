package org.demon.vulmanage.controller;

import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.exception.BusinessException;
import org.demon.vulmanage.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private MinioService minioService;

    /**
     * 上传图片文件
     */
    @PostMapping("/upload/image")
    public ResponseEntity<Map<String, Object>> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> response = new HashMap<>();
        
        // 验证文件类型
        if (!isImageFile(file)) {
            throw new BusinessException(400, "只支持图片文件上传");
        }
        
        // 验证文件大小（限制为5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new BusinessException(400, "文件大小不能超过5MB");
        }
        
        // 上传文件
        String fileUrl = minioService.uploadFile(file);
        
        response.put("success", true);
        response.put("message", "文件上传成功");
        response.put("url", fileUrl);
        
        log.info("Image uploaded successfully: {}", fileUrl);
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证是否为图片文件
     */
    private boolean isImageFile(MultipartFile file) {
        String contentType = file.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
}