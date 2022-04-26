package com.hys.mylogrecord.log;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

@Data
@NoArgsConstructor
public class OperationLogDTO implements Serializable {

    private static final long serialVersionUID = 6992131465961814287L;
    /**
     * 主键id
     */
    private Long id;
    /**
     * 日志类型
     *
     * @see OperationLogTypeEnum
     */
    private Integer type;
    /**
     * 关联主键id
     */
    private Long relationId;
    /**
     * 操作人id
     */
    private Long operatorId;
    /**
     * 操作时间
     */
    private Date operateTime;
    /**
     * 操作说明
     */
    private String description;
}
