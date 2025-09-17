package avemujica.entrance.interceptor;

import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.RestBean;
import avemujica.common.entity.SafeRequest;
import avemujica.common.utils.Const;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.common.util.StringUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

//防重放过滤器
@Slf4j
@Component
public class NonceInterceptor implements HandlerInterceptor {
    @Value("${nonce.expire.seconds}")
    long nonceExpireSeconds;

    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod handlerMethod)){
            return true;
        }
        //注解控制检查
        if(!(handlerMethod.getMethod().isAnnotationPresent(CheckNonce.class))){
            return true;
        }

        String nonce = request.getHeader("X-Nonce");
        long timestamp = Long.parseLong(request.getHeader("X-Timestamp"));

        try{
            if(StringUtils.isEmpty(nonce)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(RestBean.forbidden("Missing nonce").asJsonString());
                return false;
            }

            if(System.currentTimeMillis() - timestamp > 5*60*1000){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(RestBean.forbidden("Request expired or invalid timestamp").asJsonString());
                return false;
            }

            String key = Const.NONCE_RECORD + nonce;
            boolean notExists = Boolean.TRUE.equals(stringRedisTemplate
                    .opsForValue()
                    .setIfAbsent(key, Long.toString(timestamp),nonceExpireSeconds, TimeUnit.SECONDS));

            if(!notExists){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                //重复代表可能存在重放攻击
                response.getWriter().write(RestBean.forbidden("Duplicate request or replay attack detected").asJsonString());
                log.warn("Duplicate nonce detected: {}", nonce);
                return false;
            }
            return  true;
        }catch (Exception e){
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(RestBean.forbidden("Bad request").asJsonString());
            return false;
        }
    }
}
