package avemujica.usermanage.controller;

import avemujica.common.entity.RestBean;
import avemujica.common.utils.Const;
import avemujica.usermanage.entity.vo.request.ChangePasswordVO;
import avemujica.usermanage.entity.vo.request.ModifyEmailVO;
import avemujica.usermanage.servlet.AccountService;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {
    @Resource
    AccountService service;

    @PostMapping("/change-password")
    public RestBean<Void> changePassword(@RequestBody @Valid ChangePasswordVO vo,
                                         @RequestAttribute(Const.ATTR_USER_ID) int userId) {
        return service.changePassword(userId, vo.getPassword(), vo.getNew_password()) ?
                RestBean.success() : RestBean.failure(401, "原密码输入错误！");
    }

    @PostMapping("/modify-email")
    public RestBean<Void> modifyEmail(@RequestAttribute(Const.ATTR_USER_ID) int id,
                                      @RequestBody @Valid ModifyEmailVO vo) {
        String result = service.modifyEmail(id, vo);
        if(result == null) {
            return RestBean.success();
        } else {
            return RestBean.failure(401, result);
        }
    }


}
