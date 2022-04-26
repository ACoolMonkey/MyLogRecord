package com.hys.mylogrecord.log;

import lombok.Getter;

@Getter
public enum OperationLogTypeEnum {

    /**
     * 空操作
     */
    NO_OP(0, "");

    private final Integer type;
    private final String name;

    OperationLogTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
