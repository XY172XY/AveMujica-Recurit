package avemujica.question.entity.vo.request;

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
    @NotBlank
    String type;
    Map<String,Object> title;
    Map<String,Object> content;
    Integer originScore;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    LocalDateTime deadline;
    String difficulty;
    Integer questionOrder;
    Integer turn;
}
