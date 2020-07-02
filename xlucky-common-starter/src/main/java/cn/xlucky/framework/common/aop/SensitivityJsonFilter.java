package cn.xlucky.framework.common.aop;

import cn.xlucky.framework.common.anno.SensitivityField;
import com.alibaba.fastjson.serializer.ValueFilter;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Field;


/**
 * 敏感过滤
 * @author xlucky
 * @date 2020/6/30
 * @version 1.0.0
 */
@Slf4j
public class SensitivityJsonFilter implements ValueFilter {

    public SensitivityJsonFilter() {
    }

    @Override
    public Object process(Object object, String name, Object value) {
        if (value == null) {
            return value;
        } else {
            Field field = this.getField(object.getClass(), name);
            if (field == null) {
                log.debug(object.getClass() + "(" + name + ") field not found.");
                return value;
            } else {
                SensitivityField sensitivityField = field.getAnnotation(SensitivityField.class);
                if (sensitivityField == null) {
                    return value;
                } else {
                    String format = sensitivityField.format();
                    int formatLength = format.length();
                    String valueStr = value.toString();
                    int valueLength = valueStr.length();
                    StringBuffer resultBuffer = new StringBuffer();
                    String formatChartStr = null;

                    for(int i = 0; i < valueLength && i < formatLength; ++i) {
                        formatChartStr = String.valueOf(format.charAt(i));
                        if ("#".equals(formatChartStr)) {
                            resultBuffer.append(valueStr.charAt(i));
                        } else {
                            resultBuffer.append(formatChartStr);
                        }
                    }

                    return resultBuffer.toString();
                }
            }
        }
    }

    private Field getField(Class clazz, String name) {
        try {
            Field field = clazz.getDeclaredField(name);
            return field;
        } catch (NoSuchFieldException var5) {
            Class superClass = clazz.getSuperclass();
            return superClass != null && !"java.lang.Object".equals(superClass.getName()) ? this.getField(superClass, name) : null;
        }
    }
}