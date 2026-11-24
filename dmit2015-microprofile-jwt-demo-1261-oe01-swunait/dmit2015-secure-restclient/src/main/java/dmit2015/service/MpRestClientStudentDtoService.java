package dmit2015.service;

import dmit2015.model.Student;
import dmit2015.restclient.StudentMpRestClient;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.util.List;
import java.util.Optional;

@Named("currentMpRestClientStudentDtoService")
@ApplicationScoped
public class MpRestClientStudentDtoService implements StudentService {

    @Inject
    @RestClient
    private StudentMpRestClient mpRestClient;

    @Override
    public Student createStudent(Student student) {
        try (Response response = mpRestClient.createStudent(student)) {
            if (response.getStatus() != Response.Status.CREATED.getStatusCode()) {
                throw new WebApplicationException("Create failed with HTTP error code :"
                        + response.getStatus());
            } else {
                String location = response.getHeaderString("location");
                int resourceIdIndex = location.lastIndexOf("/") + 1;
                Long resourceId = Long.parseLong(location.substring(resourceIdIndex));
                student.setId(resourceId);
            }
        }
        return student;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        return mpRestClient.findStudentById(id);
    }

    @Override
    public List<Student> getAllStudents() {
        return mpRestClient.getStudents();
    }

    @Override
    public Student updateStudent(Student student) {
        return mpRestClient.updateStudent(student.getId(), student);
    }

    @Override
    public void deleteStudentById(Long id) {
        mpRestClient.deleteStudent(id);
    }
}
