package cn.xlucky.framework.elasticsearch.apollo.config;

import com.ctrip.framework.apollo.Config;
import com.ctrip.framework.apollo.ConfigService;
import com.ctrip.framework.apollo.model.ConfigChangeEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.cloud.context.scope.refresh.RefreshScope;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.CollectionUtils;


/**
 * apollo热部署
 * @author xlucky
 * @date 2020/5/26
 * @version 1.0.0
 */
@Slf4j
@Configuration
public class PropertiesRefresherConfig implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    @Autowired
    private RefreshScope refreshScope;
    @Value("${apollo.bootstrap.namespaces:}")
    private String appNamespaces;

    public PropertiesRefresherConfig() {
    }

    private void refreshProperties(ConfigChangeEvent changeEvent) {
        this.applicationContext.publishEvent(new EnvironmentChangeEvent(this.applicationContext, changeEvent.changedKeys()));
        this.refreshScope.refreshAll();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() {
        if (this.appNamespaces == null || "".equals(this.appNamespaces)) {
            this.appNamespaces = "application";
        }

        String[] namespacesArr = this.appNamespaces.split(",");
        String[] var2 = namespacesArr;
        int var3 = namespacesArr.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String namespace = var2[var4];
            if (namespace != null && !"".equals(namespace)) {
                namespace = namespace.trim();
                log.info("注册Apollo监听; {}", namespace);
                Config config = ConfigService.getConfig(namespace);
                config.addChangeListener(changeEvent -> {
                    if (!CollectionUtils.isEmpty(changeEvent.changedKeys())) {
                        PropertiesRefresherConfig.this.refreshProperties(changeEvent);
                    }
                });
            }
        }
    }
}