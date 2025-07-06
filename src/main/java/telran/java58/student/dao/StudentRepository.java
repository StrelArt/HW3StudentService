package telran.java58.student.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import telran.java58.student.model.Student;

import java.util.Set;
import java.util.stream.Stream;

public interface StudentRepository extends MongoRepository<Student,Long> {
    Stream<Student> findByNameIgnoreCase(String name);

    Long countByNameInIgnoreCase(Set<String> names);


    //TODO
//    @Query("{'scores.Math':{'$gt':90} }")
    @Query("{'scores.?0':{$gte: ?1}}")
    Stream<Student> findByExamAndScoresGreaterThan(String examName, int score);



}
