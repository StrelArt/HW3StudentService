package telran.java58.student.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import telran.java58.configuration.ServiceConfiguration;
import telran.java58.student.dao.StudentRepository;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.dto.exeptions.ConflictException;
import telran.java58.student.dto.exeptions.NotFoundException;
import telran.java58.student.model.Student;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


//AAA - Arrange, Act, Assert

@ContextConfiguration(classes = {ServiceConfiguration.class})
@SpringBootTest
public class StudentServiceTest {
    private final long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private Student student;

    @Autowired
    private ModelMapper modelMapper;

    @MockitoBean
    private StudentRepository studentRepository;

    private StudentService studentService;

    @BeforeEach
    public void setUp(){
        student=new Student(studentId,name,password);
        studentService = new StudentServiceImpl(studentRepository, modelMapper);
    }

    @Test
    void testAddStudentWhenDoesNotExist(){
        // Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenReturn(student);
        // Act
        studentService.addStudent(dto);
        //Assert
        verify(studentRepository,times(1)).save(student);
    }

    @Test
    void testAddStudentWhenStudentExists(){
        // Arrange
        StudentCredentialsDto dto = new StudentCredentialsDto(studentId, name, password);
        when(studentRepository.existsById(studentId)).thenReturn(true);

        //Act & Assert
        assertThrows(ConflictException.class, () -> studentService.addStudent(dto));
        verify(studentRepository,never()).save(any(Student.class));

    }

    @Test
    void testFindStudentWhenStudentExists(){
        //Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // aCT
        StudentDto dto = studentService.findStudent(studentId);

        //Assert
        assertNotNull(dto);
        assertEquals(studentId, dto.getId());

    }

    @Test
    void testFindStudentWhenStudentNotExists(){
        //Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> studentService.findStudent(studentId));

    }

    @Test
    void testRemoveStudent(){
        //Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        // Act
        StudentDto dto = studentService.removeStudent(studentId);

        //Assert
        assertNotNull(dto);
        assertEquals(studentId, dto.getId());
        verify(studentRepository, times(1)).deleteById(studentId);

    }

    @Test
    void testUpdateStudent(){
        //Arrange
        String newName = "Jane";
        StudentUpdateDto dto = new StudentUpdateDto(newName, null);
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));

        //Act
        StudentCredentialsDto updatedDto = studentService.updateStudent(studentId, dto);

        //Assert
        assertNotNull(updatedDto);
        assertEquals(studentId, updatedDto.getId());
        assertEquals(newName, updatedDto.getName());
        assertEquals(password, updatedDto.getPassword());
        verify(studentRepository, times(1)).save(student);


    }

    @Test
    void testAddScore(){
        //Arrange
        when(studentRepository.findById(studentId)).thenReturn(Optional.ofNullable(student));
        String examName = "Exam";
        int score = 90;
        ScoreDto scoreDto = new ScoreDto(examName, score);

        //Act
        studentService.addScore(studentId, scoreDto);

        //Assert
        verify(studentRepository, times(1)).save(student);
        assertTrue(student.getScores().containsKey(examName));
        assertEquals(score, student.getScores().get(examName));
    }

    @Test
    void testFindStudentsByName(){

        //Arrange
        when(studentRepository.findByNameIgnoreCase(name)).thenReturn(Stream.of(student));
        //Act
        List<StudentDto> students = studentService.findStudentsByName(name);

        //Assert
        assertNotNull(students);
        assertEquals(1, students.size());
        assertEquals(studentId, students.get(0).getId());
        assertEquals(name, students.get(0).getName());
    }

    @Test
    void testCountStudentsByNames(){
        Set<String> names = Set.of(name, "Peter");
        when(studentRepository.countByNameInIgnoreCase(names)).thenReturn(2L);

        //Act
        Long result = studentService.countStudentsByNames(names);

        //Assert
        assertNotNull(result);
        assertEquals(2L, result);
    }

    @Test
    void testFindStudentsByExamNameMinScore(){

        //Arrange
        String examName = "Exam";
        int minScore = 90;
        when(studentRepository.findByExamAndScoresGreaterThan(examName, minScore))
                .thenReturn(Stream.of(student));

        //Act
        List<StudentDto> students = studentService
                .findStudentsByExamNameMinScore(examName, minScore);

        //Assert
        assertNotNull(students);
        assertEquals(1, students.size());
        assertEquals(studentId, students.get(0).getId());
        assertEquals(name, students.get(0).getName());
    }



}
