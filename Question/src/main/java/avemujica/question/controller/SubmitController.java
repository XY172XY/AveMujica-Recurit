package avemujica.question.controller;

import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.question.entity.vo.request.QuestionSubmitVO;
import avemujica.question.entity.vo.response.SubmitRecord;
import avemujica.question.service.SubmitService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/submit")
public class SubmitController implements MessageHandle {
    @Resource
    SubmitService submitService;

    @PostMapping()
    public RestBean<String> submit(@RequestBody QuestionSubmitVO vo,
                                   @RequestParam(required = false) MultipartFile file,
                                   @RequestParam @Pattern(regexp = "(flag|choice|material)") String type,
                                   HttpServletRequest request
    ){
        switch (type) {
            case "flag" -> {
                return messageHandle(() -> submitService.submitFlag(vo, request.getRemoteAddr()));
            }
            case "material" -> {
                return messageHandle(() -> submitService.submitMaterial(vo, request.getRemoteAddr(), file));
            }
            case "choice" -> {
                String result = submitService.submitChoice(vo, request.getRemoteAddr());
                if(result.charAt(0) >= '0' && result.charAt(0) <= '9') {
                    //成功返回错误的题目号，例如"1,11,24,",无序
                    return RestBean.success(result);
                }else{
                    return RestBean.failure(400,result);
                }
            }
        }
        return RestBean.failure(400,"bad request param: type");
    }

    @PostMapping("/get")
    public RestBean<SubmitRecord> getSubmitRecord(@RequestParam Integer userId,
                                                  @RequestParam Integer questionId){
        return RestBean.success(submitService.getRecordByUserIdAndQuestionId(userId,questionId));
    }
}
