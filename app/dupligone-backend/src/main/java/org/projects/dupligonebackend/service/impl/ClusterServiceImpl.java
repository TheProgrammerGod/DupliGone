package org.projects.dupligonebackend.service.impl;

import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.repository.ClusterRepository;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.service.ClusterService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

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
        // TODO
        return null;
    }


}
