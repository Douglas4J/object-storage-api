package object_storage.service;

import lombok.RequiredArgsConstructor;
import object_storage.dto.ArquivoResponseDTO;
import object_storage.model.Arquivo;
import object_storage.repository.ArquivoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ArquivoService {

    private final S3Client s3Client;
    private final ArquivoRepository arquivoRepository;

    @Value("${aws.s3.bucket}")
    private String bucket;

    @Value("${aws.s3.endpoint}")
    private String endpoint;

    public ArquivoResponseDTO enviar(MultipartFile file) {

        try {
            String key = generateKey(file.getOriginalFilename());

            PutObjectRequest request = PutObjectRequest.builder()
                    .bucket(bucket)
                    .key(key)
                    .contentType(file.getContentType())
                    .build();

            s3Client.putObject(
                    request,
                    RequestBody.fromBytes(file.getBytes())
            );

            Arquivo arquivo = Arquivo.builder()
                    .fileName(file.getOriginalFilename())
                    .contentType(file.getContentType())
                    .key(key)
                    .bucket(bucket)
                    .size(file.getSize())
                    .build();

            Arquivo salvar = arquivoRepository.save(arquivo);

            return ArquivoResponseDTO.builder()
                    .id(salvar.getId())
                    .fileName(salvar.getFileName())
                    .url(buildUrl(key))
                    .build();

        } catch (Exception e) {
            throw new RuntimeException("Erro ao fazer upload", e);
        }
    }

    private String generateKey(String fileName) {
        return "batatas/" + UUID.randomUUID() + "-" + fileName;
    }

    private String buildUrl(String key) {
        return endpoint + "/" + bucket + "/" + key;
    }

    public void delete(Long id) {

        Arquivo arquivo = arquivoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Arquivo não encontrado"));

        // Remove do Garage (S3)
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
                .bucket(arquivo.getBucket())
                .key(arquivo.getKey())
                .build();

        s3Client.deleteObject(deleteRequest);

        // Remove do banco
        arquivoRepository.delete(arquivo);
    }
}