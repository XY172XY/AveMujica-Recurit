package avemujica.question.entity.vo.response;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSelectByTurnOrDirection {
    @TableId(type = IdType.AUTO)
    Integer id;
    String type;
    Integer originScore;
    Integer currentScore;
    String firstBlood;
    LocalDateTime deadline;
    String difficulty;
    Integer questionOrder;
    Integer turn;
    String direction;
}
