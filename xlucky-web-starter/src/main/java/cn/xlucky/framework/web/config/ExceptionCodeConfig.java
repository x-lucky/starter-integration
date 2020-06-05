package cn.xlucky.framework.web.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
* 异常类配置
* <p>
* @author xlucky
* @version  1.0.0
* @date 2019/5/9
*/
@Configuration
@ConfigurationProperties(
    prefix = "xlucky"
)
public class ExceptionCodeConfig {
    /**
     * 异常池
     */
    public static Map<String, String> EX_MAP;
    private Map<String, String> exception;

    public ExceptionCodeConfig() {
    }

    public void setException(Map<String, String> exception) {
        EX_MAP = exception;
        this.exception = exception;
    }

    public Map<String, String> getException() {
        return this.exception;
    }
}