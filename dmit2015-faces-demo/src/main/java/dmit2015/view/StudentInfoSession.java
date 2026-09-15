package dmit2015.view;

import dmit2015.model.StudentInfo;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Named;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Named
@SessionScoped
public class StudentInfoSession implements Serializable {

    private List<StudentInfo> students = new ArrayList<>(); // getter

    public void add(StudentInfo newStudentInfo) {
        students.add(newStudentInfo);
    }

    public void remove(StudentInfo existingStudentInfo) {
        students.remove(existingStudentInfo);
    }

    public List<StudentInfo> getStudents() {
        return students;
    }
}
