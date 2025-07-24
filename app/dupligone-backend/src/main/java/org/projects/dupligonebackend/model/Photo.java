package org.projects.dupligonebackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Photo {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID sessionId;

    @Column(nullable = false)
    private String filePath;

    @Column(length = 128)
    private String ahash;
    @Column(length = 128)
    private String phash;
    @Column(length = 128)
    private String dhash;
    @Column(length = 128)
    private String whash;

    private double brightness;
    private double sharpness;
    private double contrast;
    private double exposureFlatness;
    private double smileScore;
    private Integer faceCount;

    private boolean isBest;

    private Instant analyzedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cluster_id")
    private Cluster cluster;
}
