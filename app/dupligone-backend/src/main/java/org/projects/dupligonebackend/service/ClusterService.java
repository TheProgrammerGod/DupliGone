package org.projects.dupligonebackend.service;

import org.projects.dupligonebackend.model.Cluster;

import java.util.List;
import java.util.UUID;

public interface ClusterService {
    List<Cluster> getClustersForSession(UUID sessionId);
}
