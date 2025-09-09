package avemujica.entrance.configuration;

import avemujica.common.entity.RestBean;
import avemujica.common.utils.Jwt;
import avemujica.entrance.filter.JwtAuthenticationFilter;
import avemujica.entrance.filter.RequestLogFilter;
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
    Jwt jwtUtils;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(conf->conf
                                .anyRequest().permitAll()
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
            //todo 比对数据库
            //todo jwt令牌生成，与过期时间设置
            //String jwt = jwtUtils.createJwt(user,)
        }
    }

    private void onLogoutSuccess(HttpServletRequest req,
                                 HttpServletResponse res,
                                 Object exceptionOrAuthentication) throws IOException {
        res.setContentType("application/json;charset=UTF-8");
        PrintWriter writer = res.getWriter();
        String authorization = req.getHeader("Authorization");
        //todo 验证Jwt令牌
        writer.write(RestBean.failure(400,"未能正常退出").asJsonString());
    }

}
