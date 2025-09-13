package avemujica.question.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
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
@TableName("am_answer")
public class Answer {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer questionId;
    String flagAnswer;
    Map<String,String> choiceAnswer;
}
