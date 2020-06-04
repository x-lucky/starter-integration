package cn.xlucky.framework.mybatis.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
/**
 * 主配置类
 * @author xlucky
 * @date 2020/6/3
 * @version 1.0.0
 */
@Configuration
@EnableTransactionManagement
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@ConditionalOnProperty(prefix = "hikari.datasource", name = {"masterJdbcUrl"})
@ConfigurationProperties(prefix = "hikari.datasource")
@Slf4j
@Data
public class MasterMybatisDatasource {
    private String driverClassName;
    private String masterJdbcUrl;
    private String masterUsername;
    private String masterPassword;
    private Integer maximumPoolSize;
    private Long connectionTimeout;

    public static final String MASTER_TRANSACTION_MANAGER = "masterTransactionManager";

    @Primary
    @Bean(
            name = {"masterDataSource"}
    )
    public DataSource masterDataSource() {
        log.info("====== masterDataSource init ======");
        HikariDataSource ds = new HikariDataSource();
        ds.setDriverClassName(getDriverClassName());
        ds.setMaximumPoolSize(getMaximumPoolSize());
        ds.setConnectionTimeout(getConnectionTimeout());
        ds.setJdbcUrl(getMasterJdbcUrl());
        ds.setUsername(getMasterUsername());
        ds.setPassword(getMasterPassword());
        ds.setMinimumIdle(5);
        ds.setIdleTimeout(120000L);
        ds.setMaxLifetime(60000L);
        return ds;
    }

    @Primary
    @Bean(
        name = {"masterSqlSessionFactory"}
    )
    public SqlSessionFactory masterSqlSessionFactory(@Qualifier("masterDataSource") DataSource dataSource) throws Exception {
        log.info("====== masterSqlSessionFactory init ======");
        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
        factory.setDataSource(dataSource);
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources("classpath:mybatis-config.xml");
        if (resources != null && resources.length > 0) {
            factory.setConfigLocation(resources[0]);
        }

        return factory.getObject();
    }

    @Primary
    @Bean(
        name = {MASTER_TRANSACTION_MANAGER}
    )
    public DataSourceTransactionManager masterTransactionManager() {
        return new DataSourceTransactionManager(this.masterDataSource());
    }
}