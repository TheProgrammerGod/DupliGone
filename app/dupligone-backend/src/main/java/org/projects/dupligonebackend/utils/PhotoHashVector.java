package org.projects.dupligonebackend.utils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.projects.dupligonebackend.model.Photo;

import java.util.UUID;

@Data
@AllArgsConstructor
public class PhotoHashVector {
    private final Photo photo;
    private final long[] hashVector;
    private final UUID sessionID;

    public int hammingDistanceTo(PhotoHashVector other){
        int total = 0;
        for(int i = 0; i < hashVector.length; i++){
            total += Long.bitCount(this.hashVector[i] ^ other.hashVector[i]);
        }
        return total;
    }
}
