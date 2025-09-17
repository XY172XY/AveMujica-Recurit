package avemujica.common.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@Accessors(chain=true)
@TableName("am_question_score")
public class QuestionScore {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer questionId;
    Integer originScore;
    Integer submitCount;
    Integer average;
    Integer std;
    float decline;

    public QuestionScore(){
        submitCount=0;
        average=0;
        std=0;
        decline=1;
    }
}
