package telran.java58.student.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.service.StudentService;

import java.util.List;
import java.util.Set;

@RestController
@RequiredArgsConstructor
public class StudentController {
    //@Autowired
    private final StudentService studentService;

    @PostMapping("/student")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addStudent(@RequestBody StudentCredentialsDto student) {
        studentService.addStudent(student);
    }

    @GetMapping("/student/{id}")
    public StudentDto findStudent(@PathVariable Long id) {
        return studentService.findStudent(id);
    }

    @DeleteMapping("/student/{id}")
    public StudentDto removeStudent(@PathVariable Long id) {
        return studentService.removeStudent(id);
    }

    @PatchMapping("/student/{id}")
    public StudentCredentialsDto updateStudent(@PathVariable Long id, @RequestBody StudentUpdateDto studentUpdateDto) {
        return studentService.updateStudent(id, studentUpdateDto);
    }

    @PatchMapping("/score/student/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void addScore(@PathVariable Long id, @RequestBody ScoreDto scoreDto) {
        studentService.addScore(id, scoreDto);

    }

    @GetMapping("/students/name/{name}")
    public List<StudentDto> findStudentsByName(@PathVariable String name) {
        return studentService.findStudentsByName(name);
    }

    @GetMapping("/quantity/students")
    public Long countStudentsByNames(@RequestParam (name = "names") Set<String> name) {
        return studentService.countStudentsByNames(name);
    }

    @GetMapping("/students/exam/{examName}/minscore/{minScore}")
    public List<StudentDto> findStudentsByExamNameMinScore(@PathVariable String examName, @PathVariable Integer minScore) {
        return studentService.findStudentsByExamNameMinScore(examName, minScore);
    }
}
