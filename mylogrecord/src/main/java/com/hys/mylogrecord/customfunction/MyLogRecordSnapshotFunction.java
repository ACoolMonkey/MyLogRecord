package com.hys.mylogrecord.customfunction;

import com.hys.mylogrecord.util.LogRecordContext;

/**
 * 日志记录保存快照
 * 注：实现类必须定义成Spring Bean的形式
 *
 * @author Robert Hou
 * @since 2022年04月25日 17:45
 **/
public interface MyLogRecordSnapshotFunction {

    /**
     * 方法名
     * 注：需要保证全局唯一，建议加上项目名前缀
     */
    String functionName();

    /**
     * 自定义函数
     * 注：本方法的返回值会被存进缓存中，通过{@link LogRecordContext#getSnapshotCache()}方法拿到缓存值
     */
    Object snapshotApply(Object value);
}
