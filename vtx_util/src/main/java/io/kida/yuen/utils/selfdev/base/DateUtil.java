package io.kida.yuen.utils.selfdev.base;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.stream.IntStream;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: DateUtil.java
 * @ClassName: DateUtil
 * @Description:日期时间操作的工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class DateUtil {

    /** DateFormat缓存 */
    private static final Map<String, DateFormat> dateFormatMap = new HashMap<>();

    public static final String yyyy_MM_dd_EN = "yyyy-MM-dd";
    public static final String yyyy_MM_dd_decline = "yyyy/MM/dd";
    public static final String yyyyMMdd_EN = "yyyyMMdd";
    public static final String yyyy_MM_dd_dont = "yyyy.MM.dd";
    public static final String yyyy_MM_EN = "yyyy-MM";
    public static final String yyyyMM_EN = "yyyyMM";
    public static final String yyyy_MM_dd_HH_mm_ss_EN = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm_ss_S_EN = "yyyy-MM-dd HH:mm:ss.S";
    public static final String yyyyMMddHHmmss_EN = "yyyyMMddHHmmss";
    public static final String yyyy_MM_dd_CN = "yyyy年MM月dd日";
    public static final String yyyy_MM_dd_HH_mm_ss_CN = "yyyy年MM月dd日HH时mm分ss秒";
    public static final String yyyy_MM_dd_HH_mm_CN = "yyyy年MM月dd日HH时mm分";
    public static final String BJBOSS_DATE = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    public static final String HH_mm_ss_EN = "HH:mm:ss";

    /**
     * 
     * @MethodName: getDateFormat
     * @Description: 获取DateFormat
     * @author yuanzhenhui
     * @param formatStr
     * @return DateFormat
     * @date 2023-10-11 04:13:42
     */
    public static DateFormat getDateFormat(String formatStr) {
        DateFormat df = dateFormatMap.get(formatStr);
        if (df == null) {
            df = new SimpleDateFormat(formatStr);
            dateFormatMap.put(formatStr, df);
        }
        return df;
    }

    /**
     * 
     * @MethodName: getDate
     * @Description: 获取时间节点
     * @author yuanzhenhui
     * @return Date
     * @date 2023-10-11 04:13:52
     */
    public static Date getDate() {
        return Calendar.getInstance().getTime();
    }

    /**
     * 
     * @MethodName: getDays
     * @Description: 获取两日期之间的所有日期
     * @author yuanzhenhui
     * @param startTime
     * @param endTime
     * @param fmt
     * @return List<String>
     * @date 2023-10-11 04:14:02
     */
    public static List<String> getDays(String startTime, String endTime, String fmt) {
        // 返回的日期集合
        List<String> days = new ArrayList<>();

        DateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd_EN);
        DateFormat dateFormat1 = new SimpleDateFormat(fmt);
        try {
            Date start = dateFormat.parse(startTime);
            Date end = dateFormat.parse(endTime);

            Calendar tempStart = Calendar.getInstance();
            tempStart.setTime(start);

            Calendar tempEnd = Calendar.getInstance();
            tempEnd.setTime(end);
            tempEnd.add(Calendar.DATE, +1);// 日期加1(包含结束)
            while (tempStart.before(tempEnd)) {
                days.add(dateFormat1.format(tempStart.getTime()));
                tempStart.add(Calendar.DAY_OF_YEAR, 1);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.getDays] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return days;
    }

    /**
     * 
     * @MethodName: getDate
     * @Description: 按照默认formatStr的格式，转化dateTimeStr为Date类型 dateTimeStr必须是formatStr的形式
     * @author yuanzhenhui
     * @param dateTimeStr
     * @param formatStr
     * @return Date
     * @date 2023-04-17 02:56:35
     */
    public static Date getDate(String dateTimeStr, String formatStr) {
        Date date = null;
        try {
            if (dateTimeStr != null && !dateTimeStr.equals("")) {
                DateFormat sdf = DateUtil.getDateFormat(formatStr);
                date = sdf.parse(dateTimeStr);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.getDate] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: string2Date
     * @Description: 字符串转日期
     * @author yuanzhenhui
     * @param strDate
     * @param pattern
     * @return Date
     * @date 2023-04-17 02:56:53
     */
    public static Date string2Date(String strDate, String pattern) {
        Date date = null;
        try {
            if (strDate != null && !strDate.equals("")) {
                if (pattern == null || pattern.equals("")) {
                    pattern = yyyy_MM_dd_EN;
                }
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                date = sdf.parse(strDate);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.string2Date] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: convertDate
     * @Description: 转化dateTimeStr为Date类型
     * @author yuanzhenhui
     * @param dateTimeStr
     * @return Date
     * @date 2023-04-17 02:57:10
     */
    public static Date convertDate(String dateTimeStr) {
        Date date = null;
        try {
            if (dateTimeStr != null && !dateTimeStr.equals("")) {
                DateFormat sdf = DateUtil.getDateFormat(yyyy_MM_dd_EN);
                date = sdf.parse(dateTimeStr);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.convertDate] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: getDate
     * @Description: 按照默认显示日期时间的格式"yyyy-MM-dd"，转化dateTimeStr为Date类型 dateTimeStr必须是"yyyy-MM-dd"的形式
     * @author yuanzhenhui
     * @param dateTimeStr
     * @return Date
     * @date 2023-04-17 02:57:25
     */
    public static Date getDate(String dateTimeStr) {
        return getDate(dateTimeStr, yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: transferDate
     * @Description: 将YYYYMMDD转换成Date日期
     * @author yuanzhenhui
     * @param date
     * @return Date
     * @date 2023-04-17 02:57:38
     */
    public static Date transferDate(String date) {
        Date dateTmp = null;
        if (date != null && date.length() == 8) {
            String con = "-";
            String yyyy = date.substring(0, 4);
            String mm = date.substring(4, 6);
            String dd = date.substring(6, 8);
            int month = Integer.parseInt(mm);
            int day = Integer.parseInt(dd);
            if (!(month < 1 || month > 12 || day < 1 || day > 31)) {
                String str = yyyy + con + mm + con + dd;
                dateTmp = DateUtil.getDate(str, yyyy_MM_dd_EN);
            }
        }
        return dateTmp;
    }

    /**
     * 
     * @MethodName: dateToDateString
     * @Description: 将Date转换成字符串“yyyy-mm-dd hh:mm:ss”的字符串
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-04-17 02:57:52
     */
    public static String dateToDateString(Date date) {
        return dateToDateString(date, yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 
     * @MethodName: dateToDateFullString
     * @Description: 将Date转换成字符串“yyyymmddhhmmss”的字符串
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-04-17 02:58:10
     */
    public static String dateToDateFullString(Date date) {
        String reVal = null;
        if (null != date) {
            reVal = dateToDateString(date, yyyyMMddHHmmss_EN);
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: dateToDateString
     * @Description: 将Date转换成formatStr格式的字符串
     * @author yuanzhenhui
     * @param date
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:23:12
     */
    public static String dateToDateString(Date date, String formatStr) {
        DateFormat df = getDateFormat(formatStr);
        return df.format(date);
    }

    /**
     * 
     * @MethodName: stringToDateString
     * @Description: 将String转换成formatStr格式的字符串
     * @author yuanzhenhui
     * @param date
     * @param formatStr1
     * @param formatStr2
     * @return String
     * @date 2023-10-11 04:23:21
     */
    public static String stringToDateString(String date, String formatStr1, String formatStr2) {
        Date d = getDate(date, formatStr1);
        DateFormat df = getDateFormat(formatStr2);
        return df.format(d);
    }

    /**
     * 
     * @MethodName: getCurDate
     * @Description: 获取当前日期yyyy-MM-dd的形式
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:23:30
     */
    public static String getCurDate() {
        return dateToDateString(new Date(), yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: getCurDate
     * @Description: 获取当前日期
     * @author yuanzhenhui
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:23:45
     */
    public static String getCurDate(String formatStr) {
        return dateToDateString(new Date(), formatStr);
    }

    /**
     * 
     * @MethodName: getCurCNDate
     * @Description: 获取当前日期yyyy年MM月dd日的形式
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:23:58
     */
    public static String getCurCNDate() {
        return dateToDateString(new Date(), yyyy_MM_dd_CN);
    }

    /**
     * 
     * @MethodName: getCurDateTime
     * @Description: 获取当前日期时间yyyy-MM-dd HH:mm:ss的形式
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:24:06
     */
    public static String getCurDateTime() {
        return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 
     * @MethodName: getCurZhCNDateTime
     * @Description: 获取当前日期时间yyyy年MM月dd日HH时mm分ss秒的形式
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:24:14
     */
    public static String getCurZhCNDateTime() {
        return dateToDateString(new Date(), yyyy_MM_dd_HH_mm_ss_CN);
    }

    /**
     * 
     * @MethodName: compareDateStr
     * @Description: 比较两个"yyyy-MM-dd HH:mm:ss"格式的日期，之间相差多少毫秒,time2-time1
     * @author yuanzhenhui
     * @param time1
     * @param time2
     * @return long
     * @date 2023-10-11 04:24:25
     */
    public static long compareDateStr(String time1, String time2) {
        Date d1 = getDate(time1);
        Date d2 = getDate(time2);
        return d2.getTime() - d1.getTime();
    }

    /**
     * 
     * @MethodName: compareDateStr
     * @Description: 比较任意格式时间相差毫秒数
     * @author yuanzhenhui
     * @param time1
     * @param time2
     * @param format
     * @return long
     * @date 2023-10-11 04:28:38
     */
    public static long compareDateStr(String time1, String time2, String format) {
        Date d1 = getDate(time1, format);
        Date d2 = getDate(time2, format);
        return d2.getTime() - d1.getTime();
    }

    /**
     * 
     * @MethodName: compareDateNow
     * @Description: 比较起始时间与当前时间相差毫秒数
     * @author yuanzhenhui
     * @param time
     * @param format
     * @return long
     * @date 2023-10-11 04:28:47
     */
    public static long compareDateNow(String time, String format) {
        Date date = getDate(time, format);
        return System.currentTimeMillis() - date.getTime();
    }

    /**
     * 
     * @MethodName: compareDateStr
     * @Description: 比较两个"yyyy-MM-dd HH:mm:ss"格式的日期，之间相差多少毫秒,time2-time1
     * @author yuanzhenhui
     * @param time1
     * @param time2
     * @return long
     * @date 2023-10-11 04:28:55
     */
    public static long compareDateStr(Date time1, Date time2) {
        return time2.getTime() - time1.getTime();
    }

    /**
     * 
     * @MethodName: isTimeBefor
     * @Description: nows时间大于date时间 为true
     * @author yuanzhenhui
     * @param nows
     * @param date
     * @return boolean
     * @date 2023-10-11 04:29:44
     */
    public static boolean isTimeBefor(Date nows, Date date) {
        return nows.getTime() - date.getTime() > 0 ? true : false;
    }

    /**
     * 
     * @MethodName: getMicroSec
     * @Description: 将小时数换算成返回以毫秒为单位的时间
     * @author yuanzhenhui
     * @param hours
     * @return long
     * @date 2023-10-11 04:29:59
     */
    public static long getMicroSec(BigDecimal hours) {
        return hours.multiply(new BigDecimal(3600 * 1000)).longValue();
    }

    /**
     * 
     * @MethodName: getDateStringOfYear
     * @Description: 获取当前日期years年后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param years
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:30:08
     */
    public static String getDateStringOfYear(int years, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.YEAR, years);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateStringOfMon
     * @Description: 获取当前日期mon月后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param months
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:30:25
     */
    public static String getDateStringOfMon(int months, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.MONTH, months);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateStringOfDay
     * @Description: 获取当前日期days天后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param days
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:30:45
     */
    public static String getDateStringOfDay(int days, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.DATE, days);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: theDateIsToday
     * @Description: 判断日期是否是今天
     * @author yuanzhenhui
     * @param date
     * @param format
     * @return int
     * @date 2023-10-11 04:30:54
     */
    public static int theDateIsToday(String date, String format) {
        String theDate = stringToDateString(date, format, yyyyMMdd_EN);
        String today = getDateStringOfDay(0, yyyyMMdd_EN);
        return theDate.equals(today) ? 1 : 0;
    }

    /**
     * 
     * @MethodName: getDateStringOfHour
     * @Description: 获取当前日期hours小时后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param hours
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:31:06
     */
    public static String getDateStringOfHour(int hours, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.HOUR_OF_DAY, hours);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateOfMon
     * @Description: 获取指定日期mon月后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param date
     * @param mon
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:31:43
     */
    public static String getDateOfMon(String date, int mon, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(DateUtil.getDate(date, formatStr));
        now.add(Calendar.MONTH, mon);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateOfDay
     * @Description: 获取指定日期day天后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param date
     * @param day
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:31:57
     */
    public static String getDateOfDay(String date, int day, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(DateUtil.getDate(date, formatStr));
        now.add(Calendar.DATE, day);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDate
     * @Description: 获取日期
     * @author yuanzhenhui
     * @param beginDate
     * @param ds
     * @return Date
     * @date 2023-10-11 04:32:12
     */
    public static Date getDate(Date beginDate, int ds) {
        Date dateTmp = null;
        try {
            if (ds == 0) {
                dateTmp = new Date();
            } else {
                SimpleDateFormat dft = new SimpleDateFormat(yyyy_MM_dd_EN);
                Calendar date = Calendar.getInstance();
                date.setTime(beginDate);
                date.set(Calendar.DATE, date.get(Calendar.DATE) - ds);
                dateTmp = dft.parse(dft.format(date.getTime()));
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.getDate] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
            dateTmp = new Date();
        }
        return dateTmp;

    }

    /**
     * 
     * @MethodName: getAfterNDays
     * @Description: 获取指定时间之后N天的日期
     * @author yuanzhenhui
     * @param date
     * @param n
     * @param formateStr
     * @return String
     * @date 2023-10-11 04:32:22
     */
    public static String getAfterNDays(Date date, int n, String formateStr) {
        SimpleDateFormat sdf = new SimpleDateFormat(formateStr);
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, n);
        return sdf.format(calendar.getTime());
    }

    /**
     * 
     * @MethodName: getDateOfMin
     * @Description: 获取指定日期mins分钟后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param date
     * @param mins
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:32:31
     */
    public static String getDateOfMin(String date, int mins, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(DateUtil.getDate(date, formatStr));
        now.add(Calendar.SECOND, mins * 60);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateOfMin
     * @Description: 获取指定日期mins分钟后的一个日期
     * @author yuanzhenhui
     * @param date
     * @param mins
     * @return Date
     * @date 2023-10-11 04:32:54
     */
    public static Date getDateOfMin(Date date, int mins) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(date);
        now.add(Calendar.SECOND, mins * 60);
        return now.getTime();
    }

    /**
     * 
     * @MethodName: getDateStringOfMin
     * @Description: 获取当前日期mins分钟后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param mins
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:33:04
     */
    public static String getDateStringOfMin(int mins, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.MINUTE, mins);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getDateOfMin
     * @Description: 获取当前日期mins分钟后的一个日期
     * @author yuanzhenhui
     * @param mins
     * @return Date
     * @date 2023-10-11 04:33:15
     */
    public static Date getDateOfMin(int mins) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.MINUTE, mins);
        return now.getTime();
    }

    /**
     * 
     * @MethodName: getDateStringOfSec
     * @Description: 获取当前日期sec秒后的一个(formatStr)的字符串
     * @author yuanzhenhui
     * @param sec
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:33:24
     */
    public static String getDateStringOfSec(int sec, String formatStr) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(new Date());
        now.add(Calendar.SECOND, sec);
        return dateToDateString(now.getTime(), formatStr);
    }

    /**
     * 
     * @MethodName: getMonthDay
     * @Description: 获得指定日期月份的天数
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:33:33
     */
    public static int getMonthDay(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);

    }

    /**
     * 
     * @MethodName: getCurentMonthDay
     * @Description: 获得系统当前月份的天数
     * @author yuanzhenhui
     * @return int
     * @date 2023-10-11 04:33:44
     */
    public static int getCurentMonthDay() {
        Date date = Calendar.getInstance().getTime();
        return getMonthDay(date);
    }

    /**
     * 
     * @MethodName: getMonthDay
     * @Description: 获得指定日期月份的天数 yyyy-mm-dd
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:33:58
     */
    public static int getMonthDay(String date) {
        Date strDate = getDate(date, yyyy_MM_dd_EN);
        return getMonthDay(strDate);
    }

    /**
     * 
     * @MethodName: getYear
     * @Description: 获取19xx,20xx形式的年
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:34:07
     */
    public static int getYear(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.YEAR);
    }

    /**
     * 
     * @MethodName: getMonth
     * @Description: 获取月份，1-12月
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:34:18
     */
    public static int getMonth(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.MONTH) + 1;
    }

    /**
     * 
     * @MethodName: getDay
     * @Description: 获取xxxx-xx-xx的日
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:34:26
     */
    public static int getDay(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 
     * @MethodName: getHour
     * @Description: 获取Date中的小时(24小时)
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:34:58
     */
    public static int getHour(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 
     * @MethodName: getMin
     * @Description: 获取Date中的分钟
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:35:09
     */
    public static int getMin(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.MINUTE);
    }

    /**
     * 
     * @MethodName: getSecond
     * @Description: 获取Date中的秒
     * @author yuanzhenhui
     * @param d
     * @return int
     * @date 2023-10-11 04:35:21
     */
    public static int getSecond(Date d) {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        now.setTime(d);
        return now.get(Calendar.SECOND);
    }

    /**
     * 
     * @MethodName: getMondayOfThisWeek
     * @Description: 得到本周周一
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:35:28
     */
    public static String getMondayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 1);
        return dateToDateString(c.getTime(), yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: getSundayOfThisWeek
     * @Description: 得到本周周日
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:35:44
     */
    public static String getSundayOfThisWeek() {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + 7);
        return dateToDateString(c.getTime());
    }

    /**
     * 
     * @MethodName: getDayOfThisWeek
     * @Description: 得到本周周(*)
     * @author yuanzhenhui
     * @param num
     * @return String
     * @date 2023-10-11 04:40:42
     */
    public static String getDayOfThisWeek(int num) {
        Calendar c = Calendar.getInstance();
        int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (day_of_week == 0) {
            day_of_week = 7;
        }
        c.add(Calendar.DATE, -day_of_week + num);
        return dateToDateString(c.getTime(), yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: getDayOfThisMoon
     * @Description: 得到本月指定天
     * @author yuanzhenhui
     * @param num
     * @return String
     * @date 2023-10-11 04:40:53
     */
    public static String getDayOfThisMoon(String num) {
        String date = dateToDateString(new Date(), yyyy_MM_EN);
        return date + "-" + num;
    }

    /**
     * 
     * @MethodName: getQuotByDays
     * @Description: 获取两个日期相差的天数
     * @author yuanzhenhui
     * @param beginDate
     * @param endDate
     * @return long
     * @date 2023-10-11 04:41:01
     */
    public static long getQuotByDays(String beginDate, String endDate) {
        long quot = 0;
        DateFormat df = getDateFormat(yyyy_MM_dd_EN);
        try {
            Date d1 = df.parse(beginDate);
            Date d2 = df.parse(endDate);
            quot = d2.getTime() - d1.getTime();
            quot = quot / 1000 / 60 / 60 / 24;
        } catch (ParseException e) {
            log.error("func[DateUtil.getQuotByDays] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return quot;
    }

    /**
     * 
     * @MethodName: getDateAddDay
     * @Description: 根据日期追加的天数，得到一个新日期
     * @author yuanzhenhui
     * @param date
     * @param days
     * @param format
     * @return String
     * @date 2023-10-11 04:41:10
     */
    public static String getDateAddDay(String date, int days, String format) {
        DateFormat df = getDateFormat(format);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(date));
            cal.set(Calendar.DAY_OF_YEAR, cal.get(Calendar.DAY_OF_YEAR) + days);

            date = df.format(cal.getTime());
        } catch (ParseException e) {
            log.error("func[DateUtil.getDateAddDay] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: getLastDayOfCurrMonth
     * @Description: 获取当前月的最后一天
     * @author yuanzhenhui
     * @return Date
     * @date 2023-10-11 04:41:18
     */
    public static Date getLastDayOfCurrMonth() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, 1);
        cal.set(Calendar.DAY_OF_MONTH, 0);
        return cal.getTime();
    }

    /**
     * 
     * @MethodName: getDateAddMonth
     * @Description: 根据日期追加的天数，得到一个新日期
     * @author yuanzhenhui
     * @param date
     * @param m
     * @return String
     * @date 2023-10-11 04:41:26
     */
    public static String getDateAddMonth(String date, int m) {
        DateFormat df = getDateFormat(yyyyMM_EN);
        try {
            Calendar cal = Calendar.getInstance();
            cal.setTime(df.parse(date));
            cal.add(Calendar.MONTH, m);
            date = df.format(cal.getTime());
        } catch (ParseException e) {
            log.error("func[DateUtil.getDateAddMonth] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: getFirstDayOfMonth
     * @Description: 获取指定年月的第一天
     * @author yuanzhenhui
     * @param year
     * @param month
     * @return String
     * @date 2023-10-11 04:41:34
     */
    public static String getFirstDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最小天数
        int lastDay = cal.getActualMinimum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        DateFormat df = getDateFormat(yyyy_MM_dd_EN);
        return df.format(cal.getTime());
    }

    /**
     * 
     * @MethodName: getLastDayOfMonth
     * @Description: 获取指定年月的第一天
     * @author yuanzhenhui
     * @param year
     * @param month
     * @return String
     * @date 2023-10-11 04:41:45
     */
    public static String getLastDayOfMonth(int year, int month) {
        Calendar cal = Calendar.getInstance();
        // 设置年份
        cal.set(Calendar.YEAR, year);
        // 设置月份
        cal.set(Calendar.MONTH, month - 1);
        // 获取某月最大天数
        int lastDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        // 设置日历中月份的最大天数
        cal.set(Calendar.DAY_OF_MONTH, lastDay);
        // 格式化日期
        DateFormat df = getDateFormat(yyyy_MM_dd_EN);
        return df.format(cal.getTime());
    }

    /**
     * 
     * @MethodName: getYesterday
     * @Description: 获取昨天日期
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-10-11 04:41:56
     */
    public static String getYesterday(Date date) {
        DateFormat df = getDateFormat(yyyy_MM_dd_EN);
        Calendar calendar = Calendar.getInstance();
        try {
            calendar.setTime(df.parse(df.format(date)));
            calendar.add(Calendar.DAY_OF_MONTH, -1);
        } catch (Exception e) {
            log.error("func[DateUtil.getYesterday] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return df.format(calendar.getTime());
    }

    /**
     * 
     * @MethodName: getIntToStr
     * @Description: 10位时间戳转时间
     * @author yuanzhenhui
     * @param dateInt
     * @param format
     * @return String
     * @date 2023-10-11 04:42:05
     */
    public static String getIntToStr(String dateInt, String format) {
        DateFormat df = getDateFormat(format);
        long times = Integer.parseInt(dateInt) * 1000L;
        Date date = new Date(times);
        return df.format(date);
    }

    /**
     * 
     * @MethodName: getDateInt
     * @Description: 获取 10位时间戳
     * @author yuanzhenhui
     * @return Integer
     * @date 2023-10-11 04:42:13
     */
    public static Integer getDateInt() {
        return (int)(System.currentTimeMillis() / 1000);
    }

    /**
     * 
     * @MethodName: getLongToStr
     * @Description: 13位时间戳转时间
     * @author yuanzhenhui
     * @param time
     * @param format
     * @return String
     * @date 2023-10-11 04:42:21
     */
    public static String getLongToStr(long time, String format) {
        return dateToDateString(new Date(time), format);
    }

    /**
     * 
     * @MethodName: getIntervalSec
     * @Description: 获取两个小时间的间隔秒杀
     * @author yuanzhenhui
     * @param start
     * @param end
     * @return int
     * @date 2023-10-11 04:42:31
     */
    public static int getIntervalSec(int start, int end) {
        return (end - start) * 60 * 60;
    }

    /**
     * 
     * @MethodName: getMillsStr
     * @Description: 毫秒时间戳毫秒加小数点
     * @author yuanzhenhui
     * @param time
     * @return String
     * @date 2023-10-11 04:42:40
     */
    public static String getMillsStr(long time) {
        String timeStr = String.valueOf(time);
        String suffix = timeStr.substring(0, timeStr.length() - 3);
        String prefix = timeStr.substring(timeStr.length() - 3, timeStr.length());
        return suffix + "." + prefix;
    }

    /**
     * 
     * @MethodName: longToString
     * @Description: 带小数点的毫秒时间戳转时间格式
     * @author yuanzhenhui
     * @param timeStr
     * @param formatStr
     * @return String
     * @date 2023-10-11 04:42:49
     */
    public static String longToString(String timeStr, String formatStr) {
        long times = Long.parseLong(timeStr.replace(".", ""));
        return dateToDateString(new Date(times), formatStr);
    }

    /**
     * 
     * @MethodName: getTodayTime
     * @Description: 获取当天起始时间
     * @author yuanzhenhui
     * @return Long
     * @date 2023-10-11 04:42:58
     */
    public static Long getTodayTime() {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, 0);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    /**
     * 
     * @MethodName: getTodayInt
     * @Description: 获取今天时间戳
     * @author yuanzhenhui
     * @return Integer
     * @date 2023-10-11 04:43:06
     */
    public static Integer getTodayInt() {
        return (int)(getTodayTime() / 1000);
    }

    /**
     * 
     * @MethodName: getEndTime
     * @Description: 获取当天结束时间
     * @author yuanzhenhui
     * @return Long
     * @date 2023-10-11 04:43:15
     */
    public static Long getEndTime() {
        Calendar todayEnd = Calendar.getInstance();
        todayEnd.set(Calendar.HOUR, 23);
        todayEnd.set(Calendar.MINUTE, 59);
        todayEnd.set(Calendar.SECOND, 59);
        todayEnd.set(Calendar.MILLISECOND, 999);
        return todayEnd.getTime().getTime();
    }

    /**
     * 
     * @MethodName: getTomorrowInt
     * @Description: 获取明天的时间戳
     * @author yuanzhenhui
     * @return Integer
     * @date 2023-10-11 04:43:25
     */
    public static Integer getTomorrowInt() {
        return (int)(getTomorrowTime() / 1000);
    }

    /**
     * 
     * @MethodName: getTomorrowTime
     * @Description: 获取第二天起始时间
     * @author yuanzhenhui
     * @return Long
     * @date 2023-10-11 04:43:33
     */
    public static Long getTomorrowTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime().getTime();
    }

    /**
     * 
     * @MethodName: getPointHourTime
     * @Description: 获取当天指定小时的时间
     * @author yuanzhenhui
     * @param hour
     * @return Long
     * @date 2023-10-11 04:43:51
     */
    public static Long getPointHourTime(int hour) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    /**
     * 
     * @MethodName: getPointDateHourTime
     * @Description: 获取当天n天后的h小时
     * @author yuanzhenhui
     * @param days
     * @param hour
     * @return Long
     * @date 2023-10-11 04:44:03
     */
    public static Long getPointDateHourTime(int days, int hour) {
        Calendar todayStart = Calendar.getInstance();
        todayStart.add(Calendar.DATE, days);
        todayStart.set(Calendar.HOUR_OF_DAY, hour);
        todayStart.set(Calendar.MINUTE, 0);
        todayStart.set(Calendar.SECOND, 0);
        todayStart.set(Calendar.MILLISECOND, 0);
        return todayStart.getTime().getTime();
    }

    /**
     * 
     * @MethodName: hourTosec
     * @Description: 时分秒转成秒数
     * @author yuanzhenhui
     * @param time
     * @return Integer
     * @date 2023-10-11 04:44:12
     */
    public static Integer hourTosec(String time) {
        if ("null".equals(time) || StringUtil.isEmpty(time)) {
            return null;
        }
        if (time.length() <= 5) {
            time += ":00";
        }
        int index1 = time.indexOf(":");
        int index2 = time.indexOf(":", index1 + 1);
        int hh = Integer.parseInt(time.substring(0, index1));
        int mi = Integer.parseInt(time.substring(index1 + 1, index2));
        int ss = Integer.parseInt(time.substring(index2 + 1));
        return hh * 60 * 60 + mi * 60 + ss;
    }

    /**
     * 
     * @MethodName: minTosec
     * @Description: 时分秒转成秒数
     * @author yuanzhenhui
     * @param time
     * @return Integer
     * @date 2023-10-11 04:44:21
     */
    public static Integer minTosec(String time) {
        if (time.length() <= 5) {
            time += ":00";
        }
        int index1 = time.indexOf(":");
        int index2 = time.indexOf(":", index1 + 1);
        int mi = Integer.parseInt(time.substring(0, index1));
        int ss = Integer.parseInt(time.substring(index1 + 1, index2));
        return mi * 60 + ss;
    }

    /**
     * 
     * @MethodName: isDate
     * @Description: 判断传入字符串是否日期
     * @author yuanzhenhui
     * @param dateTimeStr
     * @param formatStr
     * @return boolean
     * @date 2023-10-11 04:44:30
     */
    public static boolean isDate(String dateTimeStr, String formatStr) {
        DateFormat df = getDateFormat(formatStr);
        try {
            df.parse(dateTimeStr);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 
     * @MethodName: isDate
     * @Description: 校验输入字符是否日期类型
     * @author yuanzhenhui
     * @param dateStr
     * @return Date
     * @date 2023-10-11 04:44:39
     */
    public static Date isDate(String dateStr) {
        String[] dateFormatArr = {yyyy_MM_dd_decline, yyyy_MM_dd_EN, yyyyMMdd_EN, yyyy_MM_dd_dont};
        for (int i = 0; i < dateFormatArr.length; i++) {
            Date tempDate = isDateOut(dateStr, dateFormatArr[i]);
            if (null != tempDate) {
                return tempDate;
            }
        }
        return null;
    }

    /**
     * 
     * @MethodName: isDateOut
     * @Description: 验证输入的文本信息日期是否合
     * @author yuanzhenhui
     * @param dateStr
     * @param patternString
     * @return Date
     * @date 2023-10-11 04:44:50
     */
    public static Date isDateOut(String dateStr, String patternString) {
        Date date = null;
        try {
            SimpleDateFormat formatDate =
                new SimpleDateFormat(StringUtil.isEmpty(patternString) ? yyyy_MM_dd_EN : patternString);
            formatDate.setLenient(false);
            ParsePosition pos = new ParsePosition(0);
            Date tempDate = formatDate.parse(dateStr, pos);
            tempDate.getTime();
            date = tempDate;
        } catch (Exception e) {
            log.error("func[DateUtil.isDateOut] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: isInDate
     * @Description: 判断时间是否在时间段内
     * @author yuanzhenhui
     * @param strDate
     * @param strDateBegin
     * @param strDateEnd
     * @return boolean
     * @date 2023-10-11 04:45:22
     */
    public static boolean isInDate(String strDate, String strDateBegin, String strDateEnd) {
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(strDate.substring(11, 13));
        int strDateM = Integer.parseInt(strDate.substring(14, 16));
        int strDateS = Integer.parseInt(strDate.substring(17, 19));
        // 截取开始时间时分秒
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(3, 5));
        int strDateBeginS = Integer.parseInt(strDateBegin.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(3, 5));
        int strDateEndS = Integer.parseInt(strDateEnd.substring(6, 8));
        if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
            // 当前时间小时数在开始时间和结束时间小时数之间
            if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM && strDateS >= strDateBeginS
                && strDateS <= strDateEndS) {
                return true;
            }
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
            else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM == strDateEndM
                && strDateS <= strDateEndS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 
     * @MethodName: isInDate
     * @Description: 判断时间是否在时间段内
     * @author yuanzhenhui
     * @param date
     * @param strDateBegin
     * @param strDateEnd
     * @return boolean
     * @date 2023-10-11 04:45:37
     */
    public static boolean isInDate(Date date, String strDateBegin, String strDateEnd) {
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_EN);
        String strDate = sdf.format(date);
        // 截取当前时间时分秒
        int strDateH = Integer.parseInt(strDate.substring(11, 13));
        int strDateM = Integer.parseInt(strDate.substring(14, 16));
        int strDateS = Integer.parseInt(strDate.substring(17, 19));
        // 截取开始时间时分秒
        int strDateBeginH = Integer.parseInt(strDateBegin.substring(0, 2));
        int strDateBeginM = Integer.parseInt(strDateBegin.substring(3, 5));
        int strDateBeginS = Integer.parseInt(strDateBegin.substring(6, 8));
        // 截取结束时间时分秒
        int strDateEndH = Integer.parseInt(strDateEnd.substring(0, 2));
        int strDateEndM = Integer.parseInt(strDateEnd.substring(3, 5));
        int strDateEndS = Integer.parseInt(strDateEnd.substring(6, 8));
        if ((strDateH >= strDateBeginH && strDateH <= strDateEndH)) {
            // 当前时间小时数在开始时间和结束时间小时数之间
            if (strDateH > strDateBeginH && strDateH < strDateEndH) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM >= strDateBeginM && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数等于开始时间小时数，分钟数等于开始时间分钟数，秒数在开始和结束之间
            } else if (strDateH == strDateBeginH && strDateM == strDateBeginM && strDateS >= strDateBeginS
                && strDateS <= strDateEndS) {
                return true;
            }
            // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数小等于结束时间分钟数
            else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM <= strDateEndM) {
                return true;
                // 当前时间小时数大等于开始时间小时数，等于结束时间小时数，分钟数等于结束时间分钟数，秒数小等于结束时间秒数
            } else if (strDateH >= strDateBeginH && strDateH == strDateEndH && strDateM == strDateEndM
                && strDateS <= strDateEndS) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 
     * @MethodName: isInDate
     * @Description: 判断指定时期是否在范围之内
     * @author yuanzhenhui
     * @param date
     * @param dateBegin
     * @param dateEnd
     * @return boolean
     * @date 2023-10-11 04:45:48
     */
    public static boolean isInDate(Date date, Date dateBegin, Date dateEnd) {
        boolean flag = false;
        Long curtDate = date.getTime();
        Long beginDate = dateBegin.getTime();
        Long endDate = dateEnd.getTime();
        if (curtDate > beginDate && curtDate < endDate) {
            flag = true;
        } else if (curtDate.equals(beginDate) || curtDate.equals(endDate)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isInTime
     * @Description: 判断指定时间是否在时间区间之内
     * @author yuanzhenhui
     * @param time
     * @param begin
     * @param end
     * @return boolean
     * @date 2023-10-11 04:45:58
     */
    public static boolean isInTime(int time, int begin, int end) {
        return (time >= begin && time < end) ? true : false;
    }

    /**
     * 
     * @MethodName: getMinutest
     * @Description: 获取分钟测试
     * @author yuanzhenhui
     * @param begin
     * @param format
     * @return int
     * @date 2023-10-11 04:46:07
     */
    public static int getMinutest(String begin, String format) {
        String nowMinutes = DateUtil.getCurDate("HH:mm");
        long time = DateUtil.compareDateStr("09:00", nowMinutes, "HH:mm");
        return (int)time;
    }

    /**
     * 
     * @MethodName: getPastDate
     * @Description: 获取过去的日期
     * @author yuanzhenhui
     * @param past
     * @return String
     * @date 2023-10-11 04:46:15
     */
    public static String getPastDate(int past) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - past);
        Date today = calendar.getTime();
        SimpleDateFormat format = new SimpleDateFormat(yyyy_MM_dd_EN);
        return format.format(today);
    }

    /**
     * 
     * @MethodName: getPastDayList
     * @Description: 获取过去日期的时间列表
     * @author yuanzhenhui
     * @param intervals
     * @return List<String>
     * @date 2023-10-11 04:46:23
     */
    public static List<String> getPastDayList(int intervals) {
        List<String> pastDaysList = new ArrayList<>();
        IntStream.range(0, intervals).forEach(i -> pastDaysList.add(getPastDate(i)));
        return pastDaysList;
    }

    /**
     * 
     * @MethodName: disparityDays
     * @Description: 获取两日期之间相差的天数
     * @author yuanzhenhui
     * @param date1
     * @param date2
     * @return int
     * @date 2023-10-11 04:46:32
     */
    public static int disparityDays(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date1);

        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date2);
        int day1 = cal1.get(Calendar.DAY_OF_YEAR);
        int day2 = cal2.get(Calendar.DAY_OF_YEAR);
        int year1 = cal1.get(Calendar.YEAR);
        int year2 = cal2.get(Calendar.YEAR);
        if (year1 != year2) {// 不同年
            int timeDistance = 0;
            for (int i = year1; i < year2; i++) {
                if (i % 4 == 0 && i % 100 != 0 || i % 400 == 0) { // 闰年
                    timeDistance += 366;
                } else { // 不是闰年
                    timeDistance += 365;
                }
            }
            return timeDistance + (day2 - day1);
        } else { // 同一年
            return day2 - day1;
        }
    }

    /**
     * 
     * @MethodName: date2String
     * @Description: date转字符串
     * @author yuanzhenhui
     * @param date
     * @param pattern
     * @return String
     * @date 2023-10-11 04:46:42
     */
    public static String date2String(Date date, String pattern) {
        String reVal = null;
        if (date != null) {
            SimpleDateFormat sdf =
                new SimpleDateFormat((pattern == null || pattern.equals("")) ? yyyy_MM_dd_HH_mm_ss_EN : pattern);
            reVal = sdf.format(date);
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: string2Timestamp
     * @Description: 字符串转时间戳
     * @author yuanzhenhui
     * @param strDateTime
     * @param pattern
     * @return Timestamp
     * @date 2023-10-11 04:46:50
     */
    public static Timestamp string2Timestamp(String strDateTime, String pattern) {
        Timestamp ts = null;
        try {
            if (strDateTime != null && !strDateTime.equals("")) {
                SimpleDateFormat sdf =
                    new SimpleDateFormat((pattern == null || pattern.equals("")) ? yyyy_MM_dd_HH_mm_ss_EN : pattern);
                Date date = sdf.parse(strDateTime);
                ts = new Timestamp(date.getTime());
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.string2Timestamp] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return ts;
    }

    /**
     * 
     * @MethodName: disparityMonth
     * @Description: 中间间隔的月份
     * @author yuanzhenhui
     * @param startDate
     * @param endDate
     * @return
     * @throws ParseException
     *             List<String>
     * @date 2023-10-11 04:46:59
     */
    public static List<String> disparityMonth(String startDate, String endDate) throws ParseException {
        List<String> list = new ArrayList<>();
        SimpleDateFormat sdf = new SimpleDateFormat(yyyy_MM_EN);
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(sdf.parse(startDate));
        c2.setTime(sdf.parse(endDate));
        int year = c2.get(Calendar.YEAR) - c1.get(Calendar.YEAR);
        int month = c2.get(Calendar.MONTH) + year * 12 - c1.get(Calendar.MONTH);
        for (int i = 0; i <= month; i++) {
            c1.setTime(sdf.parse(startDate));
            c1.add(c1.MONTH, i);
            list.add(sdf.format(c1.getTime()));
        }
        return list;
    }

    /**
     * 
     * @MethodName: currentTimestamp
     * @Description: 获取当前时间戳
     * @author yuanzhenhui
     * @return Timestamp
     * @date 2023-10-11 04:47:08
     */
    public static Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    /**
     * 
     * @MethodName: currentTimestamp2String
     * @Description: 获取字符串的当前时间戳
     * @author yuanzhenhui
     * @param pattern
     * @return String
     * @date 2023-10-11 04:47:15
     */
    public static String currentTimestamp2String(String pattern) {
        return timestamp2String(currentTimestamp(), pattern);
    }

    /**
     * 
     * @MethodName: timestamp2String
     * @Description: 时间戳转字符串
     * @author yuanzhenhui
     * @param timestamp
     * @param pattern
     * @return String
     * @date 2023-10-11 04:47:23
     */
    public static String timestamp2String(Timestamp timestamp, String pattern) {
        String reVal = null;
        if (timestamp != null) {
            SimpleDateFormat sdf =
                new SimpleDateFormat((pattern == null || pattern.equals("")) ? yyyy_MM_dd_HH_mm_ss_EN : pattern);
            reVal = sdf.format(new Date(timestamp.getTime()));
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: stringToYear
     * @Description: 获取年份
     * @author yuanzhenhui
     * @param strDest
     * @return String
     * @date 2023-10-11 04:47:33
     */
    public static String stringToYear(String strDest) {
        String reVal = null;
        if (strDest != null && !strDest.equals("")) {
            Date date = string2Date(strDest, yyyy_MM_dd_EN);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            reVal = String.valueOf(c.get(Calendar.YEAR));
        }
        return reVal;

    }

    /**
     * 
     * @MethodName: stringToMonth
     * @Description: 获取月份
     * @author yuanzhenhui
     * @param strDest
     * @return String
     * @date 2023-10-11 04:47:41
     */
    public static String stringToMonth(String strDest) {
        String reVal = null;
        if (strDest != null && !strDest.equals("")) {
            Date date = string2Date(strDest, yyyy_MM_dd_EN);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int month = c.get(Calendar.MONTH);
            month = month + 1;
            if (month < 10) {
                reVal = "0" + month;
            } else {
                reVal = String.valueOf(month);
            }
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: stringToDay
     * @Description: 获取日期
     * @author yuanzhenhui
     * @param strDest
     * @return String
     * @date 2023-10-11 04:47:50
     */
    public static String stringToDay(String strDest) {
        String reVal = null;
        if (strDest != null && !strDest.equals("")) {
            Date date = string2Date(strDest, yyyy_MM_dd_EN);
            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DAY_OF_MONTH);
            if (day < 10) {
                reVal = "0" + day;
            } else {
                reVal = "" + day;
            }
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: getFirstDayOfMonth
     * @Description: 获取每月的第一天
     * @author yuanzhenhui
     * @param c
     * @return Date
     * @date 2023-10-11 04:48:01
     */
    public static Date getFirstDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = 1;
        c.set(year, month, day, 0, 0, 0);
        return c.getTime();
    }

    /**
     * 
     * @MethodName: getLastDayOfMonth
     * @Description: 获取每月的最后一天
     * @author yuanzhenhui
     * @param c
     * @return Date
     * @date 2023-10-11 04:48:09
     */
    public static Date getLastDayOfMonth(Calendar c) {
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH) + 1;
        int day = 1;
        if (month > 11) {
            month = 0;
            year = year + 1;
        }
        c.set(year, month, day - 1, 0, 0, 0);
        return c.getTime();
    }

    /**
     * 
     * @MethodName: date2GregorianCalendarString
     * @Description: 日期转公历字符串
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-10-11 04:48:18
     */
    public static String date2GregorianCalendarString(Date date) {
        String reVal = null;
        try {
            if (date != null) {
                GregorianCalendar ca = new GregorianCalendar();
                ca.setTimeInMillis(date.getTime());
                XMLGregorianCalendar tXMLGregorianCalendar = DatatypeFactory.newInstance().newXMLGregorianCalendar(ca);
                reVal = tXMLGregorianCalendar.normalize().toString();
            }
        } catch (DatatypeConfigurationException e) {
            log.error("func[DateUtil.date2GregorianCalendarString] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: compareDate
     * @Description: 日期的比较
     * @author yuanzhenhui
     * @param firstDate
     * @param secondDate
     * @return boolean
     * @date 2023-10-11 04:48:27
     */
    public static boolean compareDate(Date firstDate, Date secondDate) {
        boolean flag = false;
        if (firstDate != null && secondDate != null) {
            String strFirstDate = date2String(firstDate, yyyy_MM_dd_EN);
            String strSecondDate = date2String(secondDate, yyyy_MM_dd_EN);
            if (strFirstDate.equals(strSecondDate)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: getStartTimeOfDate
     * @Description: 获取开始时间
     * @author yuanzhenhui
     * @param currentDate
     * @return Date
     * @date 2023-10-11 04:48:36
     */
    public static Date getStartTimeOfDate(Date currentDate) {
        Date date = null;
        if (null != currentDate) {
            String strDateTime = date2String(currentDate, yyyy_MM_dd_EN) + " 00:00:00";
            date = string2Date(strDateTime, yyyy_MM_dd_HH_mm_ss_EN);
        }
        return date;
    }

    /**
     * 
     * @MethodName: getEndTimeOfDate
     * @Description: 获取结束时间
     * @author yuanzhenhui
     * @param currentDate
     * @return Date
     * @date 2023-10-11 04:48:43
     */
    public static Date getEndTimeOfDate(Date currentDate) {
        Date date = null;
        if (null != currentDate) {
            String strDateTime = date2String(currentDate, yyyy_MM_dd_EN) + " 59:59:59";
            date = string2Date(strDateTime, yyyy_MM_dd_HH_mm_ss_EN);
        }
        return date;
    }

    /**
     * 
     * @MethodName: getBetweenDays
     * @Description: 计算两个日期的间隔天数
     * @author yuanzhenhui
     * @param startDate
     * @param endDate
     * @return long
     * @date 2023-10-11 04:48:53
     */
    public static long getBetweenDays(String startDate, String endDate) {
        if (endDate == null || startDate == null) {
            return -1;
        }
        Date dateStart = isDate(startDate);
        if (null == dateStart) {
            return -1;
        }
        Date dateEnd = isDate(endDate);
        if (null == dateEnd) {
            return -1;
        }
        return getBetweenDays(dateStart, dateEnd);
    }

    /**
     * 
     * @MethodName: getBetweenDays
     * @Description: 计算两个日期的间隔天数
     * @author yuanzhenhui
     * @param startDate
     * @param endDate
     * @return long
     * @date 2023-10-11 04:49:02
     */
    public static long getBetweenDays(Date startDate, Date endDate) {
        if (endDate == null || startDate == null) {
            return -1;
        }
        Long days = endDate.getTime() - startDate.getTime();
        days = days / (1000 * 60 * 60 * 24);
        return days;
    }

    /**
     * 
     * @MethodName: getAfterDate
     * @Description: 获取与指定日期相差指定 天数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param dayCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:49:13
     */
    public static String getAfterDate(String baseDate, int dayCount, String patternString) {
        int year = Integer.parseInt(baseDate.substring(0, 4));
        int month = Integer.parseInt(baseDate.substring(5, 7));
        int date = Integer.parseInt(baseDate.substring(8, 10));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date);
        calendar.add(Calendar.DATE, dayCount);
        SimpleDateFormat formatter = new SimpleDateFormat(patternString);
        return formatter.format(calendar.getTime());
    }

    /**
     * 
     * @MethodName: getAfterDate
     * @Description: 获取与指定日期相差指定 天数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param dayCount
     * @return String
     * @date 2023-10-11 04:49:22
     */
    public static String getAfterDate(String baseDate, int dayCount) {
        return getAfterDate(baseDate, dayCount, yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 
     * @MethodName: getAfterDate
     * @Description: 获取与指定日期相差指定 天数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param dayCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:49:30
     */
    public static String getAfterDate(Date baseDate, int dayCount, String patternString) {
        return getAfterDate(getDateString(baseDate, yyyy_MM_dd_HH_mm_ss_EN), dayCount, patternString);
    }

    /**
     * 
     * @MethodName: getAfterDate
     * @Description: 获取与指定日期相差指定 天数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param dayCount
     * @return String
     * @date 2023-10-11 04:53:10
     */
    public static String getAfterDate(Date baseDate, int dayCount) {
        return getAfterDate(baseDate, dayCount, yyyy_MM_dd_HH_mm_ss_EN);
    }

    /**
     * 
     * @MethodName: getAfterMonth
     * @Description: 获取与指定日期相差指定 月数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param monthCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:53:03
     */
    public static String getAfterMonth(String baseDate, int monthCount, String patternString) {
        int year = Integer.parseInt(baseDate.substring(0, 4));
        int month = Integer.parseInt(baseDate.substring(5, 7));
        int date = Integer.parseInt(baseDate.substring(8, 10));
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date);
        calendar.add(Calendar.MONTH, monthCount);
        SimpleDateFormat formatter = new SimpleDateFormat(patternString);
        return formatter.format(calendar.getTime());
    }

    /**
     * 
     * @MethodName: getAfterMonth
     * @Description: 获取与指定日期相差指定 月数 的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param monthCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:52:50
     */
    public static String getAfterMonth(Date baseDate, int monthCount, String patternString) {
        return getAfterMonth(getDateString(baseDate, yyyy_MM_dd_HH_mm_ss_EN), monthCount, patternString);
    }

    /**
     * 
     * @MethodName: getEndDate
     * @Description: 获取与指定日期相差指定 月数 并减去天数的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param monthCount
     * @param dateCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:52:42
     */
    public static String getEndDate(String baseDate, int monthCount, int dateCount, String patternString) {
        int day = Integer.parseInt(baseDate.substring(8, 10));
        String endDate = getAfterMonth(baseDate, monthCount, patternString);
        int endDay = Integer.parseInt(endDate.substring(8, 10));
        // 说明日期没变
        if (endDay == day) {
            // 月数为正则为减一
            if (monthCount > 0) {
                endDate = getAfterDate(endDate, dateCount, patternString);
            } else {
                endDate = getAfterDate(endDate, dateCount, patternString);
            }
        } else { // 日期已变
            if (monthCount < 0) {
                endDate = getAfterDate(endDate, dateCount, patternString);
            }
        }
        return endDate;
    }

    /**
     * 
     * @MethodName: getEndDate
     * @Description: 获取与指定日期相差指定 月数 并减去天数的日期
     * @author yuanzhenhui
     * @param baseDate
     * @param monthCount
     * @param dateCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:52:32
     */
    public static String getEndDate(Date baseDate, int monthCount, int dateCount, String patternString) {
        return getEndDate(getDateString(baseDate, yyyy_MM_dd_HH_mm_ss_EN), monthCount, dateCount, patternString);
    }

    /**
     * 
     * @MethodName: getBeforeMonth
     * @Description: 当前日期转换为指定月数后 的日期
     * @author yuanzhenhui
     * @param monthCount
     * @param patternString
     * @return String
     * @date 2023-10-11 04:52:23
     */
    public static String getBeforeMonth(int monthCount, String patternString) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, monthCount);
        SimpleDateFormat formatter = new SimpleDateFormat(patternString);
        return formatter.format(calendar.getTime());
    }

    /**
     * 
     * @MethodName: getDateToString
     * @Description: 日期格式化(String转换为Date)
     * @author yuanzhenhui
     * @param dateStr
     * @param patten
     * @return Date
     * @date 2023-10-11 04:52:10
     */
    public static Date getDateToString(String dateStr, String patten) {
        Date date = null;
        try {
            if (StringUtil.isNotEmpty(dateStr)) {
                SimpleDateFormat formatter = new SimpleDateFormat(patten, Locale.ENGLISH);
                date = formatter.parse(dateStr);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.getDateToString] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: getDateString
     * @Description: 日期格式化(String转换为String)
     * @author yuanzhenhui
     * @param date
     * @param patternString
     * @return String
     * @date 2023-10-11 04:52:02
     */
    public static String getDateString(String date, String patternString) {
        if (date == null) {
            return "";
        }

        if (date.length() < 10) {
            return "";
        }

        SimpleDateFormat formatter = new SimpleDateFormat(patternString, Locale.ENGLISH);
        int len = patternString.length();
        if (len > date.length()) {
            patternString = patternString.substring(0, date.length());
        }
        return formatter.format(getDateToString(date, patternString));
    }

    /**
     * 
     * @MethodName: getDateString
     * @Description: 日期格式化(Date转换为String)
     * @author yuanzhenhui
     * @param _date
     * @param patternString
     * @return String
     * @date 2023-10-11 04:51:53
     */
    public static String getDateString(Date _date, String patternString) {
        String dateString = "";
        if (_date != null) {
            SimpleDateFormat formatter = new SimpleDateFormat(patternString);
            dateString = formatter.format(_date);
        }
        return dateString;
    }

    /**
     * 
     * @MethodName: dateToDate
     * @Description: 日期格式转换 DATE to DATE
     * @author yuanzhenhui
     * @param _date
     * @param patten
     * @return Date
     * @date 2023-10-11 04:51:44
     */
    public static Date dateToDate(Date _date, String patten) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat(patten);
        try {
            if (_date != null) {
                String dateStr = formatter.format(_date);
                date = formatter.parse(dateStr);
            }
        } catch (ParseException e) {
            log.error("func[DateUtil.dateToDate] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: getDateOfString
     * @Description: 获得格式化日期之后的 String数据
     * @author yuanzhenhui
     * @param dateLong
     * @param patten
     * @return String
     * @date 2023-10-11 04:51:35
     */
    public static String getDateOfString(Long dateLong, String patten) {
        if (dateLong != null) {
            return (new SimpleDateFormat(patten).format(new Date(dateLong.longValue()))).toString();
        }
        return "";
    }

    /**
     * 
     * @MethodName: getSqlDate
     * @Description: 文本时间转换为时间对象
     * @author yuanzhenhui
     * @param baseDate
     * @return java.sql.Date
     * @date 2023-10-11 04:51:27
     */
    public static java.sql.Date getSqlDate(String baseDate) {
        if (baseDate == null || baseDate.length() == 0) {
            return null;
        }
        Date date = getDateToString(baseDate, yyyy_MM_dd_EN);
        return new java.sql.Date(date.getTime());
    }

    /**
     * 
     * @MethodName: utilDateToSQLDate
     * @Description: java.util.Date对象转换为java.sql.Date对象
     * @author yuanzhenhui
     * @param date
     * @return java.sql.Date
     * @date 2023-10-11 04:51:19
     */
    public static java.sql.Date utilDateToSQLDate(Date date) {
        return new java.sql.Date(date.getTime());
    }

    /**
     * 
     * @MethodName: getDateString
     * @Description: 获取到指定样式的年月日(年月日参数为int型)
     * @author yuanzhenhui
     * @param year
     * @param month
     * @param date
     * @param patternString
     * @return String
     * @date 2023-10-11 04:51:10
     */
    public static String getDateString(int year, int month, int date, String patternString) {
        String dateString = "";
        SimpleDateFormat formatter = new SimpleDateFormat(patternString);
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, date);
        Date showDate = calendar.getTime();
        dateString = formatter.format(showDate);
        return dateString;
    }

    /**
     * 
     * @MethodName: getDateString
     * @Description: 获取到指定样式的年月日(年月日参数为String型)
     * @author yuanzhenhui
     * @param year
     * @param month
     * @param date
     * @param patternString
     * @return String
     * @date 2023-10-11 04:51:00
     */
    public static String getDateString(String year, String month, String date, String patternString) {
        String dateString = "";
        try {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(date);
            dateString = getDateString(y, m, d, patternString);
        } catch (Exception e) {
            log.error("func[DateUtil.getDateString] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return dateString;
    }

    /**
     * 
     * @MethodName: getDateStr
     * @Description: 获取当前日期
     * @author yuanzhenhui
     * @param patternString
     * @return String
     * @date 2023-10-11 04:50:50
     */
    public static String getDateStr(String patternString) {
        SimpleDateFormat formatter = new SimpleDateFormat(patternString);
        return formatter.format(new Date(System.currentTimeMillis()));
    }

    /**
     * 
     * @MethodName: getCalendar
     * @Description: 把Date转换为Calendar对象
     * @author yuanzhenhui
     * @param d
     * @return Calendar
     * @date 2023-10-11 04:50:40
     */
    public static Calendar getCalendar(Date d) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(d);
        return cal;
    }

    /**
     * 
     * @MethodName: parseDateTime
     * @Description: 将时间对象转换成指定的格式有小时
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-10-11 04:50:30
     */
    public static String parseDateTime(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_EN);
        return bartDateFormat.format(date);
    }

    /**
     * 
     * @MethodName: parseDate
     * @Description: 将时间对象转换成指定的格式无小时
     * @author yuanzhenhui
     * @param date
     * @return String
     * @date 2023-10-11 04:50:17
     */
    public static String parseDate(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat bartDateFormat = new SimpleDateFormat(yyyy_MM_dd_EN);
        return bartDateFormat.format(date);
    }

    /**
     * 
     * @MethodName: firstDate
     * @Description: 获取当前月第一天
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:50:09
     */
    public static String firstDate() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = ca.getTime();
        return getDateString(firstDate, yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: lastDate
     * @Description: 获取当前月第一天
     * @author yuanzhenhui
     * @return String
     * @date 2023-10-11 04:50:00
     */
    public static String lastDate() {
        Calendar ca = Calendar.getInstance();
        ca.setTime(new Date());
        ca.set(Calendar.DAY_OF_MONTH, 1);
        ca.add(Calendar.MONTH, 1);
        ca.add(Calendar.DAY_OF_MONTH, -1);
        Date lastDate = ca.getTime();
        return getDateString(lastDate, yyyy_MM_dd_EN);
    }

    /**
     * 
     * @MethodName: getUpMouth
     * @Description: 获取上一个月的日期
     * @author yuanzhenhui
     * @param date
     * @return Date
     * @date 2023-10-11 04:49:52
     */
    public static Date getUpMouth(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, -1);
        return ca.getTime();
    }

    /**
     * 
     * @MethodName: getUpYear
     * @Description: 获取过去一年这个时间
     * @author yuanzhenhui
     * @param date
     * @return Date
     * @date 2023-10-11 04:39:37
     */
    public static Date getUpYear(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.YEAR, -1);
        return ca.getTime();
    }

    /**
     * 
     * @MethodName: getLastDay
     * @Description: 获取每月最后一天
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:39:29
     */
    public static int getLastDay(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(string2Date(date, yyyy_MM_dd_EN));
        return ca.getActualMaximum(Calendar.DATE);
    }

    /**
     * 
     * @MethodName: getWeek
     * @Description: 获取日期事第几周
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:39:22
     */
    public static int getWeek(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 
     * @MethodName: getUpMouth
     * @Description: 获取上一个月的日期
     * @author yuanzhenhui
     * @param date
     * @return Date
     * @date 2023-10-11 04:39:13
     */
    public static Date getUpMouth(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtil.getDateToString(date, yyyy_MM_dd_EN));
        ca.add(Calendar.MONTH, -1);
        return ca.getTime();
    }

    /**
     * 
     * @MethodName: getYear
     * @Description: 获取日期的年
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:39:04
     */
    public static int getYear(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtil.getDateToString(date, yyyy_MM_dd_EN));
        return ca.get(Calendar.YEAR);
    }

    /**
     * 
     * @MethodName: getMonth
     * @Description: 获取日期的月
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:38:57
     */
    public static int getMonth(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtil.getDateToString(date, yyyy_MM_dd_EN));
        return ca.get(Calendar.MONTH) + 1;
    }

    /**
     * 
     * @MethodName: getDay
     * @Description: 获取日期的日
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:38:50
     */
    public static int getDay(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtil.getDateToString(date, yyyy_MM_dd_EN));
        return ca.get(Calendar.DATE);
    }

    /**
     * 
     * @MethodName: getWeek
     * @Description: 获取日期的第几周
     * @author yuanzhenhui
     * @param date
     * @return int
     * @date 2023-10-11 04:38:42
     */
    public static int getWeek(String date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(DateUtil.getDateToString(date, yyyy_MM_dd_EN));
        return ca.get(Calendar.DAY_OF_WEEK);
    }

    /**
     * 
     * @MethodName: checkMax
     * @Description: 检测d1 是否大于等于d2
     * @author yuanzhenhui
     * @param d1
     * @param d2
     * @return boolean
     * @date 2023-10-11 04:38:33
     */
    public static boolean checkMax(Date d1, Date d2) {
        boolean flag = false;
        if (null != d1) {
            if (null != d2) {
                String d1s = getDateString(d1, yyyyMMdd_EN);
                String d12s = getDateString(d2, yyyyMMdd_EN);
                if (Double.valueOf(d1s) >= Double.valueOf(d12s)) {
                    flag = true;
                }
            } else {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isWeekend
     * @Description: 判断是否周末
     * @author yuanzhenhui
     * @param date
     * @return boolean
     * @date 2023-10-11 04:38:24
     */
    public static boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return (Calendar.SUNDAY == calendar.get(Calendar.DAY_OF_WEEK)
            || Calendar.SATURDAY == calendar.get(Calendar.DAY_OF_WEEK)) ? true : false;
    }

    /**
     * 
     * @MethodName: addMinutes
     * @Description: 给定时间往后延给定分钟
     * @author yuanzhenhui
     * @param date
     * @param minute
     * @return Date
     * @date 2023-10-11 04:38:16
     */
    public static Date addMinutes(Date date, int minute) {
        if (null == date) {
            date = new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, minute);
        return calendar.getTime();
    }

    /**
     * 
     * @MethodName: pastDay
     * @Description: 获取过去的天
     * @author yuanzhenhui
     * @param date
     * @param day
     * @return Date
     * @date 2023-10-11 04:38:05
     */
    public static Date pastDay(Date date, int day) {
        if (null == date) {
            return new Date();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, -day);
        return calendar.getTime();
    }

    /**
     * 
     * @MethodName: interceptionDate
     * @Description: 截取时间字符串，舍弃毫秒 2012-11-20 16:58:03.0 --> 2012-11-20 16:58:03.0
     * @author yuanzhenhui
     * @param dateStr
     * @return String
     * @date 2023-10-11 04:37:57
     */
    public static String interceptionDate(String dateStr) {
        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }
        if (dateStr.length() > 20) {
            dateStr = dateStr.substring(0, dateStr.lastIndexOf("."));
        }

        return dateStr;
    }

    /**
     * 
     * @MethodName: interceptionDate
     * @Description: 截取时间字符串，指定长度.
     * @author yuanzhenhui
     * @param dateStr
     * @param length
     * @return String
     * @date 2023-10-11 04:37:47
     */
    public static String interceptionDate(String dateStr, int length) {
        if (dateStr == null || "".equals(dateStr)) {
            return null;
        }
        if (dateStr.length() > length) {
            dateStr = dateStr.substring(0, length);
        }
        return dateStr;
    }

    /**
     * 
     * @MethodName: getTimestamp
     * @Description: 获取时间戳
     * @author yuanzhenhui
     * @param str
     * @return Timestamp
     * @date 2023-10-11 04:37:38
     */
    public static Timestamp getTimestamp(String str) {
        Timestamp ret = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd_HH_mm_ss_EN, Locale.ENGLISH);
            Date date = dateFormat.parse(str);
            long datelong = date.getTime();
            ret = new Timestamp(datelong);
        } catch (Exception e) {
            log.error("func[DateUtil.getTimestamp] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return ret;
    }

    /**
     * 
     * @MethodName: getTimestamp
     * @Description: 获取时间戳
     * @author yuanzhenhui
     * @param str
     * @param _dtFormat
     * @return Timestamp
     * @date 2023-10-11 04:37:31
     */
    public static Timestamp getTimestamp(String str, String _dtFormat) {
        Timestamp ret = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(_dtFormat);
            Date date = dateFormat.parse(str);
            long datelong = date.getTime();
            ret = new Timestamp(datelong);
        } catch (Exception e) {
            log.error("func[DateUtil.getTimestamp] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return ret;
    }

    /**
     * 
     * @MethodName: getMaxDate
     * @Description: 获取最大日期
     * @author yuanzhenhui
     * @return Date
     * @date 2023-10-11 04:37:23
     */
    public static Date getMaxDate() {
        Date date = null;
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(yyyy_MM_dd_EN, Locale.ENGLISH);
            date = dateFormat.parse("2099-12-31");
        } catch (ParseException e) {
            log.error("func[DateUtil.getMaxDate] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return date;
    }

    /**
     * 
     * @MethodName: convertToXMLGregorianCalendar
     * @Description: 转换为XML公历
     * @author yuanzhenhui
     * @param date
     * @return XMLGregorianCalendar
     * @date 2023-10-11 04:37:11
     */
    public XMLGregorianCalendar convertToXMLGregorianCalendar(Date date) {
        XMLGregorianCalendar gc = null;
        try {
            GregorianCalendar cal = new GregorianCalendar();
            cal.setTime(date);
            gc = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
        } catch (Exception e) {
            log.error("func[DateUtil.convertToXMLGregorianCalendar] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return gc;
    }

    /**
     * 
     * @MethodName: convertToDate
     * @Description: 公历转换成时间
     * @author yuanzhenhui
     * @param cal
     * @return Date
     * @date 2023-10-11 04:37:00
     */
    public Date convertToDate(XMLGregorianCalendar cal) {
        GregorianCalendar ca = cal.toGregorianCalendar();
        return ca.getTime();
    }

    /**
     * 
     * @MethodName: get30MinuteLast
     * @Description: 获取后30分钟
     * @author yuanzhenhui
     * @param date
     * @param minute
     * @return Date
     * @date 2023-10-11 04:36:48
     */
    public static Date get30MinuteLast(Date date, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, -30);
        return calendar.getTime();
    }

    /**
     * 
     * @MethodName: dateIsNull
     * @Description: 校验开始时间与结束时间是否同时存在
     * @author yuanzhenhui
     * @param startDate
     * @param endDate
     * @return boolean
     * @date 2023-10-11 04:36:39
     */
    public static boolean dateIsNull(Date startDate, Date endDate) {
        boolean flag;
        if ((null == startDate && null != endDate) || (null == endDate && null != startDate)) {
            flag = false;
        } else {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @MethodName: startEndDateLessThanNowDay
     * @Description: 校验开始时间结束时间是否符合规范
     * @author yuanzhenhui
     * @param nowDate
     * @param startDate
     * @param endDate
     * @return boolean
     * @date 2023-10-11 04:36:28
     */
    public static boolean startEndDateLessThanNowDay(Date nowDate, Date startDate, Date endDate) {
        boolean flag = true;
        if (dateIsNull(startDate, endDate) && null != nowDate) {
            long longStartDate = startDate.getTime();
            long longEndDate = endDate.getTime();
            long longNowDate = nowDate.getTime();
            if (longNowDate > longStartDate || longStartDate > longEndDate) {
                flag = false;
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isDateInterval
     * @Description: 判断时间是否在另一个时间区间
     * @author yuanzhenhui
     * @param nowDate
     * @param startDate
     * @param endDate
     * @return boolean
     * @date 2023-10-11 04:36:15
     */
    public static boolean isDateInterval(Date nowDate, Date startDate, Date endDate) {
        boolean flag = false;
        if (dateIsNull(startDate, endDate)) {
            Calendar date = Calendar.getInstance();
            date.setTime(nowDate);

            Calendar start = Calendar.getInstance();
            start.setTime(startDate);

            Calendar end = Calendar.getInstance();
            end.setTime(endDate);
            if (date.after(start) && date.before(end)) {
                flag = true;
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isDateIntervalIndulgence
     * @Description: 校验2个时间区间是否包涵
     * @author yuanzhenhui
     * @param startDate1
     * @param endDate1
     * @param startDate2
     * @param endDate2
     * @return boolean
     * @date 2023-10-11 04:36:03
     */
    public static boolean isDateIntervalIndulgence(Date startDate1, Date endDate1, Date startDate2, Date endDate2) {
        boolean flag = false;
        if (dateIsNull(startDate1, endDate1) && dateIsNull(startDate2, endDate2)) {
            long startLong1 = startDate1.getTime();
            long endLong1 = endDate1.getTime();
            long startLong2 = startDate2.getTime();
            long endLong2 = endDate2.getTime();
            if (startLong1 <= endLong2 && endLong1 >= startLong2) {
                flag = true;
            }
        }
        return flag;
    }

}
