package avemujica.usermanage.controller;

import avemujica.common.entity.RestBean;
import avemujica.usermanage.entity.dto.Account;
import avemujica.usermanage.service.AccountService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Resource
    AccountService accountService;

}
