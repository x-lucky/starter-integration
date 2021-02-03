package cn.xlucky.framework.common.util;

import java.text.MessageFormat;


/**
 * 缓存key组合
 * @author 聂祥
 * @date 2021/2/3
 * @version 1.0.0
 */
public class CacheKey {
    private CacheKey() {
    }

    public static String key(String pattern, Object... arguments) {
        String[] args = new String[arguments.length];
        int idx = 0;
        Object[] var4 = arguments;
        int var5 = arguments.length;

        for(int var6 = 0; var6 < var5; ++var6) {
            Object o = var4[var6];
            args[idx] = String.valueOf(o);
            ++idx;
        }

        return MessageFormat.format(pattern, args);
    }
}