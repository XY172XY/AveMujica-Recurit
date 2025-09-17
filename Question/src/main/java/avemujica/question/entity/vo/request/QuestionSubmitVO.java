package avemujica.question.entity.vo.request;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionSubmitVO {
    Integer id;
    Integer userId;
    Integer questionId;
    Integer originScore;
    String flag;
    Map<String,String> options;
}
