package avemujica.question.mapper;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.dto.QuestionSubmit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface SubmitMapper extends BaseMapper<QuestionSubmit> {

    @Select("SELECT COUNT(*) FROM am_submit WHERE question_id = #{questionId}")
    int countForQuestion(Integer questionId);
}
