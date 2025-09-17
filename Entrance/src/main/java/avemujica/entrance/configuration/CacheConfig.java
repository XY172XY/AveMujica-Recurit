package avemujica.entrance.configuration;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.cache.support.CompositeCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean("questionCacheManager")
    public CacheManager questionCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("questionCache");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(60))
                .maximumSize(1000));
        return manager;
    }

    @Bean("submitCacheManager")
    public CacheManager submitCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("submitCache");
        manager.setCaffeine(Caffeine.newBuilder()
        .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(1000));
        return manager;
    }

    @Bean("turnDirectionCacheManager")
    public CacheManager turnDirectionCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("turnDirectionCache");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(5))
                .maximumSize(1000));
        return manager;
    }

    @Bean("scoreCacheManager")
    public CacheManager scoreCacheManager() {
        CaffeineCacheManager manager = new CaffeineCacheManager("scoreCache");
        manager.setCaffeine(Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(1))
                .maximumSize(1000));
        return manager;
    }

    @Primary
    @Bean
    public CompositeCacheManager compositeCacheManager(
            @Qualifier("questionCacheManager") CacheManager q,
            @Qualifier("turnDirectionCacheManager")CacheManager t,
            @Qualifier("submitCacheManager")CacheManager s,
            @Qualifier("scoreCacheManager")CacheManager sc
    ) {
        return new CompositeCacheManager(q,t,s,sc);
    }
}
