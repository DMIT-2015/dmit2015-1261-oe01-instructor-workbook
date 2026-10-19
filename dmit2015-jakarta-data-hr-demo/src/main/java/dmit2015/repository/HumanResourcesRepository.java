package dmit2015.repository;

import dmit2015.entity.Department;
import dmit2015.entity.Employee;
import dmit2015.entity.Region;
import jakarta.data.repository.*;

import java.util.List;
import java.util.Optional;

@Repository
public interface HumanResourcesRepository {

    @Query("""
select d
from Department d
where lower(d.departmentName) like lower(?1)
order by d.departmentName
""")
    List<Department> departmentsBy(String departmentName);

    @Find
    Department departmentByDepartmentId(Short id);

    @Query("""
select e
from Employee e join fetch e.department join fetch e.job join fetch e.manager
where e.department.id = ?1
""")
    List<Employee> employeesByDepartmentId(Short id);

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
