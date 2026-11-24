package dmit2015.repository;

import dmit2015.entity.Student;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;

import java.util.List;
import java.util.Optional;

/**
 * This Jakarta Persistence class contains methods for performing CRUD operations on a
 * Jakarta Persistence managed entity.
 */
@ApplicationScoped
public class StudentRepository {

    // Assign a unitName if there are more than one persistence unit defined in persistence.xml
    @PersistenceContext //(unitName="pu-name-in-persistence.xml")
    private EntityManager entityManager;

    @Transactional
    public void add(@Valid Student newStudent) {
        // If the primary key is not an identity column then write code below here to
        // 1) Generate a new primary key value
        // 2) Set the primary key value for the new entity

        entityManager.persist(newStudent);
    }

    public Optional<Student> findById(Long studentId) {
        try {
            Student querySingleResult = entityManager.find(Student.class, studentId);
            if (querySingleResult != null) {
                return Optional.of(querySingleResult);
            }
        } catch (Exception ex) {
            // studentId value not found
            throw new RuntimeException(ex);
        }
        return Optional.empty();
    }

    public List<Student> findAll() {
        return entityManager.createQuery("SELECT o FROM Student o ", Student.class)
                .getResultList();
    }

    @Transactional
    public Student update(@Valid Student updatedStudent) {
        Long id = updatedStudent.getId();
        Optional<Student> optionalStudent = findById(id);
        if (optionalStudent.isEmpty()) {
            String errorMessage = String.format("The id %s does not exists in the system.", id);
            throw new RuntimeException(errorMessage);
        } else {
             var existingStudent = optionalStudent.orElseThrow();
             // Update only properties that is editable by the end user
             existingStudent.setFirstName(updatedStudent.getFirstName());
             existingStudent.setLastName(updatedStudent.getLastName());
             existingStudent.setCourseSection(updatedStudent.getCourseSection());
//             existingStudent.setPicture(updatedStudent.getPicture());

            updatedStudent = entityManager.merge(existingStudent);
        }
        return updatedStudent;
    }

    @Transactional
    public void delete(Student existingStudent) {
         // Write code to throw a RuntimeException if this entity contains child records

        if (entityManager.contains(existingStudent)) {
            entityManager.remove(existingStudent);
        } else {
            entityManager.remove(entityManager.merge(existingStudent));
        }
    }

    @Transactional
    public void deleteById(Long studentId) {
        Optional<Student> optionalStudent = findById(studentId);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.orElseThrow();
            // Write code to throw a RuntimeException if this entity contains child records

            entityManager.remove(existingStudent);
        }
    }

    public long count() {
        return entityManager.createQuery("SELECT COUNT(o) FROM Student o", Long.class).getSingleResult();
    }

    @Transactional
    public void deleteAll() {
        entityManager.flush();
        entityManager.clear();
        entityManager.createQuery("DELETE FROM Student").executeUpdate();
    }

}