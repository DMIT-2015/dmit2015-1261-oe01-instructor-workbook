package dmit2015.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import net.datafaker.Faker;

import java.util.UUID;

@Data
public class Student {

    private String id;

    @NotBlank(message = "First Name is required")
    @Size(min = 1, message = "First Name must contain at least {min} or more characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 2, message = "Last Name must contain at least {min} or more characters")
    private String lastName;

    public static Student of(Faker faker) {
        Student newStudent = new Student();
        newStudent.setId(UUID.randomUUID().toString());
        newStudent.setFirstName(faker.name().firstName());
        newStudent.setLastName(faker.name().lastName());
        return newStudent;
    }

}
