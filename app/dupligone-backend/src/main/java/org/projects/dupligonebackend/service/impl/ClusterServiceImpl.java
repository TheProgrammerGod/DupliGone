package org.projects.dupligonebackend.service.impl;

import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.repository.ClusterRepository;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.ClusterService;
import org.projects.dupligonebackend.utils.PhotoHashVector;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements ClusterService {

    private final ClusterRepository clusterRepository;
    private final PhotoRepository photoRepository;

    public ClusterServiceImpl(ClusterRepository clusterRepository,
                              PhotoRepository photoRepository) {
        this.clusterRepository = clusterRepository;
        this.photoRepository = photoRepository;
    }


    @Override
    public List<Cluster> getClustersForSession() {
        UUID sessionId = SessionContextHolder.getSessionId();
        return clusterRepository.findBySessionId(sessionId);
    }

    @Override
    public void runClusterJob(PhotoSession session) {
        List<Photo> photos = getAnalyzedPhotosForSession(session.getId());
        List<Cluster> clusters = cluster(photos);

        for(Cluster cluster : clusters){
            
        }
    }

    private List<Photo> getAnalyzedPhotosForSession(UUID sessionId){
        return photoRepository.findAllBySessionId(sessionId);
    }

    private List<Cluster> cluster(List<Photo> photos){

        // Conver hash strings to bit arrays or long ints
        List<PhotoHashVector> vectors = photos.stream()
                .map(photo -> new PhotoHashVector(photo, extractCombinedHashVector(photo), photo.getSessionId()))
                .toList();

        // run DBSCAN
        List<Cluster> clusters = runDBSCAN(vectors);

        return clusters;
    }

    private long[] extractCombinedHashVector(Photo photo){
        // We'll treat each hash as a 64-bit long and concatenate all into a vector
        return new long[]{
          Long.parseUnsignedLong(photo.getAhash(), 16),
          Long.parseUnsignedLong(photo.getPhash(), 16),
          Long.parseUnsignedLong(photo.getDhash(), 16),
          Long.parseUnsignedLong(photo.getWhash(), 16)
        };
    }

    private List<Cluster> runDBSCAN(List<PhotoHashVector> vectors){
        int eps = 45, minPts = 2;
        List<Cluster> clusters = new ArrayList<>();
        Set<PhotoHashVector> visited = new HashSet<>();
        Set<PhotoHashVector> clustered = new HashSet<>();

        for(PhotoHashVector point : vectors){
            if(visited.contains(point))
                continue;
            visited.add(point);

            List<PhotoHashVector> neighbors = getNeighbors(point, vectors, eps);
            if(neighbors.size() < minPts)
                continue;

            Cluster cluster = new Cluster();
            cluster.setId(UUID.randomUUID());
            cluster.setSessionId(point.getSessionID());
            cluster.setCreatedAt(Instant.now());
            cluster.getPhotos().add(point.getPhoto());
            clustered.add(point);
            Queue<PhotoHashVector> queue = new LinkedList<>(neighbors);

            while(!queue.isEmpty()){
                PhotoHashVector neighbor = queue.poll();
                if(!visited.contains(neighbor)){
                    visited.add(neighbor);
                    List<PhotoHashVector> neighborNeighbors = getNeighbors(neighbor, vectors, eps);
                    if(neighborNeighbors.size() >= minPts)
                        queue.addAll(neighborNeighbors);
                }

                if(!clustered.contains(neighbor)){
                    cluster.getPhotos().add(neighbor.getPhoto());
                    clustered.add(neighbor);
                }
            }
            clusters.add(cluster);
        }
        return clusters;
    }

    private List<PhotoHashVector> getNeighbors(PhotoHashVector p, List<PhotoHashVector> all, int eps){
        return all.stream()
                .filter(other -> !p.equals(other) && p.hammingDistanceTo(other) <= eps)
                .collect(Collectors.toList());
    }

}
