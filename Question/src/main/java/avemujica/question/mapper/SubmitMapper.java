package avemujica.question.mapper;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.dto.QuestionSubmit;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.IService;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SubmitMapper extends BaseMapper<QuestionSubmit> {
}
