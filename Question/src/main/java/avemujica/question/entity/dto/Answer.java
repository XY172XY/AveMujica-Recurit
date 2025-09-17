package avemujica.question.entity.dto;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
@TableName(value = "am_answer",autoResultMap = true)
public class Answer {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer questionId;
    String flagAnswer;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,String> choiceAnswer;
}
