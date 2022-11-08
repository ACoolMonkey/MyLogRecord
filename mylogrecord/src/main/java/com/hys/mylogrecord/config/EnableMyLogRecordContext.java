package com.hys.mylogrecord.config;

/**
 * 是否启动日志记录上下文
 *
 * @author Robert Hou
 * @since 2022年11月08日 10:46
 **/
public class EnableMyLogRecordContext {

    private static final ThreadLocal<String[]> ENABLE_MY_LOG_RECORD_SCAN_BASE_PACKAGES_CACHE = new ThreadLocal<>();

    private EnableMyLogRecordContext() {
    }

    public static void setEnableMyLogRecordScanBasePackagesCache(String[] scanBasePackages) {
        ENABLE_MY_LOG_RECORD_SCAN_BASE_PACKAGES_CACHE.set(scanBasePackages);
    }

    public static String[] getEnableMyLogRecordScanBasePackagesCache() {
        return ENABLE_MY_LOG_RECORD_SCAN_BASE_PACKAGES_CACHE.get();
    }

    public static void remove() {
        ENABLE_MY_LOG_RECORD_SCAN_BASE_PACKAGES_CACHE.remove();
    }
}
