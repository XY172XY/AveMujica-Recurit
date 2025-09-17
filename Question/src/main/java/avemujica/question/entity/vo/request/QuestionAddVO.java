package avemujica.question.entity.vo.request;

import avemujica.question.entity.dto.Answer;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
public class QuestionAddVO {
    String type;
    Map<String,Object> title;
    Map<String,Object> content;
    Integer originScore;
    LocalDateTime deadline;
    String difficulty;
    Integer questionOrder;
    Integer turn;
    String direction;
    Answer answer;
}
