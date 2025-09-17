package avemujica.question.controller;

import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.question.entity.dto.Answer;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.entity.vo.response.QuestionDetail;
import avemujica.question.service.QuestionCacheService;
import avemujica.question.service.QuestionService;
import avemujica.question.service.impl.QuestionCacheServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/question")
public class QuestionManageController implements MessageHandle{
    @Resource
    QuestionService questionService;
    @Resource
    QuestionCacheService questionCacheService;


    @CheckNonce
    @PostMapping("/admin/add")
    @Operation(summary = "请完整上传题目信息")
    public RestBean<String> addQuestion(@RequestBody QuestionAddVO vo,
                                        @RequestParam String nonce,
                                        @RequestParam Long timestamp) {
        return this.messageHandle(() -> questionCacheService.addCacheQuestion(vo));
    }

    @CheckNonce
    @PostMapping("/admin/update")
    @Operation(summary = "题目更新接口,请上传完整题目信息")
    public RestBean<String> updateQuestion(@RequestBody @Valid QuestionUpdateVO vo,
                                           @RequestParam String nonce,
                                           @RequestParam Long timestamp){
        return this.messageHandle(()->questionCacheService.updateByQuestionUpdateVO(vo)==null ? null : "更新失败");
    }

    @CheckNonce
    @PostMapping("/admin/delete")
    @Operation(summary = "删除题目")
    public RestBean<String> deleteQuestion(@RequestParam Integer id,
                                           @RequestParam String nonce,
                                           @RequestParam Long timestamp){
        return this.messageHandle(()->questionCacheService.deleteQuestion(id) == null ? null : "该题目不存在，请校对题目编号");
    }

    @CheckNonce
    @PostMapping("/select-direction-turn")
    public RestBean<List<Question>> selectQuestionByDirectionAndTurn(@RequestParam(required = false) String direction,
                                                                     @RequestParam(required = false) Integer turn,
                                                                     @RequestParam Integer page,
                                                                     @RequestParam String nonce,
                                                                     @RequestParam Long timestamp){
        if(direction==null||turn==null){
            return RestBean.failure(400,"参数错误");
        }
        List<Question> res = questionService.selectByTurnOrDirections(direction,turn,page);
        return res != null ? RestBean.success(res) : RestBean.failure(500,"查询失败");
    }

    @CheckNonce
    @PostMapping("select-detail")
    public RestBean<QuestionDetail> selectQuestionDetail(@RequestParam Integer id,
                                                         @RequestParam String nonce,
                                                         @RequestParam Long timestamp){
        Question question = questionCacheService.getQuestionById(id);
        QuestionDetail res = question != null ? question.asViewObject(QuestionDetail.class) : null;
        return res != null ? RestBean.success(res) : RestBean.failure(500,"题目不存在");
    }
}
