package com.ljpww72729.atblink.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import androidx.collection.ArrayMap;

/**
 * String 帮助类
 *
 * Created by LinkedME06 on 24/04/2017.
 */

public class StringUtils {

    /**
     * 将字符串首字母大写，字母的ascii编码前移从而转换为大写
     *
     * @param str String
     * @return 转换后的字符串
     */
    public static String capitalze(String str) {
        char[] cs = str.toCharArray();
        cs[0] -= 32;
        return String.valueOf(cs);
    }

    /**
     * 可以替换大部分空白字符， 不限于空格\s 可以匹配空格、制表符、换页符等空白字符的其中任意一个
     *
     * @param originStr 需要被替换的空格
     * @return 去除空格后的字符串
     */
    public static String removeAllBlankCharacter(String originStr) {
        return originStr.replaceAll("\\s*", "");
    }

    /**
     * 将map对象转化为String，以separator分隔符分隔，键值对以=连接 形如：a=b&c=d
     * 主要用于请求对象的拼接
     *
     * @param convertMap 被转化的map对象
     * @param separator  分隔符
     */
    public static String convertArrayMapToString(ArrayMap<String, String> convertMap, String separator) {
        String convertString = "";
        if (convertMap != null && convertMap.size() > 0) {
            for (int i = 0; i < convertMap.size(); i++) {
                convertString += convertMap.keyAt(i) + "=" + convertMap.valueAt(i) + separator;
            }
            if (convertString.length() > 0) {
                convertString = convertString.substring(0, convertString.length() - 1);
            }
        }
        return convertString;
    }

    // 日期正则
    public static final String PATTERNDATE = "(((((\\d{4}|\\d{2})(-|/|\\.))?(\\d{1,2}\\3)\\d{1,2})|(((\\d{2}|\\d{4})年)?(\\d{1,2}月)?\\d{1,2}日)))(\\d{1,2}(:|时)\\d{1,2}((:|分)\\d{1,2}(秒?))?)?";
    // 账号正则
    public static final String PATTERNACCOUNT = "(尾号|账号|账户)(\\d{3,4})(账户|账号)?";
    // 余额正则
    public static final String PATTERNBALANCE = "(余额|剩余)([\\d,.]+)(元|美元|当地币)?";
    // 金额正则
    public static final String PATTERNMONEY = "((人民币|美元|当地币)([\\d.,]+)(美元|元)?)|(([\\d.,]+)(美元|元|当地币))";
    // 类型正则
    public static final String PATTERNTRANSACTIONTYPE = "((" + PATTERNDATE + ")|(" + PATTERNACCOUNT + "))" + "(\\D+)" + "(" + PATTERNMONEY + ")";

    /**
     * 从字符串中获取日期字符串
     *
     * @param parseString 需要处理的字符串
     * @return 获取到的日期字符串
     */
    public static String obtainDateString(String parseString) {
        String dateString = "";


        // 创建 Pattern 对象
        Pattern r = Pattern.compile(PATTERNDATE);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(parseString);
        if (m.find()) {
            dateString = m.group(0);
        } else {
            System.out.println("NO MATCH");
        }
        return dateString;
    }

    /**
     * 从字符串中获取账号字符串
     *
     * @param parseString 需要处理的字符串
     * @return 获取到的账号字符串
     */
    public static String obtainAccountString(String parseString) {
        String accountString = "";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(PATTERNACCOUNT);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(parseString);
        if (m.find()) {
            // 获取捕获组中的第二个分组，即账号
            accountString = m.group(2);
        } else {
            System.out.println("NO MATCH");
        }
        return accountString;
    }

    /**
     * 从字符串中获取余额字符串
     *
     * @param parseString 需要处理的字符串
     * @return 获取到的余额字符串
     */
    public static String obtainBalanceString(String parseString) {
        String balanceString = "";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(PATTERNBALANCE);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(parseString);
        if (m.find()) {
            balanceString = convertMoney(m.group(3)) + m.group(2);
            for (int i = 0; i < m.groupCount() + 1; i++) {
                System.out.println("group" + i + "=" + m.group(i));
            }
        } else {
            System.out.println("NO MATCH");
        }
        return balanceString;
    }


    /**
     * 从字符串中获取账号字符串
     *
     * @param parseString 需要处理的字符串
     * @return 获取到的账号字符串数组，数组第一个元素为金额，第二个元素为币种
     */
    public static String obtainMoneyString(String parseString) {
        String moneyString = "";

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(PATTERNMONEY);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(parseString);
        if (m.find()) {
            if (m.group(3) == null) {
                if (m.group(7) != null) {
                    moneyString = convertMoney(m.group(7)) + m.group(6);
                }
            } else {
                moneyString = convertMoney(m.group(2)) + m.group(3);
            }
        } else {
            System.out.println("NO MATCH");
        }
        System.out.println("金额：" + moneyString);
        return moneyString;
    }

    public static String convertMoney(String currency) {
        if (currency == null || currency.equals("人民币") || currency.equals("元") || currency.equals("")) {
            currency = "￥";
        } else if (currency.equals("美元")) {
            currency = "$";
        } else {
            currency = "#";
        }
        return currency;
    }


    /**
     * 从字符串中获取交易类型字符串
     *
     * @param parseString 需要处理的字符串
     * @return 获取到的交易类型字符串
     */
    public static String obtainTransactionTypeString(String parseString) {
        String transactionString = "";

        // 去除包含撤销、未成功、失败的短信
        if (parseString.contains("撤销") ||
                parseString.contains("未成功") ||
                parseString.contains("失败")) {
            return transactionString;
        }

        // 创建 Pattern 对象
        Pattern r = Pattern.compile(PATTERNTRANSACTIONTYPE);

        // 现在创建 matcher 对象
        Matcher m = r.matcher(parseString);
        if (m.find()) {
            transactionString = m.group(23);
            // 去除 ，人民币，美元，当地币
            transactionString = transactionString.replaceAll("(，|人民币|美元|当地币|金额)", "");
            for (int i = 0; i < m.groupCount() + 1; i++) {
                System.out.println("group" + i + "=" + m.group(i));
            }
        } else {
            System.out.println("NO MATCH");
        }
        return transactionString;
    }

    public static void splitDigitEndString(String parseString) {
        Pattern pattern = Pattern.compile(".*\\D+(?=(\\d+$))");
        Matcher matcher = pattern.matcher(parseString);
        if (matcher.find()) {
            for (int i = 0; i < matcher.groupCount() + 1; i++) {
                System.out.println(matcher.group(i) + "");
            }
        } else {
            System.out.println("NO MATCH");
        }
    }


}
