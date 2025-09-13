package avemujica.question.service;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.entity.vo.response.QuestionDetail;
import avemujica.question.entity.vo.response.QuestionSelectByTurnOrDirection;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.stereotype.Service;

import java.util.List;


public interface QuestionService extends IService<Question> {
    String addQuestion(QuestionAddVO vo);

    String updateQuestion(QuestionUpdateVO vo);

    List<Question> selectByTurnOrDirections(String direction, String turn, Integer page);

    QuestionDetail selectDetailById(Integer id);
}
