package avemujica.question.controller;

import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;
import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.service.CorrectService;
import avemujica.question.service.FileDownloadService;
import avemujica.question.service.SubmitService;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import javax.annotation.RegEx;
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

    @GetMapping("/questions/{questionId}/submits")
    public RestBean<List<QuestionSubmit>> getSubmit(@PathVariable Integer questionId,
                                                             @RequestParam(defaultValue = "1") Integer page,
                                                             @RequestParam(defaultValue = "10") @Min(1) Integer size,
                                                             @RequestParam @Pattern(regexp = "(uncorrect|correct)")  String status) {
        if ("corrected".equalsIgnoreCase(status)) {
            return RestBean.success(submitService.getCorrectedSubmitList(questionId, page, size));
        } else if ("uncorrected".equalsIgnoreCase(status)) {
            return RestBean.success(submitService.getUncorrectedSubmitList(questionId, page, size));
        } else {
            return RestBean.failure(400, "status 参数必须是 corrected 或 uncorrected");
        }
    }

    @GetMapping("/files/{fileName}")
    public RestBean<String> downLoad(@PathVariable String fileName) {
        return RestBean.success(fileDownloadService.getDownloadUrl(fileName));
    }

    @PutMapping("/submits/{submitId}/score")
    public RestBean<String> correct(@PathVariable Integer submitId,
                                    @RequestParam Integer score,
                                    @RequestParam Integer questionId) {
        return messageHandle(()->correctService.correct(submitId,score,questionId));
    }
}
