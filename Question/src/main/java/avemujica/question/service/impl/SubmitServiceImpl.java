package avemujica.question.service.impl;

import avemujica.common.utils.Const;
import avemujica.common.utils.FlowUtils;
import avemujica.question.entity.dto.Answer;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.entity.vo.request.QuestionSubmitVO;
import avemujica.question.entity.vo.response.SubmitRecord;
import avemujica.question.mapper.AnswerMapper;
import avemujica.question.mapper.SubmitMapper;
import avemujica.question.service.SubmitService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class SubmitServiceImpl extends ServiceImpl<SubmitMapper, QuestionSubmit> implements SubmitService {
    @Resource
    AnswerMapper answerMapper;

    @Resource
    FlowUtils  flowUtils;

    @Resource
    MinioClient minioClient;

    @Value("${spring.web.submit.limit}")
    int submitLimit;

    @Value("${spring.web.file.limit}")
    int fileLimit;

    @Override
    @Transactional
    public String submitFlag(QuestionSubmitVO vo, String address){
            if(verifyLimit(address,submitLimit)){
                return "请勿频繁提交";
            }
        QuestionSubmit questionSubmit = new QuestionSubmit(vo);
            QuestionSubmit lastSubmit = getLastSubmit(vo.getUserId(),vo.getQuestionId());
            questionSubmit.setCount(lastSubmit != null ? lastSubmit.getCount() + 1 : 1);
            Answer answer = answerMapper.selectOne(new QueryWrapper<Answer>()
                    .select("flag_answer")
                    .eq("question_id",vo.getQuestionId()));
            if(answer == null) return "bad request: answer not exist";
            if(answer.getFlagAnswer().equals(vo.getFlag())) questionSubmit.setScore(vo.getOriginScore());
            else questionSubmit.setScore(0);
            if(this.saveOrUpdate(questionSubmit)) return null;
            return "保存失败";
    }

    @Transactional
    @Override
    public String submitChoice(QuestionSubmitVO vo, String address){
        if(verifyLimit(address,submitLimit)){
            return "请勿频繁提交";
        }
        QuestionSubmit questionSubmit = new QuestionSubmit(vo);
        QuestionSubmit lastSubmit = getLastSubmit(vo.getUserId(),vo.getQuestionId());
        questionSubmit.setCount(lastSubmit != null ? lastSubmit.getCount() + 1 : 1);
        Answer answer = answerMapper.selectOne(new QueryWrapper<Answer>()
                .select("choice_answer")
                .eq("question_id",vo.getQuestionId()));
        if(answer == null) return "bad request: answer not exist";

        StringBuffer sb = new StringBuffer();
        int cnt = judgeScore(answer.getChoiceAnswer(),vo.getOptions(),sb);
        float percentage = cnt / (float) questionSubmit.getCount();
        questionSubmit.setScore((int)(percentage*vo.getOriginScore()));
        if(this.saveOrUpdate(questionSubmit)) return sb.toString();
        return "保存失败";
    }

    @Override
    public String submitMaterial(QuestionSubmitVO vo, String address, MultipartFile file){
        synchronized (address.intern()) {
            if (!verifyLimit(address, fileLimit)) {
                return "请勿频繁提交";
            }
            QuestionSubmit questionSubmit = new QuestionSubmit()
                    .setId(vo.getId())
                    .setQuestionId(vo.getQuestionId())
                    .setUserId(vo.getUserId());
            QuestionSubmit lastSubmit = getLastSubmit(vo.getUserId(), vo.getQuestionId());
            questionSubmit.setCount(lastSubmit != null ? lastSubmit.getCount() + 1 : 1);
            questionSubmit.setScore(0);
            String res = upload(questionSubmit ,file, vo.getUserId(), vo.getQuestionId());
            if (!this.saveOrUpdate(questionSubmit)) return "提交失败";
            return res;
        }
    }

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("zip", "pdf" , "rar" , "7z");
    private static final int FILE_LENGTH = 100 * 1024 * 1024;

    //这是一个同步的上传,设置了1分钟的限流
    private String upload(QuestionSubmit qs,MultipartFile file,Integer userid,Integer questionId){
        String originalFilename = file.getOriginalFilename();
        if (file.getSize() > FILE_LENGTH) { // 100MB
            return "文件过大";
        }

        if (originalFilename == null || !originalFilename.contains(".")) {
            return "非法文件名";
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1).toLowerCase();
        String objectName = userid + "_" + questionId + "." + ext;
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            return "不支持的文件类型: " + ext;
        }

        qs.setFileName(objectName);

        String contentType = Optional.ofNullable(file.getContentType())
                .orElse("application/octet-stream");
        try (InputStream in = file.getInputStream()) {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket("submit")
                            .object(objectName)
                            .contentType(contentType)
                            .stream(in, file.getSize(), file.getSize() > 0 ? file.getSize() : -1)
                            .build()
            );
        } catch (Exception e) {
            log.error("上传文件[{}]失败: {}", objectName ,e.getMessage());
        }
        return null;
    }

    @Override
    public SubmitRecord getRecordByUserIdAndQuestionId(Integer userid,Integer questionId) {
        QuestionSubmit qs = getOne( new QueryWrapper<QuestionSubmit>()
                .select("deadline","score","count")
                .eq("user_id",userid)
                .eq("question_id",questionId));
        if(qs == null) return new SubmitRecord(null,0,0);
        return qs.asViewObject(SubmitRecord.class);
    }

    @Override
    public List<QuestionSubmit> getCorrectedSubmitList(Integer questionId, Integer page, Integer size){
        IPage<QuestionSubmit> iPage = new Page<>(page,size);
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<QuestionSubmit>()
                .ne("score",0)
                .eq("question_id",questionId);
        IPage<QuestionSubmit> result = this.page(iPage,queryWrapper);
        return result.getRecords();
    }

    @Override
    public List<QuestionSubmit> getUncorrectedSubmitList(Integer questionId, Integer page, Integer size){
        IPage<QuestionSubmit> iPage = new Page<>(page,size);
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<QuestionSubmit>()
                .eq("score",0)
                .eq("question_id",questionId);
        IPage<QuestionSubmit> result = this.page(iPage,queryWrapper);
        return result.getRecords();
    }

    private int judgeScore(Map<String,String> answer,Map<String,String> options,StringBuffer wrong){
        int score = 0;
        for(String key : answer.keySet()){
           if(options.get(key).equals(answer.get(key)))wrong.append(key).append(",");
           else score++;
        }
        return score;
    }

    private QuestionSubmit getLastSubmit(Integer id,Integer questionId){
        return this.getOne(new QueryWrapper<QuestionSubmit>()
                .select("count")
                .eq("user_id", id)
                .eq("question_id",questionId));
    }

    private boolean verifyLimit(String address,int limit) {
        String key = Const.SUBMIT_LIMIT + address;
        return flowUtils.limitCheck(key, limit);
    }
}
