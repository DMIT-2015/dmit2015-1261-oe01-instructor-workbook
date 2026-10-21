package dmit2015.model;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MapLocation {

    private String id;
    private String storeName;
    private double latitude;
    private double longitude;

    public MapLocation(String storeName, double latitude, double longitude)
    {
        id = UUID.randomUUID().toString();
        this.storeName = storeName;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
