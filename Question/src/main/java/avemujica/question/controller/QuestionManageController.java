package avemujica.question.controller;

import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.common.entity.SafeRequest;
import avemujica.question.entity.vo.request.QuestionAddVO;
import avemujica.question.service.QuestionService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/question")
public class QuestionManageController implements MessageHandle{
    @Resource
    QuestionService questionService;

    @CheckNonce
    @PostMapping("/add")
    public RestBean<String> addQuestion(@RequestBody SafeRequest<QuestionAddVO> SFvo){
        QuestionAddVO vo = SFvo.getVO();
        return this.messageHandle(()->questionService.addQuestion(vo));
    }
}
