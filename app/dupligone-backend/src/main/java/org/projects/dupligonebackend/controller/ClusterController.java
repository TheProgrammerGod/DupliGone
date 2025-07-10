package org.projects.dupligonebackend.controller;

import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.repository.ClusterRepository;
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

    private final ClusterRepository clusterRepository;

    public ClusterController(ClusterRepository clusterRepository){
        this.clusterRepository = clusterRepository;
    }

    @GetMapping
    public ResponseEntity<List<Cluster>> getAllClusters(
            @RequestHeader("X-Session-Id")UUID sessionId
            ){
        List<Cluster> clusters = clusterRepository.findBySessionId(sessionId);
        return ResponseEntity.ok(clusters);
    }

}
