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
public class QuestionDetail {
    Integer id;
    String type;
    Map<String,Object> title;
    Map<String,Object> content;
    Integer originScore;
    Integer currentScore;
    LocalDateTime deadline;
}
