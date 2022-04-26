package com.hys.mylogrecord.persistence.service;

import com.hys.mylogrecord.log.OperationLogDTO;

/**
 * 日志记录持久化
 *
 * @author Robert Hou
 * @since 2022年04月24日 00:36
 **/
public interface LogRecordService {

    /**
     * 保存日志
     *
     * @param log 日志实体
     */
    void record(OperationLogDTO log);
}
