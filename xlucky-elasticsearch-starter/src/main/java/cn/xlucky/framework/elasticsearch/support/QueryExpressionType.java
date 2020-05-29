package cn.xlucky.framework.elasticsearch.support;

/**
 * 类型
 * @author xlucky
 * @date 2020/5/29
 * @version 1.0.0
 */
public enum QueryExpressionType {
    GT("$$GT:"),
    GTE("$$GTE:"),
    LT("$$LT:"),
    LTE("$$LTE:");

    private String pattern;

    private QueryExpressionType(String pattern) {
        this.pattern = pattern;
    }

    public String getPattern() {
        return this.pattern;
    }
}