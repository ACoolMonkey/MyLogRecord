package com.hys.mylogrecord.aop.annotation;

import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;

import java.lang.annotation.*;

/**
 * 支持快照缓存，需要实现{@link MyLogRecordSnapshotFunction}接口
 *
 * @author Robert Hou
 * @since 2022年04月27日 17:56
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CustomSnapshotFunction {
}
