package avemujica.question.service.impl;

import avemujica.common.utils.Const;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.mapper.SubmitMapper;
import avemujica.question.service.CorrectService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CorrectServiceImpl implements CorrectService {
    @Resource
    SubmitMapper submitMapper;

    @Resource
    RabbitTemplate rabbitTemplate;

    @Override
    public String correct(Integer submitId, Integer score,Integer questionId) {
        if(submitMapper.update(new UpdateWrapper<QuestionSubmit>()
                .set("score",score)
                .eq("id",submitId)) == 1){

            Map<String,String> map = Map.of("questionId",questionId.toString(),"submitId",submitId.toString());
            rabbitTemplate.convertAndSend(Const.MQ_CORRECT,"correctQueue",map);

            return null;
        }
        return "更新失败";
    }
}
