package com.hys.mylogrecord.aop.annotation;

import com.hys.mylogrecord.customfunction.MyLogRecordFunction;

import java.lang.annotation.*;

/**
 * 支持自定义函数，需要实现{@link MyLogRecordFunction}接口
 *
 * @author Robert Hou
 * @since 2022年04月24日 14:51
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomFunction {
}
