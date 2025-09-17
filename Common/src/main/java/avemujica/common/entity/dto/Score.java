package avemujica.common.entity.dto;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@Accessors(chain=true)
@TableName(value = "am_score",autoResultMap = true)
public class Score {
    @TableId(type = IdType.AUTO)
    Integer id;
    Integer userId;
    String username;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,Integer> scores;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,Integer> producedScores;
    @TableField(typeHandler = JsonbToObjectHandler.class)
    Map<String,Integer> directionScores;
    Integer totalScore;

    public Score(){
        scores = new HashMap<>();
        producedScores = new HashMap<>();
        directionScores = new HashMap<>();
        totalScore = 0;
    }


}
