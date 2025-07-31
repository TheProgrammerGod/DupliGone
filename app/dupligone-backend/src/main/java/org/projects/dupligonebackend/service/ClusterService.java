package org.projects.dupligonebackend.service;

import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.model.PhotoSession;

import java.util.List;
import java.util.UUID;

public interface ClusterService {
    List<Cluster> getClustersForSession();

    void runClusterJob(PhotoSession session);
}
