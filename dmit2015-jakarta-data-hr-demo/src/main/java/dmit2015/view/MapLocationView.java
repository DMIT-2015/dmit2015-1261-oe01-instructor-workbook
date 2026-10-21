package dmit2015.view;

import dmit2015.model.MapLocation;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import lombok.Getter;

import java.util.List;

@Named
@RequestScoped
public class MapLocationView {

    @Getter
    private List<MapLocation> locations = List.of(
 new MapLocation("Northern Alberta Institute of Technology",53.568072,-113.504224),
 new MapLocation("University of Alberta",53.5231733,-113.5275665),
 new MapLocation("MacEwan University",53.5470785,-113.5101388)
            );
}
