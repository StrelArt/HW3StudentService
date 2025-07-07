package telran.java58.student.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import telran.java58.student.dto.ScoreDto;
import telran.java58.student.dto.StudentCredentialsDto;
import telran.java58.student.dto.StudentDto;
import telran.java58.student.dto.StudentUpdateDto;
import telran.java58.student.dto.exeptions.ConflictException;
import telran.java58.student.dto.exeptions.NotFoundException;
import telran.java58.student.service.StudentService;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
public class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentService studentService;

    @Autowired
    private ObjectMapper objectMapper;

    private final Long studentId = 1000L;
    private final String name = "John";
    private final String password = "1234";
    private StudentDto studentDto;
    private StudentCredentialsDto studentCredentialsDto;

    @BeforeEach
    void setUp() {
        HashMap<String, Integer> scores = new HashMap<>();
        scores.put("Math", 95);
        scores.put("History", 85);

        studentDto = new StudentDto(studentId, name, scores);
        studentCredentialsDto = new StudentCredentialsDto(studentId, name, password);
    }

    @Test
    void testAddStudent() throws Exception {
        doNothing().when(studentService).addStudent(any(StudentCredentialsDto.class));

        mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).addStudent(any(StudentCredentialsDto.class));
    }

    @Test
    void testAddStudentConflict() throws Exception {
        doThrow(new ConflictException()).when(studentService).addStudent(any(StudentCredentialsDto.class));

        mockMvc.perform(post("/student")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(studentCredentialsDto)))
                .andExpect(status().isConflict());

        verify(studentService, times(1)).addStudent(any(StudentCredentialsDto.class));
    }

    @Test
    void testFindStudent() throws Exception {
        when(studentService.findStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.scores.Math").value(95))
                .andExpect(jsonPath("$.scores.History").value(85));

        verify(studentService, times(1)).findStudent(studentId);
    }

    @Test
    void testFindStudentNotFound() throws Exception {
        when(studentService.findStudent(studentId)).thenThrow(new NotFoundException());

        mockMvc.perform(get("/student/{id}", studentId))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).findStudent(studentId);
    }

    @Test
    void testRemoveStudent() throws Exception {
        when(studentService.removeStudent(studentId)).thenReturn(studentDto);

        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name));

        verify(studentService, times(1)).removeStudent(studentId);
    }

    @Test
    void testRemoveStudentNotFound() throws Exception {
        when(studentService.removeStudent(studentId)).thenThrow(new NotFoundException());

        mockMvc.perform(delete("/student/{id}", studentId))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).removeStudent(studentId);
    }

    @Test
    void testUpdateStudent() throws Exception {
        StudentUpdateDto updateDto = new StudentUpdateDto("Jane", "5678");
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateDto.class))).thenReturn(studentCredentialsDto);

        mockMvc.perform(patch("/student/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(studentId))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.password").value(password));

        verify(studentService, times(1)).updateStudent(eq(studentId), any(StudentUpdateDto.class));
    }

    @Test
    void testUpdateStudentNotFound() throws Exception {
        StudentUpdateDto updateDto = new StudentUpdateDto("Jane", "5678");
        when(studentService.updateStudent(eq(studentId), any(StudentUpdateDto.class))).thenThrow(new NotFoundException());

        mockMvc.perform(patch("/student/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).updateStudent(eq(studentId), any(StudentUpdateDto.class));
    }

    @Test
    void testAddScore() throws Exception {
        ScoreDto scoreDto = new ScoreDto("Physics", 90);
        doNothing().when(studentService).addScore(eq(studentId), any(ScoreDto.class));

        mockMvc.perform(patch("/score/student/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isNoContent());

        verify(studentService, times(1)).addScore(eq(studentId), any(ScoreDto.class));
    }

    @Test
    void testAddScoreStudentNotFound() throws Exception {
        ScoreDto scoreDto = new ScoreDto("Physics", 90);
        doThrow(new NotFoundException()).when(studentService).addScore(eq(studentId), any(ScoreDto.class));

        mockMvc.perform(patch("/score/student/{id}", studentId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(scoreDto)))
                .andExpect(status().isNotFound());

        verify(studentService, times(1)).addScore(eq(studentId), any(ScoreDto.class));
    }

    @Test
    void testFindStudentsByName() throws Exception {
        List<StudentDto> students = Arrays.asList(studentDto);
        when(studentService.findStudentsByName(name)).thenReturn(students);

        mockMvc.perform(get("/students/name/{name}", name))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByName(name);
    }

    @Test
    void testCountStudentsByNames() throws Exception {
        Set<String> names = Set.of(name, "Jane");
        when(studentService.countStudentsByNames(names)).thenReturn(2L);

        mockMvc.perform(get("/quantity/students")
                .param("names", name, "Jane"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string("2"));

        verify(studentService, times(1)).countStudentsByNames(names);
    }

    @Test
    void testFindStudentsByExamNameMinScore() throws Exception {
        String examName = "Math";
        Integer minScore = 90;
        List<StudentDto> students = Arrays.asList(studentDto);
        when(studentService.findStudentsByExamNameMinScore(examName, minScore)).thenReturn(students);

        mockMvc.perform(get("/students/exam/{examName}/minscore/{minScore}", examName, minScore))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(studentId))
                .andExpect(jsonPath("$[0].name").value(name));

        verify(studentService, times(1)).findStudentsByExamNameMinScore(examName, minScore);
    }
}
