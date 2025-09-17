package avemujica.charts.controller;

import avemujica.common.entity.dto.Score;
import avemujica.common.service.ScoreService;
import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.RestBean;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ChartController {

    @Resource
    ScoreService scoreService;

    @GetMapping("/charts")
    @CheckNonce
    public RestBean<List<Score>> getChart(@RequestParam(required = false) String direction,
                                          @RequestParam Integer page,
                                          @RequestParam Integer size,
                                          @RequestHeader("X-Nonce") String nonce,
                                          @RequestHeader("X-Timestamp") Long timestamp) {
        if(page == null || size == null) {
            throw new IllegalArgumentException();
        }
        List<Score> scores = scoreService.getScores(direction,page,size);
        return scores == null ? RestBean.success(null) : RestBean.success(scores);
    }
}
