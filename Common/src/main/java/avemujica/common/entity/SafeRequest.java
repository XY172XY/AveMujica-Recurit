package avemujica.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

//用于需要提供时间戳和nonce的请求
@Data
@AllArgsConstructor
public class SafeRequest <T>{

    private Long timestamp;
    private String nonce;
    private T VO;
}
