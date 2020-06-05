package cn.xlucky.framework.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import java.util.function.Supplier;


/**
 * spring工具包
 * @author xlucky
 * @date 2020/3/20
 * @version 1.0.0
 */
@Order(Ordered.HIGHEST_PRECEDENCE)
@Slf4j
public class SpringContextUtil implements ApplicationContextAware, BeanDefinitionRegistryPostProcessor {
    private static ApplicationContext applicationContext;
    private static BeanDefinitionRegistry registry;
    public SpringContextUtil() {
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        SpringContextUtil.applicationContext = applicationContext;
    }

    public static String[] getActiveProfiles() {
        return applicationContext.getEnvironment().getActiveProfiles();
    }

    public static Object getBean(String name) {
        try {
            return applicationContext.getBean(name);
        } catch (Exception var2) {
            log.warn(var2.getMessage());
            return null;
        }
    }

    public static <T> T getBean(Class<T> clazz) {
        try {
            return applicationContext.getBean(clazz);
        } catch (Exception var2) {
            log.warn(var2.getMessage());
            return null;
        }
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        try {
            return applicationContext.getBean(name, clazz);
        } catch (Exception var3) {
            log.warn(var3.getMessage());
            return null;
        }
    }

    public static void registryBean(String name, final Object object) {
        registry.registerBeanDefinition(name, BeanDefinitionBuilder.genericBeanDefinition(object.getClass(), new Supplier() {
            @Override
            public Object get() {
                return object;
            }
        }).getBeanDefinition());
    }

    public static boolean isProdEnv() {
        String[] activeProfiles = getActiveProfiles();
        if (activeProfiles.length < 1) {
            return false;
        }
        for (String activeProfile : activeProfiles) {
            if ("prod".equals(activeProfile)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        SpringContextUtil.registry = registry;
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }

}
