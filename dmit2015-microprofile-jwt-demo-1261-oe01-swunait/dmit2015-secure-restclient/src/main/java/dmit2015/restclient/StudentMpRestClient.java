package dmit2015.restclient;

import dmit2015.model.Student;
import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import java.util.List;
import java.util.Optional;

@RequestScoped
@RegisterRestClient // baseUri is defined in `src/resources/META-INF/microprofile-config.properties
public interface StudentMpRestClient {

    @POST
    Response createStudent(Student student);

    @GET
    List<Student> getStudents();

    @GET
    @Path("{id}")
    Optional<Student> findStudentById(@PathParam("id") Long id);

    @PUT
    @Path("{id}")
    Student updateStudent(@PathParam("id") Long id, Student student);

    @DELETE
    @Path("{id}")
    void deleteStudent(@PathParam("id") Long id);
}
