package org.projects.dupligonebackend.repository;

import org.projects.dupligonebackend.model.PhotoSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PhotoSessionRepository extends JpaRepository<PhotoSession, UUID> {

    public PhotoSession findPhotoSessionById(UUID id);

}
