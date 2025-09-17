package avemujica.question.service.impl;

import avemujica.common.entity.dto.QuestionScore;
import avemujica.common.mapper.QuestionScoreMapper;
import avemujica.question.entity.dto.Answer;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.entity.vo.response.QuestionDetail;
import avemujica.question.mapper.AnswerMapper;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import jakarta.annotation.Resource;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {
    @Resource
    QuestionScoreMapper questionScoreMapper;

    @Resource
    AnswerMapper answerMapper;


    @Override
    public Question addQuestion(QuestionAddVO vo){
        Question question = new Question(vo);
        Question exit = this.query()
                .select("id")
                .eq("question_order",vo.getQuestionOrder())
                .eq("turn",vo.getTurn())
                .eq("direction",vo.getDirection())
                .one();
        if(exit != null){
            return null;
        }
        if(this.save(question)){
            Answer answer = vo.getAnswer();
            answer.setQuestionId(question.getId());
            answerMapper.insert(answer);
            QuestionScore questionScore = new QuestionScore()
                    .setOriginScore(vo.getOriginScore())
                    .setQuestionId(question.getId());
            questionScoreMapper.insert(questionScore);
            return question;
        }
        return null;
    }


    //弃用,使用TypeHandler,set方法无法正确解析map
    @Override
    public String updateQuestion(QuestionUpdateVO vo){
        Question question = new Question(vo);
        UpdateWrapper<Question> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id",question.getId())
                .set(!StringUtil.isNullOrEmpty(question.getType()),"type",question.getType())
                .set(question.getTitle() != null,"title",question.getTitle())
                .set(question.getContent() != null,"content",question.getTitle())
                .set(question.getOriginScore() != null,"origin_score",question.getOriginScore())
                .set(question.getDeadline() != null,"deadline",question.getDeadline())
                .set(!StringUtil.isNullOrEmpty(question.getDifficulty()),"difficulty",question.getDifficulty())
                .set(question.getQuestionOrder() != null,"question_order",question.getTitle())
                .set(question.getTurn() != null,"turn",question.getTurn())
                .set(question.getDirection() != null,"direction",question.getDirection());
        if(this.update(updateWrapper)){
            return null;
        }else{
            return "更新失败，请重试";
        }
    }


    //由于查询字段的特殊性,想去维护数据一致性需要很多额外的工作,但事实上不是很需要,三分钟的更新间隔是可以接受的
    @Override
    @Cacheable(cacheManager = "turnDirectionCacheManager",
            cacheNames = "turnDirectionCache",
            key = "#direction+'_'+#turn+'_'+#page",
            unless = "#result == null")
    public List<Question> selectByTurnOrDirections(String direction, Integer turn, Integer page){
        //限制只有一个请求能到达数据库,当然,这有极小的可能会漏一些
        synchronized ((direction + turn + page).intern()) {
            IPage<Question> iPage = new Page<>(page, 10);
            QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
            queryWrapper.select("id", "origin_score", "current_score", "first_blood", "deadline", "difficulty", "question_order", "turn", "direction", "question_order")
                    .eq(turn != null, "turn", turn)
                    .eq(direction != null, "direction", direction)
                    .orderByAsc(turn != null, "turn")
                    .orderByAsc(direction != null, "direction");
            IPage<Question> questionPage = this.page(iPage, queryWrapper);
            return questionPage.getRecords();
        }
    }

    @Override
    public QuestionDetail selectDetailById(Integer id){
        Question question = getById(id);
        if(question == null) return null;
        System.out.println(question);
        return question.asViewObject(QuestionDetail.class);
    }
}
