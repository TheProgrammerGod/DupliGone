package org.projects.dupligonebackend.service;

import org.projects.dupligonebackend.dto.PhotoUploadResponse;
import org.projects.dupligonebackend.model.Photo;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface PhotoService {

    List<Photo> getPhotosForCluster(UUID clusterId, UUID sessionId);

    List<PhotoUploadResponse> saveUploadedPhotos(List<MultipartFile> files, UUID sessionId);

}
