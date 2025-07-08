package org.projects.dupligonebackend.repository;

import org.projects.dupligonebackend.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PhotoRepository extends JpaRepository<Photo, UUID> {

    public List<Photo> findByClusterId(UUID clusterId);

    public List<Photo> findByIsBestTrue();

    public List<Photo> findByHash(String hash);

    public boolean existsByHash(String hash);

    public List<Photo> findBySessionId(UUID sessionId);

}
