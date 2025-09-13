package avemujica.question.service.impl;

import avemujica.common.utils.Const;
import avemujica.question.service.FileDownloadService;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MinioClient;
import io.minio.errors.*;
import io.minio.http.Method;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class FileDownloadServiceImpl implements FileDownloadService {
    @Resource
    MinioClient minioClient;

    @Value("${minio.presign-expire-seconds}")
    Integer expireSeconds;

    @Override
    public String getDownloadUrl(String fileName) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(Const.BUCKET)
                            .object(fileName)
                            .expiry(expireSeconds) // MinIO/S3 通常有上限(如7天)
                            .build()
            );
        } catch (Exception e) {
            log.error("下载文件[{}]时出错：{}",fileName,e.getMessage());
            return null;
        }
    }
}
