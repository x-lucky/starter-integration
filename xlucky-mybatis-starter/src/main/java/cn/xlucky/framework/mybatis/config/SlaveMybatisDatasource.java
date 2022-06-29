package cn.xlucky.framework.mybatis.config;

import com.zaxxer.hikari.HikariDataSource;
import javax.sql.DataSource;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.transaction.annotation.EnableTransactionManagement;


/**
 * 类说明
 * @author xlucky
 * @date 2020/6/3
 * @version 1.0.0
 */
//@Configuration
//@EnableTransactionManagement
//@MapperScan(
//        basePackages = {"cn.xlucky.**.mapper.slave"},
//    sqlSessionFactoryRef = "slaveSqlSessionFactory"
//)
//@AutoConfigureBefore({DataSourceAutoConfiguration.class})
//@ConditionalOnProperty(
//    prefix = "hikari.dataSource",
//    name = {"slaveJdbcUrl"}
//)
//@ConfigurationProperties(
//    prefix = "hikari.dataSource"
//)
@Data
@Slf4j
public class SlaveMybatisDatasource {
    private String driverClassName;
    private String slaveJdbcUrl;
    private String slaveUsername;
    private String slavePassword;
    private Integer maximumPoolSize;
    private Long connectionTimeout;

    public SlaveMybatisDatasource() {
    }

//    @Bean(
//        name = {"slaveSqlSessionFactory"}
//    )
//    public SqlSessionFactory slaveSqlSessionFactory(@Qualifier("slaveDataSource") DataSource dataSource) throws Exception {
//        log.info("====== slaveSqlSessionFactory init ======");
//        SqlSessionFactoryBean factory = new SqlSessionFactoryBean();
//        factory.setDataSource(dataSource);
//        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
//        Resource[] resources = resolver.getResources("classpath:mybatis-config.xml");
//        if (resources != null && resources.length > 0) {
//            factory.setConfigLocation(resources[0]);
//        }
//        return factory.getObject();
//    }
//
//    @Bean(
//        name = {"slaveDataSource"}
//    )
//    public DataSource slaveDataSource() {
//        log.info("====== slaveDataSource init ======");
//        HikariDataSource ds = new HikariDataSource();
//        ds.setDriverClassName(getDriverClassName());
//        ds.setMaximumPoolSize(getMaximumPoolSize());
//        ds.setConnectionTimeout(getConnectionTimeout());
//        ds.setJdbcUrl(getSlaveJdbcUrl());
//        ds.setUsername(getSlaveUsername());
//        ds.setPassword(getSlavePassword());
//        ds.setMinimumIdle(5);
//        ds.setIdleTimeout(120000L);
//        ds.setMaxLifetime(60000L);
//        return ds;
//    }
}