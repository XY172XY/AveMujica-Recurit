package avemujica.usermanage.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class AuthorizeVO {
    String username;
    String email;
    String role;
    String token;
    Date expire;
}
