package org.projects.dupligonebackend.service.impl;

import org.projects.dupligonebackend.context.SessionContextHolder;
import org.projects.dupligonebackend.dto.ClusterResponseDto;
import org.projects.dupligonebackend.dto.PhotoResponseDto;
import org.projects.dupligonebackend.model.Cluster;
import org.projects.dupligonebackend.model.Photo;
import org.projects.dupligonebackend.model.PhotoSession;
import org.projects.dupligonebackend.repository.ClusterRepository;
import org.projects.dupligonebackend.repository.PhotoRepository;
import org.projects.dupligonebackend.repository.PhotoSessionRepository;
import org.projects.dupligonebackend.service.ClusterService;
import org.projects.dupligonebackend.utils.PhotoHashVector;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;

@Service
public class ClusterServiceImpl implements ClusterService {

    private final ClusterRepository clusterRepository;
    private final PhotoRepository photoRepository;
    private final PhotoSessionRepository sessionRepository;

    public ClusterServiceImpl(ClusterRepository clusterRepository,
                              PhotoRepository photoRepository,
                              PhotoSessionRepository sessionRepository) {
        this.clusterRepository = clusterRepository;
        this.photoRepository = photoRepository;
        this.sessionRepository = sessionRepository;
    }


    @Override
    public List<ClusterResponseDto> getClustersForSession() {
        UUID sessionId = SessionContextHolder.getSessionId();
        List<Cluster> clusters = clusterRepository.findBySessionId(sessionId);

        return clusters.stream().map(cluster -> {
            List<PhotoResponseDto> photos = cluster.getPhotos().stream()
                    .map(photo -> new PhotoResponseDto(
                            photo.getId(),
                            photo.getFilePath()
                    ))
                    .toList();
            UUID bestPhotoId = null;
            for(Photo photo : cluster.getPhotos()){
                if(photo.isBest()){
                    bestPhotoId = photo.getId();
                    break;
                }
            }
            return new ClusterResponseDto(
                    cluster.getId(),
                    bestPhotoId,
                    photos
            );
        }).toList();
    }

    @Override
    public void runClusterJob(PhotoSession session) {
        List<Photo> photos = getAnalyzedPhotosForSession(session.getId());
        List<Cluster> clusters = cluster(photos);

        for(Cluster cluster : clusters){
            UUID bestPhotoId = selectBestPhoto(cluster.getPhotos());
            markAndSavePhotos(cluster.getPhotos(), bestPhotoId);
        }
        
        clusterRepository.saveAll(clusters);
    }

    private void markAndSavePhotos(List<Photo> photos, UUID bestPhotoId) {
        for(Photo photo : photos) {
            if (photo.getId() == bestPhotoId) {
                photo.setBest(true);
                break;
            }
        }
        photoRepository.saveAll(photos);
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
            cluster.setPhotos(new ArrayList<>());
            cluster.getPhotos().add(point.getPhoto());
            //point.getPhoto().setCluster(cluster);
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
                    //neighbor.getPhoto().setCluster(cluster);
                    clustered.add(neighbor);
                }
            }
            cluster.setFinalized(true);
            clusters.add(cluster);
        }
        return clusters;
    }

    private List<PhotoHashVector> getNeighbors(PhotoHashVector p, List<PhotoHashVector> all, int eps){
        return all.stream()
                .filter(other -> !p.equals(other) && p.hammingDistanceTo(other) <= eps)
                .collect(Collectors.toList());
    }

    private UUID selectBestPhoto(List<Photo> photos){
        if(photos == null || photos.isEmpty())
            throw new IllegalArgumentException("Photo list is empty");

        double maxSharp = getMax(photos, Photo::getSharpness);
        double minSharp = getMin(photos, Photo::getSharpness);

        double maxBright = getMax(photos, Photo::getBrightness);
        double minBright = getMin(photos, Photo::getBrightness);

        double maxContrast = getMax(photos, Photo::getContrast);
        double minContrast = getMin(photos, Photo::getContrast);

        double maxFace = getMax(photos, Photo::getFaceCount);
        double minFace = getMin(photos, Photo::getFaceCount);

        double maxSmile = getMax(photos, Photo::getSmileScore);
        double minSmile = getMin(photos, Photo::getSmileScore);

        double maxExposure = getMax(photos, Photo::getExposureFlatness);
        double minExposure = getMin(photos, Photo::getExposureFlatness);

        double bestScore = Double.NEGATIVE_INFINITY;
        UUID bestPhotoId = null;

        for(Photo photo : photos){
            double score = 0.0;

            score += 0.3 * normalize(photo.getSharpness(), minSharp, maxSharp);
            score += 0.2 * normalize(photo.getBrightness(), minBright, maxBright);
            score += 0.1 * normalize(photo.getContrast(), minContrast, maxContrast);
            score += 0.2 * normalize(photo.getFaceCount(), minFace, maxFace);
            score += 0.1 * normalize(photo.getSmileScore(), minSmile, maxSmile);

            // Exposure Flatness is better when lower, so we inverse the normalization
            score += 0.1 * (1.0 - normalize(photo.getExposureFlatness(), minExposure, maxExposure));

            if(score > bestScore){
                bestScore = score;
                bestPhotoId = photo.getId();
            }
        }

        return bestPhotoId;
    }

    private double getMax(List<Photo> photos, ToDoubleFunction<Photo> extractor){
        return photos.stream().mapToDouble(extractor).max().orElse(0.0);
    }

    private double getMin(List<Photo> photos, ToDoubleFunction<Photo> extractor){
        return photos.stream().mapToDouble(extractor).min().orElse(0.0);
    }

    private double normalize(double value, double min, double max){
        if(max == min)
            return 0.0;

        return (value - min)/(max - min);
    }
}
