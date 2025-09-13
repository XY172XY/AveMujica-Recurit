package avemujica.entrance.configuration;

import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MinioConfiguration {
    @Value("${minio.endpoint}")
    String endpoint;
    @Value("${minio.access-key}")
    String username;
    @Value("${minio.secret-key}")
    String password;
    @Value("${minio.bucket}")
    String bucket;

    @Bean
    public MinioClient minioClient(){
        log.info("Init minio client...");
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(username, password)
                .build();
    }
}
