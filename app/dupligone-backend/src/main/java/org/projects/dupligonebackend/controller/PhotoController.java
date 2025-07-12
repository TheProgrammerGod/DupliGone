package org.projects.dupligonebackend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/photos")
public class PhotoController {

    private final PhotoRepository photoRepository;

    public PhotoController(PhotoRepository photoRepository){
        this.photoRepository = photoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Photo>> gePhotosByCluster(
            @RequestParam("clusterId") UUID clusterId
            ){
        UUID sessionId = SessionContextHolder.getSessionId();
        List<Photo> photos = photoRepository.findBySessionIdAndClusterId(sessionId, clusterId);
        if(photos.isEmpty()){
            throw new EntityNotFoundException("No photos found for this cluster in this session");
        }

        return ResponseEntity.ok(photos);
    }

}
