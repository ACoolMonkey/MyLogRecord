package com.hys.mylogrecord.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 日志工具类
 *
 * @author Robert Hou
 * @since 2022年05月13日 01:21
 **/
public class LogUtils {

    private static final Pattern METHOD_LOG_DESENSITIZATION_PATTERN = Pattern.compile("^([\\w]+)? ([\\w\\.]+)? ([\\w\\.$]+)?\\(.*\\)$", Pattern.CASE_INSENSITIVE);
    private static final Pattern BEAN_LOG_DESENSITIZATION_PATTERN = Pattern.compile("^([\\w\\.$]+)?@\\w+$", Pattern.CASE_INSENSITIVE);

    private LogUtils() {
    }

    public static String methodLogDesensitization(String method) {
        if (StringUtils.isBlank(method)) {
            return method;
        }

        Matcher matcher = METHOD_LOG_DESENSITIZATION_PATTERN.matcher(method);
        if (!matcher.find()) {
            return method;
        }
        String log = matcher.group(3);
        return doLogDesensitization(log, 2);
    }

    public static String beanLogDesensitization(String bean) {
        if (StringUtils.isBlank(bean)) {
            return bean;
        }

        Matcher matcher = BEAN_LOG_DESENSITIZATION_PATTERN.matcher(bean);
        if (!matcher.find()) {
            return bean;
        }
        String log = matcher.group(1);
        return doLogDesensitization(log, 1);
    }

    private static String doLogDesensitization(String log, int last) {
        String[] splitLog = log.split("\\.");
        if (last < 0) {
            last = 0;
        } else if (last >= splitLog.length) {
            return log;
        }
        int index = splitLog.length - last;
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < splitLog.length; i++) {
            String sl = splitLog[i];
            if (i + 1 <= index) {
                stringBuilder.append(sl.charAt(0)).append(".");
            } else {
                stringBuilder.append(sl).append(".");
            }
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
