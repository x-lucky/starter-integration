package cn.xlucky.framework.mybatis.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * GlobalExceptionProperties
 *
 * @author lizheng 日撸代码三千行，不识加班累，只缘bug狂。
 * @version 1.0
 * @date 2020/9/23 15:02
 */
@ConfigurationProperties(prefix = "xy.mybatis")
@Component
public class MybatisProperties {

    /**
     * 是否打印sql日志
     */
    private boolean showSql = true;

    /**
     * 是否json打印sql执行结果
     */
    private boolean showResultUseJson = false;

    /**
     * 慢sql警告通知
     */
    private boolean slowSqlNotice = true;

    /**
     * 慢sql执行时间
     */
    private long slowSqlTime = 500L;

    public boolean isShowSql() {
        return showSql;
    }

    public void setShowSql(boolean showSql) {
        this.showSql = showSql;
    }

    public boolean isShowResultUseJson() {
        return showResultUseJson;
    }

    public void setShowResultUseJson(boolean showResultUseJson) {
        this.showResultUseJson = showResultUseJson;
    }

    public boolean isSlowSqlNotice() {
        return slowSqlNotice;
    }

    public void setSlowSqlNotice(boolean slowSqlNotice) {
        this.slowSqlNotice = slowSqlNotice;
    }

    public long getSlowSqlTime() {
        return slowSqlTime;
    }

    public void setSlowSqlTime(long slowSqlTime) {
        this.slowSqlTime = slowSqlTime;
    }
}
