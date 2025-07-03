package telran.java58.student.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import telran.java58.student.model.Student;

public interface StudentRepository extends MongoRepository<Student,Long> {

}
