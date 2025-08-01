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

import java.util.List;
import java.util.UUID;
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
                .map(photo -> new PhotoHashVector(photo, extractCombinedHashVector(photo)))
                .toList();

        // run DBSCAN
        List<Cluster> clusters =

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

}
