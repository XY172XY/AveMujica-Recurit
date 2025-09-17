package avemujica.charts.utils;

import avemujica.common.entity.dto.Score;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class ChartUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    private static final String KEY = "score:";

    public void addOrUpdateScore(Score score){
        String key = KEY + score.getUserId();
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(score));
    }

    public Score getScore(String userId){
        String key = KEY + userId;
        return JSONObject.parseObject(stringRedisTemplate.opsForValue().get(key),Score.class);
    }
}
