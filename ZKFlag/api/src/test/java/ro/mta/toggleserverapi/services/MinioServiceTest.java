package ro.mta.toggleserverapi.services;

import io.minio.MinioClient;
import io.minio.ObjectWriteResponse;
import io.minio.PutObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    @Captor
    private ArgumentCaptor<PutObjectArgs> putObjectArgsCaptor;

    private MinioService minioService;

    @BeforeEach
    void setup() {
        minioService = new MinioService(minioClient); // mock injectat manual
    }

    @Test
    void uploadFile_shouldCallMinioClientAndReturnUrl() throws Exception {
        // arrange
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "some content".getBytes()
        );

        when(minioClient.putObject(Mockito.any(PutObjectArgs.class)))
                .thenReturn(Mockito.mock(ObjectWriteResponse.class));

        // act
        String result = minioService.uploadFile(file);

        // assert
        assertTrue(result.contains("http://localhost:9000/public-bucket/")); //url ul este corect

        verify(minioClient).putObject(putObjectArgsCaptor.capture());
        assertEquals("public-bucket", putObjectArgsCaptor.getValue().bucket());
    }
}
