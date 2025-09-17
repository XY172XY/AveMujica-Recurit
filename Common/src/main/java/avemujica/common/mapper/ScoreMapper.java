package avemujica.common.mapper;

import avemujica.common.entity.dto.Score;
import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.type.JdbcType;

import java.util.List;
import java.util.Locale;

@Mapper
public interface ScoreMapper extends BaseMapper<Score> {


    @Select({
                    "SELECT user_id,username,direction_scores " +
                    "FROM am_score WHERE jsonb_exists(direction_scores, #{direction}) " +
                    "AND (direction_scores ->> #{direction})~ '^[0-9]+$' " +
                    "ORDER BY (direction_scores ->> #{direction})::int NULLS LAST,id " +
                    "LIMIT #{limit} OFFSET #{offset}"
    })
    @Results({
            @Result(column = "user_id",property = "userId"),
            @Result(column = "username",property = "username"),
            @Result(column = "direction_scores",property = "directionScores",jdbcType = JdbcType.OTHER,typeHandler = JsonbToObjectHandler.class)
    })
    List<Score> getScoresChartByDirection(@Param("direction") String direction,@Param("limit") Integer limit,@Param("offset") Integer offset);
}
