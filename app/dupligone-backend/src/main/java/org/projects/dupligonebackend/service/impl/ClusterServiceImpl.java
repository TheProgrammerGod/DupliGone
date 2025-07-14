package org.projects.dupligonebackend.service.impl;

import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.repository.ClusterRepository;
import org.projects.dupligonebackend.service.ClusterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClusterServiceImpl implements ClusterService {

    private final ClusterRepository clusterRepository;
    private final UUID sessionId = SessionContextHolder.getSessionId();

    public ClusterServiceImpl(ClusterRepository clusterRepository) {
        this.clusterRepository = clusterRepository;
    }


    @Override
    public List<Cluster> getClustersForSession() {
        return clusterRepository.findBySessionId(sessionId);
    }
}
