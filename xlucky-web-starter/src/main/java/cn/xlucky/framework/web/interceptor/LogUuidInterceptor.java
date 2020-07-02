package cn.xlucky.framework.web.interceptor;

import cn.xlucky.framework.common.constant.CommonConstant;
import cn.xlucky.framework.common.log.LogTracing;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Enumeration;

/**
 * @author xlucky
 * @version 1.0.0 on 2017/8/25
 */
@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class LogUuidInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object obj) throws Exception {
        //设置日志uuid
        setResponseTraceId(request, response);
        String apiUri = request.getRequestURI();
        //打印request header
        StringBuffer requestHeaderBuffer = new StringBuffer();
        Enumeration<String> headerNames = request.getHeaderNames();
        String headerName = null;
        while (headerNames.hasMoreElements()) {
            headerName = headerNames.nextElement();
            requestHeaderBuffer.append(headerName).append(": ")
                    .append(request.getHeader(headerName)).append("\n");
        }
        log.info("({}) headers: \n{}", apiUri, requestHeaderBuffer.toString());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("remove traceId;");
        LogTracing.end();
    }

    /**
     * 设置日志uuid
     * <p/>
     *
     * @return
     * @throws
     * @author : xlucky
     * @date : 2018/10/24
     * @version : 1.0.0
     */
    private void setResponseTraceId(final HttpServletRequest request, final HttpServletResponse response) {
        //优先从header中获取uuid
        LogTracing.start(request.getHeader(CommonConstant.LOG_TRACE_ID));
        setResponseTraceId(response);
    }

    private void setResponseTraceId(HttpServletResponse response) {
        response.addHeader(CommonConstant.LOG_TRACE_ID, LogTracing.getTraceId());
        log.info("设置响应TraceId;");
    }
}
