package avemujica.question.service;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;


public interface QuestionService extends IService<Question> {
    String addQuestion(QuestionAddVO vo);
}
