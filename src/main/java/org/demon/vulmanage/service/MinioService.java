package org.demon.vulmanage.service;

import cn.hutool.core.date.DateUtil;
import io.minio.*;
import io.minio.http.Method;
import lombok.extern.slf4j.Slf4j;
import org.demon.vulmanage.config.MinioConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MinioService implements ApplicationRunner {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinioConfig minioConfig;

    /**
     * 应用启动时检查并创建bucket
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        initializeBucket();
    }

    /**
     * 初始化bucket
     */
    private void initializeBucket() {
        try {
            String bucketName = minioConfig.getBucketName();

            // 检查bucket是否存在
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                // 创建bucket
                minioClient.makeBucket(
                        MakeBucketArgs.builder().bucket(bucketName).build()
                );
                log.info("MinIO bucket '{}' created successfully", bucketName);

                // 设置bucket为公共读取权限
                setBucketPublicReadPolicy(bucketName);
            } else {
                log.info("MinIO bucket '{}' already exists", bucketName);
            }
        } catch (Exception e) {
            log.error("Failed to initialize MinIO bucket: {}", e.getMessage(), e);
        }
    }

    /**
     * 设置bucket为公共读取权限
     */
    private void setBucketPublicReadPolicy(String bucketName) {
        try {
            String policy = "{\"Version\":\"2012-10-17\",\"Statement\":[{\"Effect\":\"Allow\",\"Principal\":{\"AWS\":[\"*\"]},\"Action\":[\"s3:GetObject\"],\"Resource\":[\"arn:aws:s3:::" + bucketName + "/*\"]}]}";

            minioClient.setBucketPolicy(
                    SetBucketPolicyArgs.builder()
                            .bucket(bucketName)
                            .config(policy)
                            .build()
            );
            log.info("MinIO bucket '{}' set to public read access", bucketName);
        } catch (Exception e) {
            log.error("Failed to set bucket policy: {}", e.getMessage(), e);
        }
    }

    /**
     * 上传文件到MinIO
     */
    public String uploadFile(MultipartFile file) {
        String bucketName = minioConfig.getBucketName();
        String fileName = generateFileName(file.getOriginalFilename());

        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 返回文件访问URL
            return "/" + minioConfig.getBucketName() + fileName;
        } catch (Exception e) {
            log.error("Failed to upload file: {}", e.getMessage(), e);
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String originalFilename) {
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        return "/" + DateUtil.format(LocalDateTime.now(), "yyyyMMdd") + "/" + UUID.randomUUID() + extension;
    }

    /**
     * 获取文件访问URL
     */
    public String getFileUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .expiry(7, TimeUnit.DAYS)
                            .build()
            );
        } catch (Exception e) {
            log.error("Failed to get file URL: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 获取文件字节数组
     */
    public byte[] getFileBytes(String fileName) {
        try {
            GetObjectResponse response = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(minioConfig.getBucketName())
                            .object(fileName)
                            .build()
            );
            return response.readAllBytes();
        } catch (Exception e) {
            log.error("Failed to get file bytes: {}", e.getMessage(), e);
            return null;
        }
    }
}