package org.projects.dupligonebackend.dto;

import java.util.List;
import java.util.UUID;

public record ClusterResponseDto(
        UUID clusterId,
        UUID bestPhotoId,
        List<PhotoResponseDto> photos
) {
}
