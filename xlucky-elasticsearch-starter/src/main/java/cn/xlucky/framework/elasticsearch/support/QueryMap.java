package cn.xlucky.framework.elasticsearch.support;

import java.util.Collections;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;


/**
 * 封装map
 * @author xlucky
 * @date 2020/5/29
 * @version 1.0.0
 */
public class QueryMap {
    public QueryMap() {
    }

    public static String expression(String fieldName, QueryExpressionType type) {
        return type.getPattern() + fieldName;
    }

    public static Map<String, Object> buildMap(String fieldName, Object fieldValue) {
        if (fieldName.startsWith(QueryExpressionType.GT.getPattern())) {
            return Collections.singletonMap("range", Collections.singletonMap(StringUtils.substring(fieldName, QueryExpressionType.GT.getPattern().length()), Collections.singletonMap("gt", fieldValue)));
        } else if (fieldName.startsWith(QueryExpressionType.GTE.getPattern())) {
            return Collections.singletonMap("range", Collections.singletonMap(StringUtils.substring(fieldName, QueryExpressionType.GTE.getPattern().length()), Collections.singletonMap("gte", fieldValue)));
        } else if (fieldName.startsWith(QueryExpressionType.LT.getPattern())) {
            return Collections.singletonMap("range", Collections.singletonMap(StringUtils.substring(fieldName, QueryExpressionType.LT.getPattern().length()), Collections.singletonMap("lt", fieldValue)));
        } else {
            return fieldName.startsWith(QueryExpressionType.LTE.getPattern()) ? Collections.singletonMap("range", Collections.singletonMap(StringUtils.substring(fieldName, QueryExpressionType.LTE.getPattern().length()), Collections.singletonMap("lte", fieldValue))) : Collections.singletonMap("term", Collections.singletonMap(fieldName, fieldValue));
        }
    }
}