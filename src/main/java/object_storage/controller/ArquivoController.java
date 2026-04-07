package object_storage.controller;

import lombok.RequiredArgsConstructor;
import object_storage.dto.ArquivoResponseDTO;
import object_storage.service.ArquivoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}