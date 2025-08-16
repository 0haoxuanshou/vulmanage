package org.demon.vulmanage.controller;

import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.config.MinioConfig;
import org.demon.vulmanage.exception.BusinessException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.io.InputStream;

@Slf4j
@RestController
@CrossOrigin(origins = "*")
public class FileAccessController {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 访问MinIO中的文件
     * 路径格式: /{bucketName}/{fileName}
     */
    @GetMapping("/{bucketName}/**")
    public ResponseEntity<byte[]> getFile(@PathVariable String bucketName, 
                                         @RequestParam(required = false) String download,
                                         HttpServletRequest request) {
        try {
            // 从请求路径中提取文件名
            String requestURI = request.getRequestURI();
            String fileName = requestURI.substring(requestURI.indexOf(bucketName) + bucketName.length() + 1);
            
            log.info("Accessing file: bucket={}, fileName={}", bucketName, fileName);
            
            // 从MinIO获取文件
            InputStream inputStream = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
            
            // 读取文件内容
            byte[] content = inputStream.readAllBytes();
            inputStream.close();
            
            // 设置响应头
            HttpHeaders headers = new HttpHeaders();
            
            // 根据文件扩展名设置Content-Type
            String contentType = getContentType(fileName);
            headers.setContentType(MediaType.parseMediaType(contentType));
            
            // 如果是下载请求，设置下载头
            if ("true".equals(download)) {
                headers.setContentDispositionFormData("attachment", fileName);
            }
            
            return ResponseEntity.ok()
                    .headers(headers)
                    .body(content);
                    
        } catch (Exception e) {
            log.error("Failed to get file: bucket={}, error={}", bucketName, e.getMessage(), e);
            throw new BusinessException(404, "文件不存在或无法访问: " + e.getMessage());
        }
    }
    
    /**
     * 根据文件扩展名获取Content-Type
     */
    private String getContentType(String fileName) {
        if (fileName == null) {
            return "application/octet-stream";
        }
        
        String extension = "";
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            extension = fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        
        return switch (extension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "webp" -> "image/webp";
            case "svg" -> "image/svg+xml";
            case "pdf" -> "application/pdf";
            case "txt" -> "text/plain";
            case "html" -> "text/html";
            case "css" -> "text/css";
            case "js" -> "application/javascript";
            case "json" -> "application/json";
            case "xml" -> "application/xml";
            default -> "application/octet-stream";
        };
    }
}