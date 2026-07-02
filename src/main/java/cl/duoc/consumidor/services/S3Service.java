package cl.duoc.consumidor.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3Service {

    private final S3Client s3Client;
    private final String bucket;

    public S3Service(
            @Value("${aws.region}") String region,
            @Value("${aws.s3.bucket}") String bucket) {
        this.bucket = bucket;
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(DefaultCredentialsProvider.create())
                .build();
    }

    /**
     * Sube el resumen de compra a S3.
     *
     * @param inscripcionId ID de la inscripcion (carpeta en S3)
     * @param contenido     bytes del archivo de resumen
     * @return clave (key) del objeto en S3
     */
    public String subirResumen(Long inscripcionId, byte[] contenido) {
        String key = inscripcionId + "/resumen-compra-" + inscripcionId + ".txt";

        s3Client.putObject(
                PutObjectRequest.builder()
                        .bucket(bucket)
                        .key(key)
                        .contentType("text/plain")
                        .build(),
                RequestBody.fromBytes(contenido)
        );

        return key;
    }
}
