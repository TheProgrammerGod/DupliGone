package org.projects.dupligonebackend.controller;

import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.repository.ClusterRepository;
import org.projects.dupligonebackend.service.ClusterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/clusters")
public class ClusterController {

    private final ClusterService clusterService;

    public ClusterController(ClusterService clusterService){
        this.clusterService = clusterService;
    }

    @GetMapping
    public ResponseEntity<List<Cluster>> getAllClusters(){
        UUID sessionId = SessionContextHolder.getSessionId();
        return ResponseEntity.ok(clusterService.getClustersForSession(sessionId));
    }

}
