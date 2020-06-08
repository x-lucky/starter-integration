package cn.xlucky.framework.web.config;

import cn.xlucky.framework.web.exception.BusinessExceptionHandler;
import cn.xlucky.framework.web.interceptor.LogUuidInterceptor;
import cn.xlucky.framework.web.util.SpringContextUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 自动装配
 *
 * @author xlucky
 * @version 1.0.0
 * @date 2020/6/4
 */
@Configuration
public class WebAutoConfig {

    @Bean(name = "LogUuidInterceptor")
    public LogUuidInterceptor logUuidInterceptor() {
        return new LogUuidInterceptor();
    }

    @Bean(name = "BusinessExceptionHandler")
    public BusinessExceptionHandler businessExceptionHandler() {
        return new BusinessExceptionHandler();
    }

    @Bean
    public SpringContextUtil springContextUtil() {
        return new SpringContextUtil();
    }

}
