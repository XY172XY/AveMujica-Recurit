package avemujica.entrance.interceptor;

import avemujica.common.annotation.CheckNonce;
import avemujica.common.entity.RestBean;
import avemujica.common.entity.SafeRequest;
import avemujica.common.utils.Const;
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

import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

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

        String requestBody = requestBody(request);

        try{
            SafeRequest<?> safeRequest = new ObjectMapper().readValue(requestBody, SafeRequest.class);
            String nonce = safeRequest.getNonce();

            if(StringUtils.isEmpty(nonce)){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(RestBean.forbidden("Missing nonce").asJsonString());
                return false;
            }

            Long timestamp = safeRequest.getTimestamp();
            if(timestamp == null || System.currentTimeMillis() - timestamp > 5*60*1000){
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                response.getWriter().write(RestBean.forbidden("Request expired or invalid timestamp").asJsonString());
                return false;
            }

            String key = Const.NONCE_RECORD + nonce;
            boolean exists = Boolean.TRUE.equals(stringRedisTemplate
                    .opsForValue()
                    .setIfAbsent(key, timestamp.toString(),nonceExpireSeconds, TimeUnit.SECONDS));

            if(!exists){
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

    private String requestBody(HttpServletRequest request) throws UnsupportedEncodingException {
        if(request instanceof ContentCachingRequestWrapper cachingRequest){
            byte[] buf = cachingRequest.getContentAsByteArray();
            if(buf.length > 0){
                return new String(buf,cachingRequest.getCharacterEncoding());
            }
        }
        log.warn("lost ContentCachingRequestWrapper in url:{}", request.getRequestURL().toString());
        return "";
    }

}
