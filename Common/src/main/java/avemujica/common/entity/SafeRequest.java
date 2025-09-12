package avemujica.common.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//用于需要提供时间戳和nonce的请求
//弃用
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SafeRequest <T>{

    private Long timestamp;
    private String nonce;
    private T VO;
}
