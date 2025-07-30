package org.projects.dupligonebackend.service.impl;

import jakarta.transaction.Transactional;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.repository.PhotoSessionRepository;
import org.projects.dupligonebackend.service.PhotoSessionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
public class PhotoSessionServiceImpl implements PhotoSessionService {

    private final PhotoSessionRepository sessionRepository;
    private final PhotoRepository photoRepository;

    public PhotoSessionServiceImpl(PhotoSessionRepository sessionRepository,
                                   PhotoRepository photoRepository){
        this.sessionRepository = sessionRepository;
        this.photoRepository = photoRepository;
    }

    @Override
    public PhotoSession createSession() {
        PhotoSession session = new PhotoSession();
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        session.setCreatedAt(Instant.now());
        session.setClusteringStatus("PENDING");
        sessionRepository.save(session);
        return session;
    }

    @Override
    @Transactional
    @Scheduled(fixedDelay = 60000) // Every 60 seconds
    public void pollAndClusterSessions() {
        List<PhotoSession> pendingSessions = sessionRepository.findAllByClusteringStatus("PENDING");
        for(PhotoSession session : pendingSessions){
            List<Photo> photos = photoRepository.findAllBySessionId(session.getId());

            boolean allAnalyzed = photos.stream().allMatch(photo ->
                    photo.getAnalyzedAt() != null
                    );

            if(allAnalyzed){
                session.setClusteringStatus("IN_PROGRESS");
                sessionRepository.save(session);
                try{
                    // TODO: Trigger clustering logic

                    session.setClusteringStatus("COMPLETED");
                } catch (Exception e){
                    session.setClusteringStatus("FAILED");

                }
                sessionRepository.save(session);
            }
        }
    }
}
