package avemujica.question.job;

import avemujica.common.utils.RedisBloom;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.mapper.SubmitMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class BloomBackfillJob {
    @Resource
    QuestionMapper questionMapper;

    @Resource
    SubmitMapper submitMapper;

    @Resource
    RedisBloom redisBloom;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    private final String BF_QUESTION = "bf:question:";
    private final String BF_SUBMIT = "bf:submit:";

    private final String READY_QUESTION_KEY = "bf:question:ready:";
    private final String READY_SUBMIT_KEY = "bf:submit:ready:";
    private final int CAPACITY = 1000;
    private final float ERROR_RATE = 0.05f;

    @PostConstruct
    public void reserveIfNeeded() {
        log.info("创建布隆过滤器,如果需要");
        if(redisBloom.tryInitBloomFilter(BF_QUESTION,CAPACITY,ERROR_RATE)){
            log.info("创建题目布隆成功");
        }
        else {
            log.info("题目布隆过滤器已存在");
        }
        if (redisBloom.tryInitBloomFilter(BF_SUBMIT,CAPACITY,ERROR_RATE)){
            log.info("创建提交布隆成功");
        }
        else{
            log.info("提交布隆过滤器已存在");
        }
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady(){
        backfillAllQuestionsId();
        backfillAllSubmitId();
    }

    private void backfillAllQuestionsId() {
        long lastId = 0;
        int batch = 100;

        while(true){
            List<Question> rows = questionMapper.selectList(
                    new LambdaQueryWrapper<Question>()
                            .gt(Question::getId, lastId)
                            .orderByAsc(Question::getId)
                            .last("LIMIT " + batch)
            );

            if(rows.isEmpty())break;
            for (Question q : rows) {
                String key = "question:" + q.getId();
                redisBloom.addInBloomFilter(BF_QUESTION,key);
            }

            lastId = rows.get(rows.size()-1).getId();
        }
        stringRedisTemplate.opsForValue().set(READY_QUESTION_KEY, "1");
    }

    private void backfillAllSubmitId(){
        long lastId = 0;
        int batch = 100;

        while(true){
            List<QuestionSubmit> rows = submitMapper.selectList(
                    new LambdaQueryWrapper<QuestionSubmit>()
                            .gt(QuestionSubmit::getId,lastId)
                            .orderByAsc(QuestionSubmit::getId)
                            .last("LIMIT "+batch)
            );

            if(rows.isEmpty())break;
            for (QuestionSubmit q : rows) {
                String key = "submit:" + q.getId();
                redisBloom.addInBloomFilter(BF_SUBMIT,key);
            }

            lastId = rows.get(rows.size()-1).getId();
        }
        stringRedisTemplate.opsForValue().set(READY_SUBMIT_KEY, "1");
    }

}
