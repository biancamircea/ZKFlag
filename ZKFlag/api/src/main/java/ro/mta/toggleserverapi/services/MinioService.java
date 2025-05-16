package ro.mta.toggleserverapi.services;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class MinioService {

    private final MinioClient minioClient;
    private final String bucketName = "public-bucket";

    public MinioService() {
        this.minioClient = MinioClient.builder()
                .endpoint("http://minio:9000")
                .credentials("admin", "admin123")
                .build();
    }

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }


    public String uploadFile(MultipartFile file) throws Exception {
        String fileName = UUID.randomUUID() + "-" + file.getOriginalFilename();

        minioClient.putObject(
                PutObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .stream(file.getInputStream(), file.getSize(), -1)
                        .contentType(file.getContentType())
                        .build()
        );

        return "http://localhost:9000/" + bucketName + "/" + fileName;
    }

    public void deleteFile(String fileName) throws Exception {
        minioClient.removeObject(
                RemoveObjectArgs.builder()
                        .bucket(bucketName)
                        .object(fileName)
                        .build()
        );
    }
}

