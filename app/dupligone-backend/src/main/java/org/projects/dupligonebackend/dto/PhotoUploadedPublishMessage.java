package org.projects.dupligonebackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PhotoUploadedPublishMessage {

    private UUID photoId;
    private String filePath;

}
