package org.projects.dupligonebackend.dto;

import java.util.UUID;

public record PhotoResponseDto(
        UUID id,
        String filePath
) {
}
