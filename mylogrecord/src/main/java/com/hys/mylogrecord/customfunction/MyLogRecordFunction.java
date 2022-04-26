package com.hys.mylogrecord.customfunction;

/**
 * 日志记录自定义函数
 * 注：实现类必须定义成Spring Bean的形式
 *
 * @author Robert Hou
 * @since 2022年04月23日 11:27
 **/
public interface MyLogRecordFunction {

    /**
     * 是否在目标方法前执行
     */
    default boolean executeBefore() {
        return false;
    }

    /**
     * 方法名
     * 注：需要保证全局唯一，建议加上项目名前缀
     */
    String functionName();

    /**
     * 自定义函数
     */
    String apply(Object value);
}
