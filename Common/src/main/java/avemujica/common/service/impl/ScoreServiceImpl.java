package avemujica.common.service.impl;

import avemujica.common.entity.dto.Score;
import avemujica.common.mapper.ScoreMapper;
import avemujica.common.service.ScoreService;
import com.alibaba.fastjson2.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class ScoreServiceImpl extends ServiceImpl<ScoreMapper, Score> implements ScoreService {
    @Resource
    StringRedisTemplate redis;
    @Resource
    RedissonClient redisson;
    @Resource
    ScoreMapper scoreMapper;

    @Value("${custom.cache.redis-ttl-seconds}")
    private int ttl;
    @Value("${custom.cache.jitter-seconds}")
    private int jitter;

    private static final String KEY = "chart:";

    @Cacheable(cacheNames = "scoreCache",
            key = "(#direction?:'null')+'_'+#page+'_'+#size",
            unless = "#result == null")
    @Override
    public List<Score> getScores(String direction, Integer page, Integer size) {
        String key = KEY + direction + page;
        List<Score> scores = getFromRedis(key);
        if (scores != null) return scores;


        RLock lock = redisson.getLock("lock:chart:"+ key);
        boolean locked = false;
        try{
            locked = lock.tryLock(100,3000, TimeUnit.MILLISECONDS);
            //返回，可能导致这次查询错误的无值，但只需要再次查询即可
            if(!locked)return getFromRedis(key);

            List<Score> cached = getFromRedis(key);
            if (cached != null) return cached;

            IPage<Score> ipage = new Page<>(page,size);
            List<Score> db;
            if(direction == null){
                db = page(ipage,new QueryWrapper<Score>()
                        .orderByAsc("total_score")).getRecords();
            }
            else{
                db = scoreMapper.getScoresChartByDirection(direction,size,(page-1)*size);
            }
            if(db == null){
                redis.opsForValue().set(key,"",Duration.ofSeconds(30));
                return null;
            }
            putToRedis(key,db,ttlWithJitter());
            return db;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(e);
        }
        finally{
            lock.unlock();
        }
    }

    private List<Score> getFromRedis(String key) {
        String v = redis.opsForValue().get(key);
        if (v == null || v.isEmpty()) return null;
        try {
            return JSONArray.parseArray(v,Score.class);
        } catch (Exception e)
        {
            return null;
        }
    }

    private void putToRedis(String key, List<Score> s, Duration d)  {
        try {
            redis.opsForValue().set(key, JSONArray.toJSONString(s), d);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Duration ttlWithJitter() {
        int j = ThreadLocalRandom.current().nextInt(jitter + 1);
        return Duration.ofSeconds(ttl + j);
    }
}
