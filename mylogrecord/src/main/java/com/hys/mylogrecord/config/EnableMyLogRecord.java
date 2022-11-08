package com.hys.mylogrecord.config;

import com.hys.mylogrecord.util.LogRecordConst;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 是否启动日志记录
 *
 * @author Robert Hou
 * @since 2022年11月08日 09:14
 **/
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(LogRecordConfigurationSelector.class)
public @interface EnableMyLogRecord {

    /**
     * 扫描路径
     */
    String[] scanBasePackages() default {LogRecordConst.ENABLE_MY_LOG_RECORD_DEFAULT_SCAN_BASE_PACKAGES};
}
