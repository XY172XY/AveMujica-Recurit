package avemujica.question.service.impl;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.service.QuestionService;
import com.alibaba.fastjson2.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    @Transactional
    public String addQuestion(QuestionAddVO vo){
        Question question = new Question(vo);
        System.out.println(JSON.toJSONString(question));

        Question exit = this.query()
                .select("id")
                .eq("question_order",vo.getQuestionOrder())
                .eq("turn",vo.getTurn())
                .one();
        if(exit != null){
            return "此轮次的该序号题目已经存在，请调整轮次或序号";
        }
        if(this.save(question)){
            return null;
        }
        return "保存失败，未知错误";
    }
}
