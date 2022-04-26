package com.hys.mylogrecord.log;

import lombok.Getter;

@Getter
public enum OperationLogTypeEnum {

    /**
     * 添加商品
     */
    INSERT_PRODUCT(1, "添加商品");

    private final Integer type;
    private final String name;

    OperationLogTypeEnum(Integer type, String name) {
        this.type = type;
        this.name = name;
    }
}
