package com.hys.mylogrecord.persistence;

import com.hys.mylogrecord.aop.annotation.MyLogRecord;
import com.hys.mylogrecord.log.OperationLogDTO;
import com.hys.mylogrecord.log.OperationLogTypeEnum;
import com.hys.mylogrecord.parse.dto.DynamicTemplatesContext;
import com.hys.mylogrecord.parse.util.LogRecordParseUtils;
import com.hys.mylogrecord.persistence.service.LogRecordService;
import org.springframework.beans.factory.annotation.Autowired;
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
    private LogRecordService logRecordService;

    /**
     * 保存日志
     */
    public void record(MyLogRecord annotation) {
        OperationLogDTO operationLogDTO = buildOperationLog(annotation);
        logRecordService.record(operationLogDTO);
    }

    private OperationLogDTO buildOperationLog(MyLogRecord annotation) {
        OperationLogDTO operationLogDTO = new OperationLogDTO();
        //type
        OperationLogTypeEnum type = annotation.type();
        operationLogDTO.setType(type.getType());
        //relationId
        DynamicTemplatesContext relationIdDT = LogRecordParseUtils.getDynamicTemplates(LogRecordParseUtils.RELATION_ID);
        String relationIdResult;
        if (relationIdDT != null) {
            relationIdResult = relationIdDT.getResult();
        } else {
            relationIdResult = annotation.relationId();
        }
        operationLogDTO.setRelationId(Long.valueOf(relationIdResult));
        //operatorId
        DynamicTemplatesContext operatorIdDT = LogRecordParseUtils.getDynamicTemplates(LogRecordParseUtils.OPERATOR_ID);
        String operatorIdResult;
        if (operatorIdDT != null) {
            operatorIdResult = operatorIdDT.getResult();
        } else {
            operatorIdResult = annotation.operatorId();
        }
        operationLogDTO.setOperatorId(Long.valueOf(operatorIdResult));
        //operateTime
        operationLogDTO.setOperateTime(new Date());
        //description
        DynamicTemplatesContext descriptionDT = LogRecordParseUtils.getDynamicTemplates(LogRecordParseUtils.DESCRIPTION);
        String descriptionResult;
        if (descriptionDT != null) {
            descriptionResult = descriptionDT.getResult();
        } else {
            descriptionResult = annotation.description();
        }
        operationLogDTO.setDescription(descriptionResult);

        return operationLogDTO;
    }
}
