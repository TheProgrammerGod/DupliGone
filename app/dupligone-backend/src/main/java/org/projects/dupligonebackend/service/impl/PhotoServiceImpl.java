package org.projects.dupligonebackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.dto.PhotoUploadResponse;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.PhotoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    @Value("${photo.storage.base-dir}")
    private Path baseDir;
    private final PhotoRepository photoRepository;
    private UUID sessionId;

    public PhotoServiceImpl(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Photo> getPhotosForCluster(UUID clusterId) {
        sessionId = SessionContextHolder.getSessionId();
        List<Photo> photos = photoRepository.findBySessionIdAndClusterId(sessionId, clusterId);
        if(photos.isEmpty()){
            throw new EntityNotFoundException("No photos found for this cluster in this session");
        }
        return photos;
    }

    @Override
    public List<PhotoUploadResponse> saveUploadedPhotos(List<MultipartFile> files) {
        sessionId = SessionContextHolder.getSessionId();

        List<PhotoUploadResponse> uploadResponses = new ArrayList<>();

        for(MultipartFile file : files){
            try {
                if (file.isEmpty())
                    continue;

                String contentType = file.getContentType();
                if (contentType == null || !contentType.startsWith("image/"))
                    continue; //skip

                UUID photoID = UUID.randomUUID();
                String extension = Objects.requireNonNull(file.getOriginalFilename())
                        .substring(file.getOriginalFilename().lastIndexOf("."));
                String relativePath = sessionId + "/" + photoID + extension;
                Path fullPath = baseDir.resolve(relativePath);

                Files.createDirectories(fullPath.getParent());
                file.transferTo(fullPath.toFile());

                Photo photo = new Photo();
                photo.setId(photoID);
                photo.setSessionId(sessionId);
                photo.setFilePath(relativePath); // save only relative path
                photoRepository.save(photo);

                PhotoUploadResponse response = new PhotoUploadResponse();
                response.setPhotoId(photoID);
                response.setOriginalFilename(file.getOriginalFilename());
                uploadResponses.add(response);

                // TODO: Send message to RabbitMQ
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return uploadResponses;

    }
}
