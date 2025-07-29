package org.projects.dupligonebackend.service;

import org.projects.dupligonebackend.model.PhotoSession;

public interface PhotoSessionService {
    PhotoSession createSession();
    void pollAndClusterSessions();
}
