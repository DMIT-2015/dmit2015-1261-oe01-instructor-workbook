package dmit2015.view;

import dmit2015.entity.Department;
import dmit2015.repository.HumanResourcesRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.convert.Converter;
import jakarta.faces.convert.FacesConverter;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ApplicationScoped
@FacesConverter(value = "departmentConverter", managed = true)
public class DepartmentConverter implements Converter<Department> {

    @Inject
    private HumanResourcesRepository hrRepository;

    @Override
    public Department getAsObject(FacesContext context, UIComponent component, String value) {
        Short deptId = Short.valueOf(value);
        return hrRepository.departmentByDepartmentId(deptId);
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Department value) {
        return value.getId().toString();
    }
}
