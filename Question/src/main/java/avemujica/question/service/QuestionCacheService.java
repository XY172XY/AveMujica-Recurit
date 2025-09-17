package avemujica.question.service;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import com.baomidou.mybatisplus.extension.service.IService;

public interface QuestionCacheService extends IService<Question> {
    Question getQuestionById(long id);

    String addCacheQuestion(QuestionAddVO vo);

    String updateByQuestionUpdateVO (QuestionUpdateVO vo);

    //删除会导致布隆过滤器出现漏洞，但属实影响不大，可以在有删改后重置一下
    String deleteQuestion(long id);
}
