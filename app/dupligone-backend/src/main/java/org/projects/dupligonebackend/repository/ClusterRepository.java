package org.projects.dupligonebackend.repository;

import org.projects.dupligonebackend.model.Cluster;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ClusterRepository extends JpaRepository<Cluster, UUID> {
    public List<Cluster> findBySessionId(UUID sessionID);

}
