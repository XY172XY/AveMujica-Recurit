package avemujica.entrance.filter;

import avemujica.common.utils.Const;
import avemujica.common.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

@Slf4j
@Component
public class IpLimitFilter extends OncePerRequestFilter {
    @Resource
    StringRedisTemplate stringRedisTemplate;

    @Resource
    FlowUtils utils;

    private static final  String KEY_IP = "ip:";

    private final List<Pattern> badUserAgents = List.of(
            Pattern.compile("sqlmap", Pattern.CASE_INSENSITIVE),
            Pattern.compile("nikto", Pattern.CASE_INSENSITIVE),
            Pattern.compile("nmap", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^$", Pattern.CASE_INSENSITIVE) // 空 UA
    );

    public final List<Pattern> injectionPayloads = List.of(
            Pattern.compile("('.+--)|(\\bor\\b\\s+\\d+=\\d+)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("<script\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bunion\\b\\s+select", Pattern.CASE_INSENSITIVE)
    );

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String ip = request.getRemoteAddr();
        String blockKey = Const.IP_LIMIT + ip;


        String ua = request.getHeader("User-Agent");
        String path = request.getRequestURI();
        String query = request.getQueryString() == null ? "" : request.getQueryString();

        //可疑UA
        if (ua != null && badUserAgents.stream().anyMatch(p -> p.matcher(ua).find())) {
            if(!tryCount(ip)){
                log.warn("可疑的请求,ip:{},ua:{}",ip,ua);
            }
            response.sendError(403, "Forbidden");
            return;
        }

        //发现有注入
        String combined = path + "?" +query;
        if (injectionPayloads.stream().anyMatch(p -> p.matcher(combined).find())) {
            if(!tryCount(ip)){
                log.warn("可疑的请求,ip:{},路径:{},参数{}",ip,path,query);
            }
            response.sendError(403, "Forbidden");
            return;
        }
        filterChain.doFilter(request, response);
    }


    private boolean tryCount(String address) {
        synchronized (address.intern()) {
            if(Boolean.TRUE.equals(stringRedisTemplate.hasKey(Const.FLOW_LIMIT_BLOCK + address)))
                return false;
            String blockKey = Const.FLOW_LIMIT_BLOCK + address;
            return utils.limitCheck(blockKey,60 * 60 * 24);
        }
    }


}
