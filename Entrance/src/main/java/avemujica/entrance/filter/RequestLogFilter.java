package avemujica.entrance.filter;

import avemujica.common.utils.Const;
import avemujica.common.utils.SnowflakeIdGenerator;
import com.alibaba.fastjson2.JSONObject;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.util.Set;

@Slf4j
@Component
public class RequestLogFilter extends OncePerRequestFilter {

    @Resource
    SnowflakeIdGenerator generator;

    private final Set<String> ignores = Set.of("/swagger-ui");

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(this.isIgnoreUrl(request.getServletPath())) {
            filterChain.doFilter(request, response);
        } else {
            //记录访问开始时间
            long startTime = System.currentTimeMillis();
            //处理开始
            this.logRequestStart(request);
            //这是一个response的包装类，有一些增强的功能
            ContentCachingResponseWrapper wrapper = new ContentCachingResponseWrapper(response);
            //继续过滤
            filterChain.doFilter(request, wrapper);
            //结束处理
            this.logRequestEnd(wrapper, startTime);
            //处理完后解除包装状态
            wrapper.copyBodyToResponse();
        }
    }

    private boolean isIgnoreUrl(String url){
        for (String ignore : ignores) {
            if(url.startsWith(ignore)) return true;
        }
        return false;
    }

    public void logRequestStart(HttpServletRequest request){
        long reqId = generator.nextId();
        MDC.put("reqId", String.valueOf(reqId));
        JSONObject object = new JSONObject();
        //把请求参数都打印出来
        request.getParameterMap().forEach((k, v) -> object.put(k, v.length > 0 ? v[0] : null));
        Object id = request.getAttribute(Const.ATTR_USER_ID);
        if(id != null) {
            //这个比较难记，从holder拿到context，从context里拿authentication，从authentication里拿到user（principal）
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ 身份: {} (UID: {}) | 角色: {} | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(),
                    user.getUsername(), id, user.getAuthorities(), object);
        } else {
            log.info("请求URL: \"{}\" ({}) | 远程IP地址: {} │ 身份: 未验证 | 请求参数列表: {}",
                    request.getServletPath(), request.getMethod(), request.getRemoteAddr(), object);
        }
    }

    //负责记录响应时长
    public void logRequestEnd(ContentCachingResponseWrapper wrapper, long startTime){
        long time = System.currentTimeMillis() - startTime;
        int status = wrapper.getStatus();
        String content = status != 200 ?
                status + " 错误" : new String(wrapper.getContentAsByteArray());
        log.info("请求处理耗时: {}ms | 响应结果: {}", time, content);
    }

}
