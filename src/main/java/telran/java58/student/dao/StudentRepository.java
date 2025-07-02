package telran.java58.student.dao;

import telran.java58.student.model.Student;

import java.util.List;
import java.util.Optional;

public interface StudentRepository {
    Student save(Student student);

    Optional<Student> findById(long id);

    void deleteById(long id);

    List<Student> findAll();
}
