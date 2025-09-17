package avemujica.question.service;

import avemujica.question.entity.dto.QuestionSubmit;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.Cacheable;

public interface SubmitCacheService extends IService<QuestionSubmit> {
    @Cacheable(cacheManager = "submitCacheManager",
            cacheNames = "submitCache",
            key = "#id",
            unless = "#result == null")
    QuestionSubmit getSubmitById(Integer id);
}
