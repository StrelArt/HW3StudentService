package telran.java58.student.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
@EqualsAndHashCode(of = "id")
public class Student {
    private long id;
    @Setter
    private String name;
    @Setter
    private String password;
    private Map<String, Integer> scores =  new HashMap<>();

    public Student(long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public void addScore(String examName, int score){
        scores.put(examName, score);
    }
}
