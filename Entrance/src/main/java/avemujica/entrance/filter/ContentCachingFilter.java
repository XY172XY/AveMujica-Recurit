package avemujica.entrance.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.IOException;

//只有部分uri需要contentCaching包装
//弃用
@Component
public class ContentCachingFilter extends OncePerRequestFilter {
    private static final int MAX_WRAP_SIZE = 256 * 1024;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if(needWarp(request)){
            ContentCachingRequestWrapper  requestWrapper = new ContentCachingRequestWrapper(request,MAX_WRAP_SIZE);
            doFilter(requestWrapper,response,filterChain);
        }
        else{
            doFilter(request,response,filterChain);
        }
    }

    private boolean needWarp(HttpServletRequest request){
        String m = request.getMethod();
        if ("GET".equals(m) || "HEAD".equals(m) || "DELETE".equals(m)) return false;

        String uri = request.getRequestURI();
        if(uri.startsWith("/AveMujica/api/upload-answer"))return false;

        long len = request.getContentLengthLong();
        if (len > MAX_WRAP_SIZE) return false;

        return true;
    }
}
