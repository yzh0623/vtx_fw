package io.kida.yuen.utils.selfdev.base;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

import lombok.extern.slf4j.Slf4j;

/**
 * 
 * @Copyright (c) by kida yuan.
 * @All right reserved.
 * @Project: vtx_util
 * @File: StringUtil.java
 * @ClassName: StringUtil
 * @Description:字符串工具类
 *
 * @Author: yuanzhenhui
 * @Date: 2023/10/11
 */
@Slf4j
public class StringUtil {

    private static final String C2U_PATTERN = "[A-Z]([a-z\\d]+)?";
    private static final String U2C_PATTERN = "([A-Za-z\\d]+)(_)?";

    private static final SecureRandom SECURE_RANDOM = new SecureRandom();

    private char[] chartable = {'啊', '芭', '擦', '搭', '蛾', '发', '噶', '哈', '哈', '击', '喀', '垃', '妈', '拿', '哦', '啪', '期',
        '然', '撒', '塌', '塌', '塌', '挖', '昔', '压', '匝', '座'};

    private char[] alphatable = {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q',
        'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};

    public static final String US_ASCII = "US-ASCII";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16BE = "UTF-16BE";
    public static final String UTF_16LE = "UTF-16LE";
    public static final String UTF_16 = "UTF-16";
    public static final String GBK = "GBK";
    public static final String GB2312 = "GB2312";
    public static final String EMPTY = "";
    public static final int INDEX_NOT_FOUND = -1;

    /**
     * 
     * @MethodName: getEncoding
     * @Description: 判断字符串的编码
     * @author yuanzhenhui
     * @param str
     * @return String
     * @date 2023-10-11 04:54:08
     */
    public static String getEncoding(String str) {
        String[] encodeStr = {UTF_8, ISO_8859_1, GBK, GB2312, US_ASCII, UTF_16BE, UTF_16LE, UTF_16};
        String encode = "";
        for (String string : encodeStr) {
            encode = checkEncoding(str, string);
            if (isNotBlank(encode)) {
                return encode;
            }
        }
        return "";
    }

    /**
     * 
     * @MethodName: checkEncoding
     * @Description: 检查字符编码
     * @author yuanzhenhui
     * @param str
     * @param encode
     * @return String
     * @date 2023-10-11 04:12:19
     */
    public static String checkEncoding(String str, String encode) {
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                return encode;
            }
        } catch (Exception e) {
            log.error("func[StringUtil.checkEncoding] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                e.getMessage(), e.getStackTrace());
        }
        return "";
    }

    /**
     * 字母Z使用了两个标签，这里有２７个值
     * 
     * i, u, v都不做声母, 跟随前面的字母
     */

    /**
     * 切割字符长度
     * 
     * @param str
     *            源字符串
     * @param len
     *            需要的长度
     * @param gb
     *            中文字占2位
     * @return List<String>
     */
    public static List<String> subGbstring(String str, int len, boolean gb) {
        List<String> list = new ArrayList<>();
        if (gb) {
            int nowlen = 0;
            int start = 0;
            StringUtil obj = new StringUtil();
            for (int i = 0; i < str.length(); i++) {
                int strgb = obj.gbValue(str.charAt(i));
                if (strgb < obj.table[0]) {
                    // 非中文简体
                    nowlen++;
                } else {
                    nowlen += 2;
                }
                if (nowlen == len) {
                    list.add(str.substring(start, i + 1));
                    start = i + 1;
                    nowlen = 0;
                } else if (nowlen > len) {
                    list.add(str.substring(start, i));
                    start = i;
                    nowlen = 0;
                } else if (i + 1 == str.length()) {
                    list.add(str.substring(start, i + 1));
                    start = i;
                    nowlen = 0;
                }
            }
        } else {
            int end = len;
            for (int start = 0; start < str.length();) {
                if (end + 1 > str.length()) {
                    end = str.length();
                }
                list.add(str.substring(start, end));
                start = end + 1;
                end += start;
            }
        }
        return list;
    }

    public int[] table = new int[27];
    {// 初始化
        for (int i = 0; i < 27; ++i) {
            table[i] = gbValue(chartable[i]);
        }
    }

    /**
     * 主函数,输入字符得到他的声母,
     * 
     * 英文字母返回对应的大写字母
     * 
     * 其他非简体汉字返回 '0'
     * 
     * @param ch
     * @return
     */
    public char charAlpha(char ch) {
        if (ch >= 'a' && ch <= 'z') {
            return (char)(ch - 'a' + 'A');
        }
        if (ch >= 'A' && ch <= 'Z') {
            return ch;
        }
        int gb = gbValue(ch);
        if (gb < table[0]) {
            return '0';
        }
        int i;
        for (i = 0; i < 26; ++i) {
            if (match(i, gb)) {
                break;
            }
        }
        if (i >= 26) {
            return '0';
        } else {
            return alphatable[i];
        }
    }

    /**
     * 
     * @MethodName: stringAlpha
     * @Description: 根据一个包含汉字的字符串返回一个汉字拼音首字母的字符串
     * @author yuanzhenhui
     * @param sourceStr
     * @return String
     * @date 2023-10-11 04:54:35
     */
    public String stringAlpha(String sourceStr) {
        StringBuilder result = new StringBuilder("");
        IntStream.range(0, sourceStr.length()).forEach(i -> result.append(charAlpha(sourceStr.charAt(i))));
        return result.toString();
    }

    private boolean match(int i, int gb) {
        if (gb < table[i]) {
            return false;
        }
        int j = i + 1;
        // 字母Z使用了两个标签
        while (j < 26 && (table[j] == table[i])) {
            ++j;
        }
        if (j == 26) {
            return gb <= table[j];
        } else {
            return gb < table[j];
        }
    }

    /**
     * 
     * @MethodName: gbValue
     * @Description: 取出汉字的编码
     * @author yuanzhenhui
     * @param ch
     * @return int
     * @date 2023-10-11 04:54:46
     */
    private int gbValue(char ch) {
        String str = "";
        str += ch;
        try {
            byte[] bytes = str.getBytes(GB2312);
            if (bytes.length < 2) {
                return 0;
            }
            return (bytes[0] << 8 & 0xff00) + (bytes[1] & 0xff);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 
     * @MethodName: getMd5
     * @Description: Md5加密
     * @author yuanzhenhui
     * @param s
     * @return String
     * @date 2023-10-11 04:54:56
     */
    public static String getMd5(String s) {
        char hexDigits[] = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
        try {
            byte[] strTemp = s.getBytes();
            MessageDigest mdTemp = MessageDigest.getInstance("MD5");
            mdTemp.update(strTemp);
            byte[] md = mdTemp.digest();
            int j = md.length;
            char str[] = new char[j * 2];
            int k = 0;
            for (int i = 0; i < j; i++) {
                byte byte0 = md[i];
                str[k++] = hexDigits[byte0 >>> 4 & 0xf];
                str[k++] = hexDigits[byte0 & 0xf];
            }
            return new String(str);
        } catch (Exception e) {
            log.error("func[StringUtil.getMd5] Exception [{} - {}] stackTrace[{}] ", e.getCause(), e.getMessage(),
                e.getStackTrace());
        }
        return null;
    }

    /**
     * 
     * @MethodName: getRand
     * @Description: 得到一个n位的随机数 第一位不能为0
     * @author yuanzhenhui
     * @param n
     * @return String
     * @date 2023-10-11 04:55:07
     */
    public static String getRand(int n) {
        Random rnd = new Random();
        StringBuilder pass = new StringBuilder("0");
        int x = rnd.nextInt(9);
        while (x == 0) {
            x = rnd.nextInt(9);
        }
        pass.append(String.valueOf(x));
        IntStream.range(1, n).forEach(i -> pass.append(String.valueOf(rnd.nextInt(9))));
        return pass.toString();
    }

    /**
     * 
     * @MethodName: getStrLen
     * @Description: java按要求长度截取字段
     * @author yuanzhenhui
     * @param str
     * @param num
     * @return String
     * @date 2023-10-11 04:55:22
     */
    public static String getStrLen(String str, int num) {
        int forNum = 0;
        int alli = 0;

        // 要循环的长度
        int strLen = 0;
        if (num <= 0) {
            return str;
        }
        if (null == str) {
            return null;
        }
        if (str.length() >= num) {
            strLen = num;

        } else {
            strLen = str.length();
        }
        for (int i = 0; i < strLen; i++) {
            if (num == Math.floor(forNum / 2f)) {
                break;
            }
            if (str.substring(i, i + 1).getBytes().length > 1) {
                // 如果是字符
                alli = alli + 1;
            }
            alli = alli + 1;
            if (alli >= num) {
                return str.substring(0, i);
            }
        }
        return str.substring(0, strLen);
    }

    /**
     * 
     * @MethodName: isLen
     * @Description: 判断字符是否超过长度
     * @author yuanzhenhui
     * @param str
     * @param num
     * @return boolean
     * @date 2023-10-11 04:55:32
     */
    public static boolean isLen(String str, int num) {
        int forNum = 0;
        int alli = 0;

        // 要循环的长度
        int strLen = 0;
        if (str.length() >= num) {
            // 超过规定字符返回true
            return true;
        } else {
            strLen = str.length();
        }
        for (int i = 0; i < strLen; i++) {
            if (num == Math.floor(forNum / 2f)) {
                break;
            }
            if (str.substring(i, i + 1).getBytes().length > 1) {
                // 如果是字符
                alli = alli + 1;
            }
            alli = alli + 1;
        }
        if (alli > num) {
            // 超过规定字符返回true
            return true;
        }
        // 不超过规定字符返回False
        return false;
    }

    /**
     * 
     * @MethodName: fillLeft
     * @Description: 填充左边字符
     * @author yuanzhenhui
     * @param source
     * @param fillChar
     * @param len
     * @return String
     * @date 2023-10-11 04:55:42
     */
    public static String fillLeft(String source, char fillChar, int len) {
        StringBuilder ret = new StringBuilder();
        if (null == source) {
            return ret.append("").toString();
        }
        if (source.length() > len) {
            ret.append(source);
        } else {
            int slen = source.length();
            while (ret.toString().length() + slen < len) {
                ret.append(fillChar);
            }
            ret.append(source);
        }
        return ret.toString();
    }

    /**
     * 
     * @MethodName: filRight
     * @Description: 填充右边字符
     * @author yuanzhenhui
     * @param source
     * @param fillChar
     * @param len
     * @return String
     * @date 2023-10-11 04:55:51
     */
    public static String filRight(String source, char fillChar, int len) {
        StringBuilder ret = new StringBuilder();
        if (null == source) {
            return ret.append("").toString();
        }
        if (source.length() > len) {
            ret.append(source);
        } else {
            ret.append(source);
            while (ret.toString().length() < len) {
                ret.append(fillChar);
            }
        }
        return ret.toString();
    }

    /**
     * 
     * @MethodName: filterStr
     * @Description: 字符串过滤将单引号换成了双引号
     * @author yuanzhenhui
     * @param str
     * @return String
     * @date 2023-10-11 04:56:02
     */
    public static String filterStr(String str) {
        if (null == str || "".equals(str)) {
            return str;
        }
        str = str.replaceAll("'", "''");
        return str;
    }

    /**
     * 
     * @MethodName: isDigit
     * @Description: 检测字符是否是数字
     * @author yuanzhenhui
     * @param c
     * @return boolean
     * @date 2023-10-11 04:56:09
     */
    public static boolean isDigit(char c) {
        String nums = "0123456789.";
        if (nums.indexOf(String.valueOf(c)) == -1) {
            return false;
        }
        return true;
    }

    /**
     * 
     * @MethodName: checkStr
     * @Description: 检查字符串是否存在特殊字符
     * @author yuanzhenhui
     * @param inputStr
     * @return String
     * @date 2023-10-11 04:56:18
     */
    public static String checkStr(String inputStr) {
        StringBuilder error = new StringBuilder("");
        if (null != inputStr && !"".equals(inputStr.trim())) {
            char c;
            for (int i = 0; i < inputStr.length(); i++) {
                c = inputStr.charAt(i);
                if (c == '"') {
                    error.append(" 特殊字符[\"]");
                }
                if (c == '\'') {
                    error.append(" 特殊字符[']");
                }
                if (c == '<') {
                    error.append(" 特殊字符[<]");
                }
                if (c == '>') {
                    error.append(" 特殊字符[>]");
                }
                if (c == '&') {
                    error.append(" 特殊字符[&]");
                }
                if (c == '%') {
                    error.append(" 特殊字符[%]");
                }
            }
        }
        return error.toString();
    }

    /**
     * 
     * @MethodName: isBlankToMsg
     * @Description: 检测字符是否为空,为空的时候返回提示
     * @author yuanzhenhui
     * @param str
     * @param msg
     * @return String
     * @date 2023-10-11 04:56:27
     */
    public static String isBlankToMsg(String str, String msg) {
        String returnstr = "";
        if (StringUtil.isBlank(str)) {
            returnstr = msg + ",";
        }
        return returnstr;
    }

    /**
     * 
     * @MethodName: getFileName
     * @Description: 获取文件名称
     * @author yuanzhenhui
     * @param filepath
     * @return String
     * @date 2023-10-11 04:56:35
     */
    public static String getFileName(String filepath) {
        if (StringUtil.isNotBlank(filepath)) {
            return filepath.substring(filepath.lastIndexOf("\\") + 1, filepath.length());
        }
        return "";
    }

    /**
     * 
     * @MethodName: changeCharset
     * @Description: 切换字符编码
     * @author yuanzhenhui
     * @param str
     * @param oldCharset
     * @param newCharset
     * @return String
     * @date 2023-10-11 04:56:43
     */
    public static String changeCharset(String str, String oldCharset, String newCharset) {
        String reVal = null;
        if (str != null) {
            // 用默认字符编码解码字符串。
            byte[] bs = null;
            try {
                if (StringUtil.isNotBlank(oldCharset)) {
                    bs = str.getBytes(oldCharset);
                } else {
                    bs = str.getBytes();
                }
                reVal = new String(bs, newCharset);
            } catch (Exception e) {
                log.error("func[StringUtil.changeCharset] Exception [{} - {}] stackTrace[{}] ", e.getCause(),
                    e.getMessage(), e.getStackTrace());
            }
        }
        return reVal;
    }

    /**
     * 
     * @MethodName: getSplit
     * @Description: 拆分规格1.00*1000*1000拆成1.00 1000 1000
     * @author yuanzhenhui
     * @param str
     * @param split
     * @return StringBuilder[]
     * @date 2023-10-11 04:56:51
     */
    public static StringBuilder[] getSplit(String str, String split) {
        String[] ggStr = null;
        // 取出数组
        if (null == str) {
            return null;
        }
        ggStr = str.split(split);
        StringBuilder[] b = new StringBuilder[ggStr.length];
        // 处理每个数组里的非数字字符
        for (int i = 0; i < ggStr.length; i++) {
            StringBuilder sBuffer = new StringBuilder();
            char[] c = ggStr[i].toCharArray();
            int data = 0;
            for (int j = 0; j < c.length; j++) {
                if (StringUtil.isDigit(c[j])) {
                    data++;
                    sBuffer.append(c[j]);
                } else if (data > 0) {
                    break;
                }
            }
            if (null == sBuffer || sBuffer.length() == 0) {
                sBuffer.append('0');
            }
            b[i] = sBuffer;
        }
        return b;
    }

    /**
     * 
     * @MethodName: makeSign
     * @Description: 将敏感字符进行替换
     * @author yuanzhenhui
     * @param value
     * @return String
     * @date 2023-10-11 04:57:00
     */
    public static String makeSign(String value) {
        String str = "";
        if (null == value) {
            return str;
        }

        // 去掉前后空格
        str = value.trim();
        return str.replaceAll(">", "&gt;").replaceAll("<", "&lt;").replaceAll("&", "&amp;").replaceAll("\"", "&quot;");
    }

    /**
     * 
     * @MethodName: intercept
     * @Description: 截取超长的信息，多余用...
     * @author yuanzhenhui
     * @param str
     * @param len
     * @return String
     * @date 2023-10-11 04:57:09
     */
    public static String intercept(String str, int len) {
        String newstr = "";
        if (null == str) {
            return newstr;
        }
        if (str.length() > len) {
            newstr = str.substring(0, len) + "...";
        } else {
            newstr = str;
        }
        return newstr;
    }

    /**
     * 
     * @MethodName: isBlankAll
     * @Description: 所有参数为空的时候返回true
     * @author yuanzhenhui
     * @param args
     * @return Boolean
     * @date 2023-10-11 04:57:17
     */
    public static Boolean isBlankAll(Object... args) {
        Boolean flag = true;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                if (!isBlank((String)args[i])) {
                    flag = false;
                }
            } else {
                if (null != args[i]) {
                    flag = false;
                }
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isBlankOne
     * @Description: 只要有一个参数为空就返回true
     * @author yuanzhenhui
     * @param args
     * @return Boolean
     * @date 2023-10-11 04:57:26
     */
    public static Boolean isBlankOne(Object... args) {
        Boolean flag = false;
        for (int i = 0; i < args.length; i++) {
            if (args[i] instanceof String) {
                if (isBlank((String)args[i])) {
                    flag = true;
                }
            } else {
                if (null == args[i]) {
                    flag = true;
                }
            }
        }
        return flag;
    }

    /**
     * 
     * @MethodName: getFirstUpper
     * @Description: 把字符串第一个字母转成大写
     * @author yuanzhenhui
     * @param str
     * @return String
     * @date 2023-10-11 04:57:34
     */
    public static String getFirstUpper(String str) {
        String newStr = "";
        if (str.length() > 0) {
            newStr = str.substring(0, 1).toUpperCase() + str.substring(1, str.length());
        }
        return newStr;
    }

    /**
     * 
     * @MethodName: indexOfAll
     * @Description: 获取一个字符在一个字符串里出现的次数
     * @author yuanzhenhui
     * @param tagetStr
     * @param str
     * @return int
     * @date 2023-10-11 04:57:43
     */
    public static int indexOfAll(String tagetStr, String str) {
        int i = 0;
        if (null != tagetStr) {
            i = tagetStr.length() - tagetStr.replace(str, "").length();
        }
        return i;
    }

    /**
     * 
     * @MethodName: getNullTo
     * @Description: 转null字符串为""
     * @author yuanzhenhui
     * @param str
     * @return String
     * @date 2023-10-11 04:57:54
     */
    public static String getNullTo(String str) {
        if (isBlank(str)) {
            str = "";
        }
        return str;
    }

    /**
     * 
     * @MethodName: equals
     * @Description: 比较两个Long是否相等
     * @author yuanzhenhui
     * @param a
     * @param b
     * @return boolean
     * @date 2023-10-11 04:58:01
     */
    public static boolean equals(Long a, Long b) {
        boolean flag = false;
        if (null == a) {
            a = 0L;
        }
        if (null == b) {
            b = 0L;
        }
        if (a.equals(b)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @MethodName: equals
     * @Description: 比较两个对象是否相等
     * @author yuanzhenhui
     * @param a
     * @param b
     * @return boolean
     * @date 2023-10-11 04:58:10
     */
    public static boolean equals(Object a, Object b) {
        boolean flag = false;
        if (null == a) {
            a = "";
        }
        a = String.valueOf(a);
        if (null == b) {
            b = "";
        }
        b = String.valueOf(b);
        if (a.equals(b)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @MethodName: equals
     * @Description: 比较两个字符串是否相等
     * @author yuanzhenhui
     * @param a
     * @param b
     * @return boolean
     * @date 2023-10-11 04:58:18
     */
    public static boolean equals(String a, String b) {
        boolean flag = false;
        if (null == a) {
            a = "";
        }
        if (null == b) {
            b = "";
        }
        if (a.equals(b)) {
            flag = true;
        }
        return flag;
    }

    /**
     * 
     * @MethodName: isBlank
     * @Description: 判断字符串是否为空
     * @author yuanzhenhui
     * @param cs
     * @return boolean
     * @date 2023-10-11 04:58:27
     */
    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (Character.isWhitespace(cs.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * 
     * @MethodName: isNotBlank
     * @Description: 判断字符串是否不为空
     * @author yuanzhenhui
     * @param cs
     * @return boolean
     * @date 2023-10-11 04:58:35
     */
    public static boolean isNotBlank(CharSequence cs) {
        return !StringUtil.isBlank(cs);
    }

    /**
     * 
     * @MethodName: getStrShowCount
     * @Description: 获取一个字符串在一个字符串中出现的次数
     * @author yuanzhenhui
     * @param str
     * @param ts
     * @return int
     * @date 2023-10-11 04:58:43
     */
    public int getStrShowCount(String str, String ts) {
        boolean flag = true;
        int i = 0;
        while (flag) {
            int index = str.indexOf(ts);
            if (index != -1) {
                i++;
                str = str.substring(index + ts.length());
            } else {
                flag = false;
            }
        }
        return i;
    }

    /**
     * 
     * @MethodName: deleteCrlfOnce
     * @Description: 删除换行一行
     * @author yuanzhenhui
     * @param input
     * @return String
     * @date 2023-10-11 04:58:53
     */
    private static String deleteCrlfOnce(String input) {
        return input.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1");
    }

    /**
     * 
     * @MethodName: deleteCrlf
     * @Description: 删除换行
     * @author yuanzhenhui
     * @param input
     * @return String
     * @date 2023-10-11 04:59:00
     */
    public static String deleteCrlf(String input) {
        if (null == input || "".equals(input)) {
            return input;
        } else {
            input = deleteCrlfOnce(input);
            return deleteCrlfOnce(input);
        }
    }

    /**
     * 
     * @MethodName: containsString
     * @Description: 判断带标点符号划分的字符串里是否包含某个字符串(精确匹配)
     * @author yuanzhenhui
     * @param strsWithSymbol
     * @param str
     * @param symbol
     * @return boolean
     * @date 2023-10-11 04:59:09
     */
    public static boolean containsString(String strsWithSymbol, String str, String symbol) {
        String[] strArr = strsWithSymbol.split(symbol);
        List<String> strList = Arrays.asList(strArr);
        return strList.contains(str) ? true : false;
    }

    /**
     * 
     * @MethodName: camel2Underline
     * @Description: 驼峰字段转下划线
     * @author yuanzhenhui
     * @param line
     * @return String
     * @date 2023-10-11 04:59:18
     */
    public static String camel2Underline(String line) {
        if (line == null || "".equals(line)) {
            return "";
        }
        line = String.valueOf(line.charAt(0)).toUpperCase().concat(line.substring(1));
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(C2U_PATTERN);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            sb.append(matcher.group().toUpperCase());
            sb.append(matcher.end() == line.length() ? "" : "_");
        }
        return sb.toString();
    }

    /**
     * 
     * @MethodName: underline2Camel
     * @Description: 下划线转驼峰
     * @author yuanzhenhui
     * @param line
     * @param smallCamel
     * @return String
     * @date 2023-10-11 04:59:26
     */
    public static String underline2Camel(String line, boolean smallCamel) {
        if (line == null || "".equals(line)) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        Pattern pattern = Pattern.compile(U2C_PATTERN);
        Matcher matcher = pattern.matcher(line);
        while (matcher.find()) {
            String word = matcher.group();
            sb.append(smallCamel && matcher.start() == 0 ? Character.toLowerCase(word.charAt(0))
                : Character.toUpperCase(word.charAt(0)));
            int index = word.lastIndexOf('_');
            if (index > 0) {
                sb.append(word.substring(1, index).toLowerCase());
            } else {
                sb.append(word.substring(1).toLowerCase());
            }
        }
        return sb.toString();
    }

    /**
     * 
     * @MethodName: isEmpty
     * @Description: 判断字符串是否为空
     * @author yuanzhenhui
     * @param cs
     * @return boolean
     * @date 2023-10-11 04:59:36
     */
    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * 
     * @MethodName: isNotEmpty
     * @Description: 判断字符串是否非空
     * @author yuanzhenhui
     * @param cs
     * @return boolean
     * @date 2023-10-11 04:59:45
     */
    public static boolean isNotEmpty(CharSequence cs) {
        return !StringUtil.isEmpty(cs);
    }

    /**
     * 
     * @MethodName: getRangeRandom
     * @Description: 获取范围内的随机数
     * @author yuanzhenhui
     * @param min
     * @param max
     * @return String
     * @date 2023-10-11 04:59:53
     */
    public static String getRangeRandom(int min, int max) {
        int rdm = SECURE_RANDOM.nextInt(max) % (max - min + 1) + min;
        return String.valueOf(rdm);
    }

    /**
     * 
     * @MethodName: gen16BinaryRandom
     * @Description: 生成16进制的随机数
     * @author yuanzhenhui
     * @param numSize
     * @return String
     * @date 2023-10-11 05:00:01
     */
    public static String gen16BinaryRandom(int numSize) {
        StringBuilder str = new StringBuilder("");
        for (int i = 0; i < numSize; i++) {
            char temp = 0;
            Long base = Math.round(Math.random() * 100000);
            if (base.intValue() % 2 == 0) {
                // 产生随机数字
                temp = (char)(Math.random() * 10 + 48);
            } else {
                // 产生a-f
                temp = (char)(Math.random() * 6 + 'a');
            }
            str.append(temp);
        }
        return str.toString();
    }

    /**
     * 
     * @MethodName: lowerFirstCase
     * @Description: 首字母小写
     * @author yuanzhenhui
     * @param str
     * @return String
     * @date 2023-10-11 05:00:08
     */
    public static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        // 首字母小写方法，大写会变成小写，如果小写首字母会消失
        chars[0] += 32;
        return String.valueOf(chars);
    }

}
