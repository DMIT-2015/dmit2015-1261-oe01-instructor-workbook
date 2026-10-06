package dmit2015.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import net.datafaker.Faker;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.random.RandomGenerator;

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

    private static final Logger logger = Logger.getLogger(Student.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    // @Column(name = "ColumnName", nullable = false)
    private Long id;

    @NotBlank(message = "First Name is required")
    @Size(min = 1, message = "First Name must contain at least {min} or more characters")
    private String firstName;

    @NotBlank(message = "Last Name is required")
    @Size(min = 2, message = "Last Name must contain at least {min} or more characters")
    private String lastName;

    public Student() {

    }

    @Version
    private Integer version;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createTime;

    @Column(nullable = false)
    private LocalDateTime updateTime;

    @PrePersist
    void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createTime = now;
        updateTime = now;
    }

    @PreUpdate
    void onUpdate() {
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
        return currentStudent;
    }
}