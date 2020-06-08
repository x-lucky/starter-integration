package cn.xlucky.framework.web.config;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * 日志拦截
 * @author xlucky
 * @date 2020/1/3
 * @version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@AutoConfigureAfter(WebAutoConfig.class)
@Component
public class InterceptorConfig extends WebMvcConfigurationSupport {
    @Resource(name = "LogUuidInterceptor")
    private HandlerInterceptor logUuidInterceptor;

    /**
     * 注册拦截器
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        //注册session拦截器
        InterceptorRegistration interceptorRegistration = registry.addInterceptor(logUuidInterceptor);
        // 配置拦截的路径
        interceptorRegistration.addPathPatterns("/**");
        interceptorRegistration.excludePathPatterns(new String[]{"/**.js", "/**.css"});
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        //将所有/static/** 访问都映射到classpath:/static/ 目录下
        registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //swagger2
        registry.addResourceHandler("/swagger-ui.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}

