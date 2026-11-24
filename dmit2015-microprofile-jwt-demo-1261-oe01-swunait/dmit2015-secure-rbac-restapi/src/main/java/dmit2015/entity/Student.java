package dmit2015.entity;

import jakarta.json.bind.annotation.JsonbTransient;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * This Jakarta Persistence class is mapped to a relational database table with the same name.
 * If Java class name does not match database table name, you can use @Table annotation to specify the table name.
 * <p>
 * Each field in this class is mapped to a column with the same name in the mapped database table.
 * If the field name does not match database table column name, you can use the @Column annotation to specify the database table column name.
 * The @Transient annotation can be used on field that is not mapped to a database table column.
 */
@Entity
//@Table(schema = "CustomSchemaName", name="CustomTableName")
@Getter
@Setter
public class Student implements Serializable {

    private static final Logger _logger = Logger.getLogger(Student.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "studentid", nullable = false)
    private Long id;

    @NotBlank(message = "First name is required.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(min = 2, message = "Last name must contain 2 or more characters.")
    private String lastName;

    private String courseSection;

    // For PostgreSQL define columnDefinition as BYTEA instead of using @Lob
//    @Column(columnDefinition = "BYTEA")     // PostgreSQL map to bytea instead of oid data type
    @Lob
    @JsonbTransient // Prevents mapping of a Java Bean property, field or type to JSON representation.
    private byte[] picture;

    public Student() {

    }

    @Version
    private Integer version;

    @Column(nullable = false)
    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @PrePersist
    private void beforePersist() {
        createTime = LocalDateTime.now();
    }

    @PreUpdate
    private void beforeUpdate() {
        updateTime = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object obj) {
        return (
                (obj instanceof Student other) &&
                        Objects.equals(id, other.id)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    // Factory method to create a new Student instance
    public static Student of(Faker faker) {
        Student currentStudent = new Student();

        currentStudent.setFirstName(faker.name().firstName());
        currentStudent.setLastName(faker.name().lastName());
        currentStudent.setCourseSection("DMIT2015-1261");
        return currentStudent;
    }


}