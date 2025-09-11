package avemujica.usermanage.entity.vo.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class RegisterAccountVO {
    String username;
    @Length(min = 6, max = 20)
    String password;
    @Email
    String email;
    @Length(min = 6, max = 6)
    String code;
}
