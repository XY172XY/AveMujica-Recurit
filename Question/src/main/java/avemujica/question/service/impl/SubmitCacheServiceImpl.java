package avemujica.question.service.impl;

import avemujica.common.utils.RedisBloom;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.mapper.SubmitMapper;
import avemujica.question.service.SubmitCacheService;
import avemujica.question.service.SubmitService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@Service
public class SubmitCacheServiceImpl extends ServiceImpl<SubmitMapper, QuestionSubmit> implements SubmitCacheService {
    @Resource
    SubmitService submitService;
    @Resource
    RedisBloom redisBloom;
    @Resource
    SubmitMapper submitMapper;
    @Resource
    RedissonClient redissonClient;
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Value("${custom.cache.redis-ttl-seconds}")
    private int ttl;
    @Value("${custom.cache.jitter-seconds}")
    private int jitter;

    private static final String BF = "bf:submit:";
    private static final String READY = "bf:submit:ready:";
    private static final String KEY = "submit:";


    //提交的数据是由管理员改题后异步更新的,直接等缓存失效就ok
    @Cacheable(cacheManager = "submitCacheManager",
            cacheNames = "submitCache",
            key = "#id",
            unless = "#result == null")
    @Override
    public QuestionSubmit getSubmitById(Integer id) {
        String key = KEY + id;
        QuestionSubmit fromRedis = getFromRedis(key);
        if (fromRedis != null) return fromRedis;

        if(isBloomReady()&&!bfMightExist(key))return null;
        RLock lock = redissonClient.getLock("lock:submit:"+ key);
        boolean locked = false;
        try{
            locked = lock.tryLock(100,3000, TimeUnit.MILLISECONDS);
            if(!locked)return getFromRedis(key);

            QuestionSubmit db = submitMapper.selectById(id);
            if(db == null) {
                //代表布隆无效,给redis空值防止穿透
                stringRedisTemplate.opsForValue().set(key,"", Duration.ofSeconds(30));
                return null;
            }
            putToRedis(key,db,ttlWithJitter());
            bfADD(key);
            return db;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        finally {
            if(locked)lock.unlock();
        }
    }

    private QuestionSubmit getFromRedis(String key){
        String v = stringRedisTemplate.opsForValue().get(key);
        if(v == null || v.isEmpty()) return null;
        try {
            return JSONObject.parseObject(v, QuestionSubmit.class);
        }catch (Exception e){
            return null;
        }
    }

    private boolean bfMightExist(String key){
        return redisBloom.existInBloomFilter(BF,key);
    }

    private boolean isBloomReady(){
        String v = stringRedisTemplate.opsForValue().get(READY);
        return "1".equals(v);
    }

    private void putToRedis(String key,QuestionSubmit db,Duration d){
        try{
            stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(db), d);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private Duration ttlWithJitter(){
        int j = ThreadLocalRandom.current().nextInt(jitter+1);
        return Duration.ofSeconds(ttl + j);
    }

    private void bfADD(String key){
        redisBloom.addInBloomFilter(BF,key);
    }
}
