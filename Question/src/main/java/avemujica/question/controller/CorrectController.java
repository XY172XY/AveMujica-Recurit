package avemujica.question.controller;

import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.service.CorrectService;
import avemujica.question.service.FileDownloadService;
import avemujica.question.service.SubmitService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/admin")
public class CorrectController implements MessageHandle {
    @Resource
    SubmitService submitService;

    @Resource
    FileDownloadService fileDownloadService;

    @Resource
    CorrectService correctService;

    @PostMapping("/get-corrected-submit")
    public RestBean<List<QuestionSubmit>> getCorrectedSubmit(@RequestParam Integer question_id,
                                                             @RequestParam Integer page,
                                                             @RequestParam Integer size) {
        return RestBean.success(submitService.getCorrectedSubmitList(question_id,page,size));
    }

    @PostMapping("/get-uncorrected-submit")
    public RestBean<List<QuestionSubmit>> getUncorrectedSubmit(@RequestParam Integer question_id,
                                                               @RequestParam Integer page,
                                                               @RequestParam Integer size) {
        return RestBean.success(submitService.getUncorrectedSubmitList(question_id,page,size));
    }

    @PostMapping("/down-load")
    public RestBean<String> downLoad(@RequestParam String fileName) {
        return RestBean.success(fileDownloadService.getDownloadUrl(fileName));
    }

    @PostMapping("/correct")
    public RestBean<String> correct(@RequestParam Integer submitId,
                                    @RequestParam Integer score,
                                    @RequestParam Integer questionId) {
        return messageHandle(()->correctService.correct(submitId,score,questionId));
    }
}
