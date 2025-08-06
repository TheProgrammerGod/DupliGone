package org.projects.dupligonebackend.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "photos")
@EqualsAndHashCode(exclude = "photos")
public class Cluster {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID sessionId;

    private String tag;
    private Instant createdAt;
    private boolean finalized;

    @OneToMany(mappedBy = "cluster", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Photo> photos;

    public void addPhoto(Photo photo){
        this.photos.add(photo);
        photo.setCluster(this);
    }

}
