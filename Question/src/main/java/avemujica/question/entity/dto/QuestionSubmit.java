package avemujica.question.entity.dto;

import avemujica.common.entity.BaseData;
import avemujica.question.entity.vo.request.QuestionSubmitVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain=true)
@TableName("am_submit")
public class QuestionSubmit implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    Integer questionId;
    LocalDateTime timeRecord;
    Integer score;
    Integer count;
    String fileName;

    public QuestionSubmit(QuestionSubmitVO vo) {
        id = vo.getId();
        userId = vo.getUserId();
        questionId = vo.getQuestionId();
    };
}
