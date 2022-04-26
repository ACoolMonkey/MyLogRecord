package com.hys.mylogrecord.persistence.service.impl;

import com.hys.mylogrecord.log.OperationLogDTO;
import com.hys.mylogrecord.persistence.service.LogRecordService;
import org.springframework.stereotype.Service;

/**
 * 日志记录持久化默认实现
 *
 * @author Robert Hou
 * @since 2022年04月24日 00:38
 **/
@Service
public class DefaultLogRecordServiceImpl implements LogRecordService {

    @Override
    public void record(OperationLogDTO log) {
        //这里是自定义的日志持久化逻辑
    }
}
