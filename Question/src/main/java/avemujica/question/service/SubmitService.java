package avemujica.question.service;

import avemujica.question.entity.dto.QuestionSubmit;
import avemujica.question.entity.vo.request.QuestionSubmitVO;
import avemujica.question.entity.vo.response.SubmitRecord;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SubmitService extends IService<QuestionSubmit> {
    String submitFlag(QuestionSubmitVO vo, String address);

    String submitChoice(QuestionSubmitVO vo, String address);

    String submitMaterial(QuestionSubmitVO vo, String address, MultipartFile file);

    SubmitRecord getRecordByUserIdAndQuestionId(Integer userid,Integer questionId);

    List<QuestionSubmit> getCorrectedSubmitList(Integer questionId, Integer page, Integer size);

    List<QuestionSubmit> getUncorrectedSubmitList(Integer questionId, Integer page, Integer size);
}
