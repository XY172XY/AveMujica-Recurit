package avemujica.common.service;

import avemujica.common.entity.dto.Score;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface ScoreService extends IService<Score> {
    List<Score> getScores(String direction, Integer page, Integer size);
}
