package object_storage.controller;

import lombok.RequiredArgsConstructor;
import object_storage.dto.ArquivoResponseDTO;
import object_storage.service.ArquivoService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

@RestController
@RequestMapping("/arquivos")
@RequiredArgsConstructor
public class ArquivoController {

    private final ArquivoService service;

    @PostMapping("/enviar")
    public ResponseEntity<ArquivoResponseDTO> enviar(
            @RequestParam("file") MultipartFile file
    ) {
        return ResponseEntity.ok(service.enviar(file));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable Long id) {

        ResponseInputStream<GetObjectResponse> s3Object = service.download(id);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"arquivo\"")
                .contentType(MediaType.parseMediaType(s3Object.response().contentType()))
                .body(new InputStreamResource(s3Object));
    }
}