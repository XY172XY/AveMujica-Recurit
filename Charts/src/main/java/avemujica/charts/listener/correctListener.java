package avemujica.charts.listener;

import avemujica.common.entity.dto.QuestionScore;
import avemujica.common.entity.dto.Score;
import avemujica.common.mapper.QuestionScoreMapper;
import avemujica.common.mapper.ScoreMapper;
import avemujica.common.service.ScoreService;
import avemujica.charts.utils.ItemDecay;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.mapper.QuestionMapper;
import avemujica.question.mapper.SubmitMapper;
import avemujica.usermanage.entity.dto.Account;
import avemujica.usermanage.mapper.AccountMapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@RabbitListener(queues = "correctQueue")
@Component
@Slf4j
public class correctListener {

    @Resource
    AccountMapper accountMapper;
    @Resource
    SubmitMapper submitMapper;

    @Resource
    QuestionScoreMapper questionScoreMapper;

    @Resource
    ScoreMapper scoreMapper;

    @Resource
    ScoreService scoreService;

    @Resource
    QuestionMapper questionMapper;

    private final String KEY = "chart_list:";


    //监听器负责更新题目提交情况,衰减因子,用户成绩单
    @Transactional
    @RabbitHandler
    public void updateQuestionScore(Map<String,String> map) {
        Integer questionId = Integer.parseInt(map.get("questionId"));
        Integer submitId = Integer.parseInt(map.get("submitId"));
        synchronized (questionId.toString().intern()) {
            int submitCount = submitMapper.countForQuestion(questionId);
            Question q = questionMapper.selectById(questionId);
            QuestionSubmit qst = submitMapper.selectOne(new QueryWrapper<QuestionSubmit>().eq("id", submitId));
            QuestionScore qs = questionScoreMapper.selectOne(new QueryWrapper<QuestionScore>().eq("question_id",questionId));
            if(qs==null)qs = new QuestionScore()
                    .setQuestionId(questionId)
                    .setOriginScore(q.getOriginScore());


            Score score = scoreMapper.selectOne(new QueryWrapper<Score>().eq("user_id",qst.getUserId()));
            if(score==null) {
                Account account = accountMapper.selectById(qst.getUserId());
                score = new Score().setUserId(account.getId()).setUsername(account.getUsername());
            }


            int oldMean = qs.getAverage();
            int oldStd = qs.getStd();
            int oldCount = qs.getSubmitCount();
            int originScore = qs.getOriginScore();

            if (submitCount - qs.getSubmitCount() == 1 || qs.getSubmitCount() == submitCount) {
                qs.setSubmitCount(submitCount);
            } else {
                //管理员在短时间内批改同一道题,会导致未更新条目堆积超过1条
                qs.setSubmitCount(qs.getSubmitCount() + 1);
            }
            int newMean = (oldMean * oldCount + qst.getScore()) / (oldCount + 1);
            int newStd = (int) calculateNewStd(oldCount, oldMean, oldStd, qst.getScore());
            qs.setAverage(newMean);
            qs.setStd(newStd);

            ItemDecay.Params params = new ItemDecay.Params();
            qs.setDecline((float) ItemDecay.decay(newMean/(float)originScore,newStd/(float)originScore,qs.getSubmitCount(),params));

            score.getScores().put(qst.getQuestionId().toString(),qst.getScore());


            scoreService.saveOrUpdate(score);
            questionScoreMapper.updateById(qs);
            submitMapper.updateById(qst);
            updateRedis(questionId,q.getDirection());
        }
    }

    private void updateRedis(Integer questionId,String direction) {
        QuestionScore qs = questionScoreMapper.selectOne(new QueryWrapper<QuestionScore>().eq("question_id", questionId));
        if(qs == null){
            log.error("排行榜更新异常,异常题目信息{}:",questionId);
            throw new IllegalArgumentException("题目未更新");
        }
        float decline = qs.getDecline();

        long lastId = 0;
        int batch = 100;
        while(true){
            List<Score> rows = scoreMapper.selectList(
                    new LambdaQueryWrapper<Score>()
                            .gt(Score::getId, lastId)
                            .orderByAsc(Score::getId)
                            .last("LIMIT " + batch)
            );
            if(rows.isEmpty())break;
            for (Score q : rows) {
                if(q.getScores() == null) continue;
                q.getScores().putIfAbsent(questionId.toString(), 0);
                q.getProducedScores().putIfAbsent(questionId.toString(), 0);
                q.getDirectionScores().putIfAbsent(direction, 0);

                //原始为转化分
                int origin = q.getScores().get(questionId.toString());
                //原始转化后总分
                int total = q.getTotalScore();
                //原始转化后分
                int produced = q.getProducedScores().get(questionId.toString());
                //原始方向总分
                int directionProduced = q.getProducedScores().get(direction);

                //更新处理后题目分
                q.getProducedScores().put(questionId.toString(), (int)(origin * decline));
                //更新转化后总分
                q.setTotalScore((int) (total - produced + origin * decline));
                //更新方向总分
                q.getDirectionScores().put(direction, directionProduced - produced + (int)(origin * decline));
            }
            scoreService.updateBatchById(rows);
            lastId = rows.get(rows.size()-1).getId();
        }
    }


    //计算均值
    static double mean(List<Integer> values) {
        if (values == null || values.isEmpty()) return 0.0;
        double sum = 0.0;
        for (double v : values) sum += v;
        return sum / (double) values.size();
    }

    //由旧数据计算新的标准差
    public static double calculateNewStd(int nOld,
                                         int meanOld,
                                         int stdDevOld,
                                         int newData) {
        if (nOld == 0) {
            return 0.0;
        }
        // 计算旧数据的总和、平方和
        double sumOld = meanOld * nOld; // 旧总和 = 均值 × 数量
        double varianceOld = Math.pow(stdDevOld, 2); // 旧方差 = 标准差²
        double ssOld = varianceOld * nOld + (sumOld * sumOld) / nOld; // 旧平方和
        // 更新统计量（加入新数据）
        int nNew = nOld + 1; // 新数量
        double sumNew = sumOld + newData; // 新总和
        double ssNew = ssOld + Math.pow(newData, 2); // 新平方和
        double meanNew = sumNew / nNew; // 新均值
        // 计算新总体方差和标准差
        double varianceNew = (ssNew - (sumNew * sumNew) / nNew) / nNew;
        return Math.sqrt(varianceNew); // 标准差 = 方差的平方根
    }


}
