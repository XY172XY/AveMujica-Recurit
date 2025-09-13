package avemujica.question.service.impl;

import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.mapper.SubmitMapper;
import avemujica.question.service.CorrectService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class CorrectServiceImpl implements CorrectService {
    @Resource
    SubmitMapper submitMapper;

    @Override
    public String correct(Integer submitId, Integer score) {
        if(submitMapper.update(new UpdateWrapper<QuestionSubmit>()
                .set("score",score)
                .eq("id",submitId)) == 1){
            return null;
        }
        return "更新失败";
    }
}
