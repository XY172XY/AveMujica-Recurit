package avemujica.usermanage.service.impl;

import avemujica.common.utils.Const;
import avemujica.common.utils.FlowUtils;
import avemujica.usermanage.entity.dto.Account;
import avemujica.usermanage.entity.vo.request.ConfirmResetVO;
import avemujica.usermanage.entity.vo.request.EmailResetVO;
import avemujica.usermanage.entity.vo.request.ModifyEmailVO;
import avemujica.usermanage.entity.vo.request.RegisterAccountVO;
import avemujica.usermanage.mapper.AccountMapper;
import avemujica.usermanage.service.AccountService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.jetbrains.annotations.NotNull;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper,Account> implements AccountService {
   @Resource
   PasswordEncoder passwordEncoder;

   @Resource
   RabbitTemplate rabbitTemplate;

   @Resource
   StringRedisTemplate stringRedisTemplate;

   @Resource
    FlowUtils  flowUtils;

    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(usernameOrEmail);
        if (account == null) {
            throw new UsernameNotFoundException("用户名或密码错误");
        }
        return User
                .withUsername(account.getUsername())
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }

    @Override
    public Account findAccountByNameOrEmail(String usernameOrEmail) {
        return this.query()
                .eq("username",usernameOrEmail)
                .or()
                .eq("email",usernameOrEmail)
                .one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String address){
        synchronized (address.intern()) {
            if(!this.verifyLimit(address)){
                return "请求频繁，请稍后再试";
            }
            Random random = new Random();
            int code = random.nextInt(899999) + 100000;
            Map<String, Object> data = Map.of("type",type,"email", email, "code", code);
            rabbitTemplate.convertAndSend(Const.MQ_MAIL,"mailQueue", data);
            stringRedisTemplate.opsForValue()
                    .set(Const.VERIFY_EMAIL_DATA + email, String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    //注册账户
    @Override
    public String registerAccount(@NotNull RegisterAccountVO vo){
        String code = getEmailVerifyCode(vo.getEmail());
        if(code == null)return "请先获取验证码";
        if(!code.equals(vo.getCode()))return "验证码错误";
        this.deleteEmailVerifyCode(vo.getEmail());
        Account account = this.findAccountByNameOrEmail(vo.getEmail());
        if(account != null) return "该邮箱账号已经被其他账号绑定";
        account = new Account()
                .setUsername(vo.getUsername())
                .setPassword(passwordEncoder.encode(vo.getPassword()))
                .setEmail(vo.getEmail())
                .setRole(Const.ROLE_NORMAL);
        if(this.save(account)){
            return null;
        }
        return "意外失败，请重新尝试";
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVO info) {
        String verify = resetConfirm(new ConfirmResetVO(info.getEmail(), info.getCode()));
        if(verify != null) return verify;
        String email = info.getEmail();
        String password = passwordEncoder.encode(info.getPassword());
        boolean update = this.update().eq("email", email).set("password", password).update();
        if(update) {
            this.deleteEmailVerifyCode(email);
        }
        return update ? null : "更新失败，请联系管理员";
    }

    @Override
    public String modifyEmail(int id, ModifyEmailVO vo) {
        String code = getEmailVerifyCode(vo.getEmail());
        if (code == null) return "请先获取验证码";
        if(!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        this.deleteEmailVerifyCode(vo.getEmail());
        Account account = this.findAccountByNameOrEmail(vo.getEmail());
        if(account != null && account.getId() != id) return "该邮箱账号已经被其他账号绑定，无法完成操作";
        this.update()
                .set("email", vo.getEmail())
                .eq("id", id)
                .update();
        return null;
    }

    @Override
    public String resetConfirm(ConfirmResetVO info) {
        String email = info.getEmail();
        String code = this.getEmailVerifyCode(email);
        if(code == null) return "请先获取验证码";
        if(!code.equals(info.getCode())) return "验证码错误，请重新输入";
        return null;
    }

    //使用密码改密码
    @Override
    public boolean changePassword(int id, String oldPass, String newPass) {
        Account account = this.getById(id);
        String password = account.getPassword();
        if(!passwordEncoder.matches(oldPass, password))
            return false;
        this.update(Wrappers.<Account>update().eq("id", id)
                .set("password", passwordEncoder.encode(newPass)));
        return true;
    }

    private void deleteEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        stringRedisTemplate.delete(key);
    }

    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }

    private boolean verifyLimit(String address) {
        String key = Const.VERIFY_EMAIL_LIMIT + address;
        return flowUtils.limitCheck(key, verifyLimit);
    }
}
