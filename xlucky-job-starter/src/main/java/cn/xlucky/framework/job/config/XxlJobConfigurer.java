package cn.xlucky.framework.job.config;

import cn.xlucky.framework.job.aop.TraceJobAspect;
import com.xxl.job.core.executor.impl.XxlJobSpringExecutor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;


/**
 * xxl-job配置类
 * @author xlucky
 * @date 2020/6/12
 * @version 1.0.0
 */
@Configuration
@ConfigurationProperties(
    prefix = "xxl.job"
)
@ComponentScan(
    basePackages = {"cn.xlucky.framework.**.jobhandler"}
)
@ConditionalOnProperty(
    prefix = "xxl.job",
    name = {"adminAddresses"}
)
@Component
@Data
@Slf4j
public class XxlJobConfigurer {
    private String adminAddresses;
    private String appName;
    private String ip;
    private int port;
    private String accessToken;
    private String logPath = "logs/xxl-job/jobhandler";
    private int logRetentionDays = 30;

    public XxlJobConfigurer() {
    }

    @Bean(
        initMethod = "start",
        destroyMethod = "destroy"
    )
    public XxlJobSpringExecutor xxlJobExecutor() {
        log.info(">>>>>>>>>>> xxl-job config init.");
        XxlJobSpringExecutor xxlJobSpringExecutor = new XxlJobSpringExecutor();
        xxlJobSpringExecutor.setAdminAddresses(adminAddresses);
        xxlJobSpringExecutor.setAppname(appName);
        xxlJobSpringExecutor.setIp(ip);
        xxlJobSpringExecutor.setPort(port);
        xxlJobSpringExecutor.setAccessToken(accessToken);
        xxlJobSpringExecutor.setLogPath(logPath);
        xxlJobSpringExecutor.setLogRetentionDays(logRetentionDays);
        return xxlJobSpringExecutor;
    }


    @Bean
    public TraceJobAspect traceJobAspect() {
        return new TraceJobAspect();
    }

}