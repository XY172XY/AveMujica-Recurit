package avemujica.question.service.impl;

import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.entity.vo.response.QuestionDetail;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.service.QuestionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.netty.util.internal.StringUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class QuestionServiceImpl extends ServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Override
    public String addQuestion(QuestionAddVO vo){
        Question question = new Question(vo);
//        System.out.println(JSON.toJSONString(question));
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

    @Override
    public List<Question> selectByTurnOrDirections(String direction, String turn, Integer page){
        IPage<Question> iPage = new Page<>(page,10);
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","origin_score","current_score","first_blood","deadline","difficulty","question_order","turn","direction","question_order")
                .eq(turn != null,"turn",turn)
                .eq(direction != null,"direction",direction)
                .orderByAsc(turn != null,"turn")
                .orderByAsc(direction  != null,"direction");
        IPage<Question> questionPage = this.page(iPage,queryWrapper);
        return questionPage.getRecords();
    }

    @Override
    public QuestionDetail selectDetailById(Integer id){
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("id","type","title","content","origin_score","current_score","deadline")
                .eq("question_order",id);
        Question question = this.getOne(queryWrapper);
        if(question == null) return null;
        return question.asViewObject(QuestionDetail.class);
    }


//    Integer id;
//    String type;
//    Map<String,Object> title;
//    Map<String,Object> content;
//    Integer originScore;
//    Integer currentScore;
//    LocalDateTime deadline;

}
