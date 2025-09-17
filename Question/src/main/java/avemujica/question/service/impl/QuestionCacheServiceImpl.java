package avemujica.question.service.impl;

import avemujica.common.utils.RedisBloom;
import avemujica.question.entity.dto.Answer;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.service.QuestionCacheService;
import avemujica.question.service.QuestionService;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;


@Slf4j
@Service
public class QuestionCacheServiceImpl extends ServiceImpl<QuestionMapper,Question> implements QuestionCacheService{
    @Resource
    QuestionMapper mapper;
    @Resource
    StringRedisTemplate redis;
    @Resource
    RedissonClient redisson;
    @Resource
    @Qualifier("questionCacheManager")
    CacheManager cacheManager;
    @Resource
    RedisBloom redisBloom;
    @Resource
    QuestionService questionService;

    @Value("${custom.cache.redis-ttl-seconds}")
    private int ttl;
    @Value("${custom.cache.jitter-seconds}")
    private int jitter;
    @Value("${custom.cache.bloom.name}")
    private String BF;

    private String READY = "bf:question:ready:";

    private static final String KEY = "question:";

    @Cacheable(cacheManager = "questionCacheManager",
            cacheNames = "questionCache",
            key = "#id",
            unless = "#result == null")
    @Override
    public Question getQuestionById(long id){
        String key = KEY + id;
        Question fromRedis = getFromRedis(key);
        if(fromRedis != null)return fromRedis;
        if(isBloomReady() && !bfMightExist(key)){
            return null;
        }
        RLock lock = redisson.getLock("lock:question:"+ key);
        boolean locked = false;
        try{
            locked = lock.tryLock(100,3000, TimeUnit.MILLISECONDS);
            //返回，可能导致这次查询错误的无值，但只需要再次查询即可
            if(!locked)return getFromRedis(key);

            //拿到锁也再尝试一次
            Question cached = getFromRedis(key);
            if (cached != null) return cached;

            Question db = mapper.selectById(id);
            if(db == null){
                //如果为空可能是布隆过滤器失效，需要防止一下缓存穿透
                redis.opsForValue().set(key,"",Duration.ofSeconds(30));
                return null;
            }
            //加入redis过期时间抖动，防止缓存雪崩
            putToRedis(key,db,ttlWithJitter());
            //加入布隆,虽然我采取的形式是一次性加载布隆，理论上是不存在需要加入的数据的
            bfAdd(key);
            return db;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return null;
        }
        finally {
            //不要忘了解锁，不然每个请求都要赖3000ms
            if(locked)lock.unlock();
        }
    }

    @Transactional
    @Override
    public String addCacheQuestion(QuestionAddVO vo){
        Question q = questionService.addQuestion(vo);
        if(q == null) return "存在未知错误,请检查题目顺序是否冲突";
        String key = KEY + q.getId();
        putToRedis(key,q,ttlWithJitter());
        bfAdd(key);
        evictL1(Long.valueOf(q.getId()));
        return null;
    }

    @Transactional
    @Override
    public String updateByQuestionUpdateVO(QuestionUpdateVO vo){
        String res = updateById(new Question(vo)) ? null : "更新失败";
        if(res != null) return res;
        String key = KEY + vo.getId();

        //更新后删除redis里的数据
        deleteFromRedis(key);
        bfAdd(key);
        evictL1(Long.valueOf(vo.getId()));
        return null;
    }

    //删除会导致布隆过滤器出现漏洞，但属实影响不大，可以在有删改后重置一下
    @Transactional
    @Override
    public String deleteQuestion(long id){
        if(mapper.deleteById(id) != 1)return "删除失败";

        String key = KEY + id;
        deleteFromRedis(key);
        evictL1(id);
        return null;
    }



    private Question getFromRedis(String key) {
        String v = redis.opsForValue().get(key);
        if (v == null || v.isEmpty()) return null;
        try {
            return JSONObject.parseObject(v, Question.class);
        } catch (Exception e)
        {
            return null;
        }
    }

    private void putToRedis(String key, Question q, Duration d)  {
        try {
            redis.opsForValue().set(key, JSONObject.toJSONString(q), d);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void deleteFromRedis(String key) {
        try {
            redis.delete(key);
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    private boolean isBloomReady() {
        String f = redis.opsForValue().get(READY);
        return "1".equals(f);
    }

    private boolean bfMightExist(String key) {
        return redisBloom.existInBloomFilter(BF,key);
    }

    private void bfAdd(String key) {
        redisBloom.addInBloomFilter(BF,key);
    }

    private Duration ttlWithJitter() {
        int j = ThreadLocalRandom.current().nextInt(jitter + 1);
        return Duration.ofSeconds(ttl + j);
    }

    private void evictL1(Long id) {
        Cache c = cacheManager.getCache("questionCache");
        if (c != null) c.evict(id);
    }


}
