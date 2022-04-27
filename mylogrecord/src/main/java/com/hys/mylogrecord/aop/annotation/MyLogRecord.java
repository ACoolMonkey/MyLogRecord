package com.hys.mylogrecord.aop.annotation;

import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;
import com.hys.mylogrecord.log.OperationLogTypeEnum;
import com.hys.mylogrecord.util.LogRecordUtils;

import java.lang.annotation.*;

/**
 * 日志记录
 *
 * @author Robert Hou
 * @since 2022年04月21日 19:37
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyLogRecord {

    /**
     * 日志类型
     */
    OperationLogTypeEnum type();

    /**
     * 关联主键id
     */
    @SpelDynamicTemplate
    @CustomFunction
    String relationId() default "";

    /**
     * 操作人id
     */
    @SpelDynamicTemplate
    @CustomFunction
    String operatorId() default "";

    /**
     * 操作说明
     */
    @SpelDynamicTemplate
    @CustomFunction
    String description() default "";

    /**
     * 保存快照，返回值会被存进缓存中。需要实现{@link MyLogRecordSnapshotFunction}接口，通过{@link LogRecordUtils#getSnapshotCache()}方法拿到缓存值
     * 注：本方法会比预处理的自定义函数还要先执行，同时也就意味着在自定义函数预执行阶段即可拿到缓存值
     */
    @SpelDynamicTemplate
    @CustomSnapshotFunction
    String snapshot() default "";
}
