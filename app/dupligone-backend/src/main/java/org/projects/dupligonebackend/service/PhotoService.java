package org.projects.dupligonebackend.service;

import org.projects.dupligonebackend.model.Photo;

import java.util.List;
import java.util.UUID;

public interface PhotoService {

    List<Photo> getPhotosForCluster(UUID clusterId, UUID sessionId);

}
