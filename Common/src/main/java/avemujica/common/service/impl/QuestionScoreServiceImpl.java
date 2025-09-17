package avemujica.common.service.impl;

import avemujica.common.entity.dto.QuestionScore;
import avemujica.common.mapper.QuestionScoreMapper;
import avemujica.common.service.QuestionScoreService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

@Service
public class QuestionScoreServiceImpl extends ServiceImpl<QuestionScoreMapper, QuestionScore> implements QuestionScoreService {
}
