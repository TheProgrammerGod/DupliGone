package org.projects.dupligonebackend.controller;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.dto.PhotoUploadResponse;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.PhotoService;
import org.projects.dupligonebackend.validation.ValidImage;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Validated
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
        return ResponseEntity.ok(photoService.getPhotosForCluster(clusterId));
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadPhotos(
            @RequestParam("files") @Valid @ValidImage List<MultipartFile> files
    ){
        List<PhotoUploadResponse> uploaded = photoService.saveUploadedPhotos(files);
        return ResponseEntity.ok(Map.of("uploaded", uploaded));
    }

}
