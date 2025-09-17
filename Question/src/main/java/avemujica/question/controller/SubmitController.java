package avemujica.question.controller;

import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.common.utils.Const;
import avemujica.question.entity.vo.request.QuestionSubmitVO;
import avemujica.question.entity.vo.response.SubmitRecord;
import avemujica.question.service.SubmitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class SubmitController implements MessageHandle {
    @Resource
    SubmitService submitService;

    @PostMapping("/submits/flag")
    public RestBean<String> submitFlag(@RequestBody @Valid QuestionSubmitVO vo,
                                       HttpServletRequest request) {
        vo.setUserId((Integer) request.getAttribute(Const.ATTR_USER_ID));
        return messageHandle(() -> submitService.submitFlag(vo, request.getRemoteAddr()));
    }

    @PostMapping("/submits/choice")
    public RestBean<String> submitChoice(@RequestBody @Valid QuestionSubmitVO vo,
                                         HttpServletRequest request) {
        vo.setUserId((Integer) request.getAttribute(Const.ATTR_USER_ID));
        String result = submitService.submitChoice(vo, request.getRemoteAddr());
        if (result.isEmpty()) return RestBean.success();
        if (Character.isDigit(result.charAt(0))) return RestBean.success(result); // 返回错误题号串
        return RestBean.failure(400, result);
    }

    @PostMapping(path = "/submits/material", consumes = "multipart/form-data")
    public RestBean<String> submitMaterial(@RequestPart("vo") @Valid QuestionSubmitVO vo,
                                           @RequestPart(value = "file", required = false) MultipartFile file,
                                           HttpServletRequest request) {
        vo.setUserId((Integer) request.getAttribute(Const.ATTR_USER_ID));
        return messageHandle(() -> submitService.submitMaterial(vo, request.getRemoteAddr(), file));
    }

    @GetMapping("/submits")
    public RestBean<SubmitRecord> getSubmitRecord(@RequestParam Integer userId,
                                                  @RequestParam Integer questionId){
        return RestBean.success(submitService.getRecordByUserIdAndQuestionId(userId,questionId));
    }
}
