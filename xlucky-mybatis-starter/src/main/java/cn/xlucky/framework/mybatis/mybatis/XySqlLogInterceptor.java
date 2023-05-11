package cn.xlucky.framework.mybatis.mybatis;


import cn.xlucky.framework.common.util.DateUtil;
import cn.xlucky.framework.mybatis.config.MybatisProperties;
import com.alibaba.fastjson.JSON;
import org.apache.commons.lang3.time.DateUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;

import java.sql.Statement;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;

/**
 * SqlLogInterceptor
 *
 * @author lizheng 日撸代码三千行，不识加班累，只缘bug狂。
 * @version 1.0
 * @date 2021/7/29 下午4:12
 */

@Intercepts({
        @Signature(type = StatementHandler.class,method = "query",args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class,method = "update",args = {Statement.class}),
        @Signature(type = StatementHandler.class,method = "batch",args = {Statement.class}
)})
@Component
public class XySqlLogInterceptor implements Interceptor, Ordered {

    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss:SSS";
    private static final Log LOGGER = LogFactory.getLog(XySqlLogInterceptor.class);

    private static final int EX_COUNT_THRESHOLD = 5;

    private static final String SQL_MIX = "-!%@!#-";

    private final AtomicInteger exCounter = new AtomicInteger(0);

    private final MybatisProperties mybatisProperties;

    public XySqlLogInterceptor(MybatisProperties mybatisProperties) {
        this.mybatisProperties = mybatisProperties;
    }


    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        // 判断连续异常次数是否达到阀值
        if (exCounter.get() > EX_COUNT_THRESHOLD) {
            return invocation.proceed();
        }
        // 耗时开始时间
        long startTime = System.currentTimeMillis();
        String sql = "";
        String sqlId = "";
        try {
            // 获取 StatementHandler ，默认是 RoutingStatementHandler
            StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
            // 获取 StatementHandler 包装类
            MetaObject metaObjectHandler = SystemMetaObject.forObject(statementHandler);
            // 获取查询接口映射的相关信息
            MappedStatement mappedStatement = (MappedStatement) metaObjectHandler.getValue("delegate.mappedStatement");

            // 获取sql
            sql = showSql(statementHandler, mappedStatement,  metaObjectHandler);
            // 获取执行sql方法
            sqlId = mappedStatement.getId();
            // 重置异常计数器
            exCounter.set(0);
        } catch (Exception e) {
            LOGGER.error(e);
            exCounter.incrementAndGet();
        }
        Object result;
        try {
            // 执行sql
            result = invocation.proceed();
        } catch (Exception e) {
            // 出现异常也要打印出sql语句
            if (mybatisProperties.isShowSql()) {
                LOGGER.info(String.format("\n======> SQL方法: %s \n======> SQL语句: %s ", sqlId, sql));
            }
            throw e;
        }
        // 计算总耗时
        long cost = System.currentTimeMillis() - startTime;
        if (mybatisProperties.isShowSql()) {
            LOGGER.info(String.format("\n======> SQL方法: %s \n======> SQL语句: %s \n======> 总耗时: %s 毫秒\n======> 执行结果:%s",
                    sqlId, sql, cost, mybatisProperties.isShowResultUseJson() ? JSON.toJSONString(result) : "{}"));
        }
        return result;
    }


    private String showSql(StatementHandler statementHandler, MappedStatement mappedStatement, MetaObject metaObjectHandler) {
        Configuration configuration = mappedStatement.getConfiguration();
        BoundSql boundSql;
        try {
            // 优先使用delegate.boundSql
            boundSql = (BoundSql) metaObjectHandler.getValue("delegate.boundSql");
            if (boundSql == null) {
                throw new RuntimeException("delegate.boundSql is null");
            }
        } catch (Exception e) {
            LOGGER.warn("MetaObject getValue delegate.boundSql error, use mappedStatement.getBoundSql(parameterObject)", e);
            Object parameterObject = statementHandler.getParameterHandler().getParameterObject();
            boundSql = mappedStatement.getBoundSql(parameterObject);
        }

        String sql;
        try {
            // 优先使用delegate.boundSql.sql
            sql = (String) metaObjectHandler.getValue("delegate.boundSql.sql");
            if (sql == null) {
                throw new RuntimeException("delegate.boundSql.sql is null");
            }
        } catch (Exception e) {
            LOGGER.warn("MetaObject getValue delegate.boundSql.sql error, use boundSql.getSql()", e);
            sql = boundSql.getSql();
        }


        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        sql = sql.replaceAll("[\\s]+", " ");
        // 替换 ? 为一串特殊的混淆字符串,防止参数里含有?造成错乱
        sql = sql.replace("?", SQL_MIX);
        if (parameterMappings != null && parameterMappings.size() != 0 && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst(SQL_MIX, Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst(SQL_MIX, Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst(SQL_MIX, Matcher.quoteReplacement(getParameterValue(obj)));
                    } else {
                        // 打印出缺失，提醒该参数缺失并防止错位
                        sql = sql.replaceFirst(SQL_MIX, "缺失");
                    }
                }
            }
        }

        return sql;
    }


    private String getParameterValue(Object obj) {
        String value;

        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        }
        else if (obj instanceof Date) {
            value = DateUtil.format((Date) obj,DATE_FORMAT);
        }
        else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }

        return value;
    }





    @Override
    public Object plugin(Object target) {
        return target instanceof StatementHandler ? Plugin.wrap(target, this) : target;
    }

    @Override
    public void setProperties(Properties properties) {

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
