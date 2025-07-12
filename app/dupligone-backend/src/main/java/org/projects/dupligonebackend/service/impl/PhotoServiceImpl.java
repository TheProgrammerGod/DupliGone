package org.projects.dupligonebackend.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.PhotoService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PhotoServiceImpl implements PhotoService {

    private final PhotoRepository photoRepository;

    public PhotoServiceImpl(PhotoRepository photoRepository) {
        this.photoRepository = photoRepository;
    }

    @Override
    public List<Photo> getPhotosForCluster(UUID clusterId, UUID sessionId) {
        List<Photo> photos = photoRepository.findBySessionIdAndClusterId(sessionId, clusterId);
        if(photos.isEmpty()){
            throw new EntityNotFoundException("No photos found for this cluster in this session");
        }
        return photos;
    }
}
