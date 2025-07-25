package org.projects.dupligonebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PhotoUploadResponse {
    private UUID photoId;
    private String originalFilename;
}
