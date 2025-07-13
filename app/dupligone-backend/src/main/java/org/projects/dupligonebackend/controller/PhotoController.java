package org.projects.dupligonebackend.controller;

import jakarta.persistence.EntityNotFoundException;
import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.dto.PhotoUploadResponse;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.PhotoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("api/photos")
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService){
        this.photoService = photoService;
    }

    @GetMapping
    public ResponseEntity<List<Photo>> gePhotosByCluster(
            @RequestParam("clusterId") UUID clusterId
            ){
        UUID sessionId = SessionContextHolder.getSessionId();
        return ResponseEntity.ok(photoService.getPhotosForCluster(clusterId, sessionId));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPhotos(
            @RequestParam("files") List<MultipartFile> files
    ){
        UUID sessionId = SessionContextHolder.getSessionId();
        List<PhotoUploadResponse> uploaded = photoService.saveUploadedPhotos(files, sessionId);

        return ResponseEntity.ok(Map.of("uploaded", uploaded));
    }

}
