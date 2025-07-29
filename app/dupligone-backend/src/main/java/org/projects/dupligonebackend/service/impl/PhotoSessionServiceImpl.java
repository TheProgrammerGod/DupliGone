package org.projects.dupligonebackend.service.impl;

import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.repository.PhotoSessionRepository;
import org.projects.dupligonebackend.service.PhotoSessionService;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class PhotoSessionServiceImpl implements PhotoSessionService {

    private final PhotoSessionRepository sessionRepository;

    public PhotoSessionServiceImpl(PhotoSessionRepository sessionRepository){
        this.sessionRepository = sessionRepository;
    }

    @Override
    public PhotoSession createSession() {
        PhotoSession session = new PhotoSession();
        UUID sessionId = UUID.randomUUID();
        session.setId(sessionId);
        session.setCreatedAt(Instant.now());
        sessionRepository.save(session);
        return session;
    }

    @Override
    public void pollAndClusterSessions() {

    }
}
