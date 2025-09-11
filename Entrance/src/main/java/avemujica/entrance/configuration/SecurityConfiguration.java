package avemujica.entrance.configuration;

import avemujica.common.entity.RestBean;
import avemujica.common.utils.Const;
import avemujica.common.utils.Jwt;
import avemujica.entrance.filter.JwtAuthenticationFilter;
import avemujica.entrance.filter.RequestLogFilter;
import avemujica.usermanage.entity.dto.Account;
import avemujica.usermanage.entity.vo.response.AuthorizeVO;
import avemujica.usermanage.service.AccountService;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfiguration {
    @Resource
    JwtAuthenticationFilter jwtAuthenticationFilter;

    @Resource
    RequestLogFilter requestLogFilter;

    @Resource
    AccountService accountService;

    @Resource
    Jwt jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                                .requestMatchers("/api/auth/**","/error").permitAll()
                                .requestMatchers("/AveMujica/").permitAll()
                                .anyRequest().hasAnyRole(Const.ROLE_ADMIN,Const.ROLE_NORMAL)
                        )
                .formLogin(conf->conf
                        .loginProcessingUrl("/api/auth/login")
                        .failureHandler(this::handleProcess)
                        .successHandler(this::handleProcess)
                        .permitAll()
                )
                .logout(conf->conf
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler(this::onLogoutSuccess)
                )
                .exceptionHandling(conf->conf
                        .accessDeniedHandler(this::handleProcess)
                        .authenticationEntryPoint(this::handleProcess)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(conf->conf
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(requestLogFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthenticationFilter, RequestLogFilter.class)
                .build();


    }

    //四个处理一次解决
    private void handleProcess(HttpServletRequest req,
                               HttpServletResponse res,
                               Object exceptionOrAuthentication) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = res.getWriter();
        if(exceptionOrAuthentication instanceof AccessDeniedException exception){
            writer.write(RestBean.forbidden(exception.getMessage()).asJsonString());
        }
        else if(exceptionOrAuthentication instanceof AuthenticationException exception){
            writer.write(RestBean.unauthorized(exception.getMessage()).asJsonString());
        }
        else if(exceptionOrAuthentication instanceof Authentication authentication){
            User user = (User) authentication.getPrincipal();
            Account account = accountService.findAccountByNameOrEmail(user.getUsername());
            String jwt = jwtUtils.createJwt(user,account.getUsername(),account.getId());
            if(jwt == null){
                writer.write(RestBean.forbidden("验证频繁，请稍后再试").asJsonString());
            }
            else{
                AuthorizeVO vo = account.asViewObject(AuthorizeVO.class,o->o.setToken(jwt));
                vo.setExpire(jwtUtils.expireTime());
                writer.write(RestBean.success(vo).asJsonString());
            }
        }
    }

    private void onLogoutSuccess(HttpServletRequest req,
                                 HttpServletResponse res,
                                 Object exceptionOrAuthentication) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = res.getWriter();
        //authorization 实际为携带的jwt令牌
        String authorization = req.getHeader("Authorization");
        if(jwtUtils.invalidateJwt(authorization)){
            writer.write(RestBean.success("推出登录成功").asJsonString());
            return;
        }
        writer.write(RestBean.failure(400,"未能正常退出").asJsonString());
    }

}
