package avemujica.question.entity.dto;

import avemujica.common.entity.BaseData;
import avemujica.common.typeHandler.JsonbToObjectHandler;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName(value = "am_question")
public class Question implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String type;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,Object> title;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,Object> content;
    Integer originScore;
    Integer currentScore;
    String firstBlood;
    LocalDateTime deadline;
    String difficulty;
    Integer questionOrder;
    Integer turn;
    String direction;

    public Question(QuestionAddVO vo){
        this.type=vo.getType();
        this.title=vo.getTitle();
        this.content=vo.getContent();
        this.originScore=vo.getOriginScore();
        this.currentScore=this.originScore;
        this.deadline=vo.getDeadline();
        this.difficulty=vo.getDifficulty();
        this.questionOrder=vo.getQuestionOrder();
        this.turn=vo.getTurn();
    }

    public Question(QuestionUpdateVO vo){
        this.id = vo.getId();
        this.type=vo.getType();
        this.title=vo.getTitle();
        this.content=vo.getContent();
        this.originScore=vo.getOriginScore();
        this.deadline=vo.getDeadline();
        this.difficulty=vo.getDifficulty();
        this.questionOrder=vo.getQuestionOrder();
        this.turn=vo.getTurn();
    }
}
