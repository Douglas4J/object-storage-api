package object_storage.dto;

import lombok.Builder;

@Builder
public record ArquivoResponseDTO(
        Long id,
        String fileName,
        String url) {
}
