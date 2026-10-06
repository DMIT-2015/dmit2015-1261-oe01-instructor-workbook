package dmit2015.service;

import dmit2015.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentService {

    Student createStudent(Student student);

    Optional<Student> getStudentById(Long id);

    List<Student> getAllStudents();

    Student updateStudent(Student student);

    void deleteStudentById(Long id);
}