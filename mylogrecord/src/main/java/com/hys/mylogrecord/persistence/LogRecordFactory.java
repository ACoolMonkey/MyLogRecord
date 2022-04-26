package com.hys.mylogrecord.persistence;

import com.hys.mylogrecord.aop.annotation.MyLogRecord;
import com.hys.mylogrecord.log.OperationLogDTO;
import com.hys.mylogrecord.log.OperationLogTypeEnum;
import com.hys.mylogrecord.parse.dto.DynamicTemplatesContext;
import com.hys.mylogrecord.parse.util.LogRecordParseUtils;
import com.hys.mylogrecord.persistence.service.LogRecordService;
import com.hys.mylogrecord.util.LogRecordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 日志记录持久化工厂
 *
 * @author Robert Hou
 * @since 2022年04月24日 10:36
 **/
@Component
public class LogRecordFactory {

    @Autowired
    @Qualifier("defaultLogRecordServiceImpl")
    private LogRecordService defaultLogRecordServiceImpl;

    /**
     * 保存日志
     */
    public void record(MyLogRecord annotation) {
        OperationLogDTO operationLogDTO = buildOperationLog(annotation);
        defaultLogRecordServiceImpl.record(operationLogDTO);
    }

    private OperationLogDTO buildOperationLog(MyLogRecord annotation) {
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        //type
        OperationLogTypeEnum type = annotation.type();
        operationLogDTO.setType(type.getType());
        //relationId
        DynamicTemplatesContext relationIdDT = LogRecordParseUtils.getDynamicTemplates(LogRecordUtils.RELATION_ID);
        if (relationIdDT != null) {
            String result = relationIdDT.getResult();
            operationLogDTO.setRelationId(Long.valueOf(result));
        }
        //operatorId
        DynamicTemplatesContext operatorIdDT = LogRecordParseUtils.getDynamicTemplates(LogRecordUtils.OPERATOR_ID);
        if (operatorIdDT != null) {
            String result = operatorIdDT.getResult();
            operationLogDTO.setOperatorId(Long.valueOf(result));
        }
        //operateTime
        operationLogDTO.setOperateTime(new Date());
        //description
        DynamicTemplatesContext descriptionDT = LogRecordParseUtils.getDynamicTemplates(LogRecordUtils.DESCRIPTION);
        if (descriptionDT != null) {
            String result = descriptionDT.getResult();
            operationLogDTO.setDescription(result);
        }

        return operationLogDTO;
    }
}
