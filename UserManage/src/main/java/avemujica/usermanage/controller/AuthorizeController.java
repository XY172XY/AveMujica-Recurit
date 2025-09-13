package avemujica.usermanage.controller;

import avemujica.common.entity.MessageHandle;
import avemujica.common.entity.RestBean;

import avemujica.common.utils.BloomFilterUtils;
import avemujica.usermanage.entity.vo.request.EmailResetVO;
import avemujica.usermanage.entity.vo.request.RegisterAccountVO;
import avemujica.usermanage.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthorizeController implements MessageHandle {
    @Resource
    AccountService accountService;

    @Resource
    BloomFilterUtils bloomFilterUtils;

    @PostMapping("/ask-code")
    @Operation(summary = "请求邮件验证码")
    public RestBean<Void> askVerifyCode(@RequestParam @Email String email,
                                        @RequestParam @Pattern(regexp = "(reset|modify|register)") String type,
                                        HttpServletRequest request) {
        return this.messageHandle(() ->
                accountService.registerEmailVerifyCode(type, String.valueOf(email), request.getRemoteAddr()));
    }

    @PostMapping("/reset-password")
    @Operation(summary = "密码重置操作")
    public RestBean<Void> resetPassword(@RequestBody @Valid EmailResetVO vo) {
        return this.messageHandle(() ->
                accountService.resetEmailAccountPassword(vo));
    }

    @PostMapping("/register")
    @Operation(summary = "注册用户")
    public RestBean<Void> registerAccount(@RequestBody @Valid RegisterAccountVO vo) {
        return this.messageHandle(() ->
                accountService.registerAccount(vo));
    }

//    @GetMapping("/test")
//    public RestBean<String> test(){
//        bloomFilterUtils.testBooleanFilter();
//        return RestBean.success();
//    }
}
