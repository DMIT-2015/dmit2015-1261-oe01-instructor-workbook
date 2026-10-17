package dmit2015.repository;

import dmit2015.entity.Region;
import jakarta.data.repository.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumanResourcesRepository {

    @Insert
    Region addRegion(Region newRegion);

    @Update
    Region updateRegion(Region updatedRegion);

    @Delete
    void deleteRegion(Region existingRegion);

    @Find
    Region findRegionById(Long id);

    @Find
    Optional<Region> findOptionalRegionById(Long id);

    @Find
    List<Region> findAllRegions();

    @Find
    @OrderBy("regionName")
    List<Region> findAllRegionsOrderByRegionName();

}
