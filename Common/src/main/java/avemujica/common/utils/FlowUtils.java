package avemujica.common.utils;

import jakarta.annotation.Resource;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

//限流工具,结合Redis进行设计
@Service
@Component
public class FlowUtils {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    //简化代码使用，内部行为接口
    private interface Action{
        boolean run(boolean overlock);
    }

    //核心函数internalCheck
    private boolean Check(String key,int frequency,int period,Action action){
        if(stringRedisTemplate.hasKey(key)){
            //查看已经请求次数
            Long val = Optional.ofNullable(stringRedisTemplate.opsForValue().increment(key)).orElse(0L);
            //由临时Action决定是否封禁
            return action.run(val > frequency);
        }
        else {
            //标记为访问了一次
            stringRedisTemplate.opsForValue().set(key,"1",period, TimeUnit.SECONDS);
            return true;
        }
    }

    //直接封禁操作，用于强制留出单次访问的时间间隔
    //blockTime 封禁时间 单位为Seconds
    public boolean limitCheck(String key,int blockTime){
        return this.Check(key,1,blockTime,(overlock)->false);
    }

    //自动升级的封禁时长
    //此函数的frequency要大于limitCheck
    public boolean limitUpgradeCheck(String key,int frequency,int baseTime,int upgradeTime){
        return this.Check(key,frequency,baseTime,(overlock)->{
            if(overlock) {
                //如果发现val>frequency会延长封禁时间
                stringRedisTemplate.opsForValue().set(key, "1", upgradeTime, TimeUnit.SECONDS);
            }
            return false;
        });
    }

    //访问频繁封禁
    public boolean limitPeriodCheck(String countKey,String blockKey,int blockTime,int frequency,int period){
        return this.Check(countKey,frequency,period,(overlock)->{
            if(overlock) {
                stringRedisTemplate.opsForValue().set(blockKey,"",blockTime,TimeUnit.SECONDS);
            }
            return !overlock;
        });
    }
}
