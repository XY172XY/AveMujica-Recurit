package avemujica.question.entity.vo.request;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionUpdateVO {
    Integer id;
    String type;
    Map<String,Object> title;
    Map<String,Object> content;
    Integer originScore;
    LocalDateTime deadline;
    String difficulty;
    Integer questionOrder;
    Integer turn;
    String direction;
}
