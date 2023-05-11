package cn.xlucky.framework.common.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @Auther: dub
 * @Date: 2019/6/21 09:31
 * @Description: 此日期处理类用来处理多线程情况下的日期冲突和反复创建对象的问题
 */
public class DateUtil {

  /**
   * 锁对象
   */
  private static final Object lockObj = new Object();

  /**
   * 存放不同的日期模板格式的sdf的Map
   */
  private static Map<String, ThreadLocal<SimpleDateFormat>> sdfMap = new HashMap<String, ThreadLocal<SimpleDateFormat>>();

  /**
   * 返回一个ThreadLocal的sdf,每个线程只会new一次sdf
   */
  private static SimpleDateFormat getSdf(final String pattern) {
    ThreadLocal<SimpleDateFormat> tl = sdfMap.get(pattern);
    // 此处的双重判断和同步是为了防止sdfMap这个单例被多次put重复的sdf
    if (tl == null) {
      synchronized (lockObj) {
        tl = sdfMap.get(pattern);
        if (tl == null) {
          // 只有Map中还没有这个pattern的sdf才会生成新的sdf并放入map
          // 这里是关键,使用ThreadLocal<SimpleDateFormat>替代原来直接new SimpleDateFormat
          tl = new ThreadLocal<SimpleDateFormat>() {
            @Override
            protected SimpleDateFormat initialValue() {
              System.out.println("thread: " + Thread.currentThread() + " init pattern: " + pattern);
              return new SimpleDateFormat(pattern);
            }
          };
          sdfMap.put(pattern, tl);
        }
      }
    }

    return tl.get();
  }

  /**
   * 是用ThreadLocal<SimpleDateFormat>来获取SimpleDateFormat,这样每个线程只会有一个SimpleDateFormat
   */
  public static String format(Date date, String pattern) {
    return getSdf(pattern).format(date);
  }

  public static Date parse(String dateStr, String pattern) throws ParseException {
    return getSdf(pattern).parse(dateStr);
  }

  /**
   * 获取某个月份天数
   */
  public static int getDaysOfMonth(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTime(date);
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
  }

  /**
   * 将String类型(20191030161040)的时间格式转Date类型
   */
  public static Date string2Date(String dateStr) throws ParseException {
    if (StringUtils.isBlank(dateStr)) {
      throw new IllegalArgumentException("参数错误");
    }
    // 1.切割拼接
    String yearStr = dateStr.substring(0, 4);
    String monthStr = dateStr.substring(4, 6);
    String dayStr = dateStr.substring(6, 8);
    String hourStr = dateStr.substring(8, 10);
    String minuteStr = dateStr.substring(10, 12);
    String secondStr = dateStr.substring(12, 14);
    String patternDate =
        yearStr + "-" + monthStr + "-" + dayStr + " " + hourStr + ":" + minuteStr + ":" + secondStr;
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    return format.parse(patternDate);
  }

}