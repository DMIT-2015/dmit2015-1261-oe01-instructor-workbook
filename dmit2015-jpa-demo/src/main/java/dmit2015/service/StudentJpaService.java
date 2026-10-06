package dmit2015.service;

import dmit2015.model.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Named;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Named("jakartaPersistenceStudentService")
@ApplicationScoped
public class StudentJpaService implements StudentService {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager entityManager;

    @Override
    @Transactional
    public Student createStudent(Student student) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        entityManager.persist(student);
        return student;
    }

    @Override
    public Optional<Student> getStudentById(Long id) {
        try {
            Student querySingleResult = entityManager.find(Student.class, id);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // id value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    @Override
    public List<Student> getAllStudents() {
        return entityManager.createQuery("SELECT o FROM Student o ", Student.class)
                .getResultList();
    }

    @Override
    @Transactional
    public Student updateStudent(Student student) {

        Optional<Student> optionalStudent = getStudentById(student.getId());
        if (optionalStudent.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", student.getId());
            throw new RuntimeException(errorMessage);
        } else {
            var existingStudent = optionalStudent.orElseThrow();
            // Update only properties that is editable by the end user

            existingStudent.setFirstName(student.getFirstName());
            existingStudent.setLastName(student.getLastName());

            student = entityManager.merge(existingStudent);
        }
        return student;
    }

    @Override
    @Transactional
    public void deleteStudentById(Long id) {
        Optional<Student> optionalStudent = getStudentById(id);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records
            entityManager.remove(student);
        } else {
            throw new RuntimeException("Could not find Student with id: " + id);
        }
    }

}