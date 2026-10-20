package dmit2015.view;

import dmit2015.entity.Department;
import dmit2015.entity.Employee;
import dmit2015.repository.HumanResourcesRepository;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;
import lombok.Setter;
import org.omnifaces.util.Messages;

import java.io.Serializable;
import java.util.List;

@Named("currentEmployeeQueryView")
@ViewScoped
public class EmployeeQueryView implements Serializable {

    @Inject
    private transient HumanResourcesRepository hrRepository;

    @Getter
    private List<Employee> queryResults;

    @Getter @Setter
    private Department selectedDepartment;

    public List<Department> completeDepartment(String query) {
        return  hrRepository.departmentsBy("%" + query + "%");
    }

    public void onSearchByDepartment() {
        try {
            queryResults = hrRepository.employeesByDepartmentId(selectedDepartment.getId());
            Messages.addGlobalInfo("Query returned {0} results.", queryResults.size());
        } catch (Exception ex) {
            handleException(ex);
        }
    }
    public void onClear() {
        queryResults = null;
        selectedDepartment = null;
    }

    /**
     * This method is used to handle exceptions and display root cause to user.
     *
     * @param ex The Exception to handle.
     */
    protected void handleException(Exception ex) {
        var details = new StringBuilder();
        Throwable causes = ex;
        while (causes.getCause() != null) {
            details.append(ex.getMessage());
            details.append(" Caused by:");
            details.append(causes.getCause().getMessage());
            causes = causes.getCause();
        }
        Messages.create(ex.getMessage()).detail(details.toString()).error().add("errors");
    }
}
