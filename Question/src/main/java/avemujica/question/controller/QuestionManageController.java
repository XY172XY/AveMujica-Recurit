package avemujica.question.controller;

import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.question.entity.dto.Question;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.entity.vo.request.QuestionUpdateVO;
import avemujica.question.entity.vo.response.QuestionDetail;
import avemujica.question.service.QuestionService;
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

    @CheckNonce
    @PostMapping("/add")
    @Operation(summary = "请完整上传题目信息")
    public RestBean<String> addQuestion(@RequestBody QuestionAddVO vo,
                                        @RequestParam String nonce,
                                        @RequestParam Long timestamp) {
        return this.messageHandle(()->questionService.addQuestion(vo));
    }

    @CheckNonce
    @PostMapping("/update")
    @Operation(summary = "题目更新接口,允许只上传题目需要更改的部分")
    public RestBean<String> updateQuestion(@RequestBody @Valid QuestionUpdateVO vo,
                                           @RequestParam String nonce,
                                           @RequestParam Long timestamp){
        return this.messageHandle(()->questionService.updateQuestion(vo));
    }

    @CheckNonce
    @PostMapping("/delete")
    @Operation(summary = "删除题目")
    public RestBean<String> deleteQuestion(@RequestParam Integer id,
                                           @RequestParam String nonce,
                                           @RequestParam Long timestamp){
        return this.messageHandle(()->questionService.removeById(id) ? null : "该题目不存在，请校对题目编号");
    }

    @CheckNonce
    @PostMapping("/select-direction-turn")
    public RestBean<List<Question>> selectQuestionByDirection(@RequestParam(required = false) String direction,
                                                              @RequestParam(required = false) String turn,
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
        QuestionDetail res = questionService.selectDetailById(id);
        return res != null ? RestBean.success(res) : RestBean.failure(500,"查询失败");
    }
}
