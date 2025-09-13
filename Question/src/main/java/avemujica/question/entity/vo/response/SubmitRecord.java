package avemujica.question.entity.vo.response;

import avemujica.common.typeHandler.JsonbToObjectHandler;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmitRecord {
    LocalDateTime timeRecord;
    Integer score;
    Integer count;
}
