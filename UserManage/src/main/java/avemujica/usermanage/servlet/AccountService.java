package avemujica.usermanage.servlet;

import avemujica.usermanage.entity.dto.Account;
import avemujica.usermanage.entity.vo.request.ConfirmResetVO;
import avemujica.usermanage.entity.vo.request.EmailResetVO;
import avemujica.usermanage.entity.vo.request.ModifyEmailVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String usernameOrEmail);

    String registerEmailVerifyCode(String type, String email, String address);

    String resetEmailAccountPassword(EmailResetVO info);

    String resetConfirm(ConfirmResetVO info);

    boolean changePassword(int id, String oldPass, String newPass);

    String modifyEmail(int id, ModifyEmailVO vo);
}
