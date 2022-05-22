package com.hys.mylogrecord.util;

/**
 * 日志记录常量
 *
 * @author Robert Hou
 * @since 2022年05月22日 13:21
 **/
public class LogRecordConst {

    public static final String LEFT_BRACKET_STR = "{";
    public static final String RIGHT_BRACKET_STR = "}";
    public static final char LEFT_BRACKET = LEFT_BRACKET_STR.charAt(0);
    public static final char RIGHT_BRACKET = RIGHT_BRACKET_STR.charAt(0);

    public static final String RELATION_ID = "relationId";
    public static final String OPERATOR_ID = "operatorId";
    public static final String DESCRIPTION = "description";
    public static final String SNAPSHOT = "snapshot";

    public static final String WELL_NUMBER = "#";

    private LogRecordConst() {
    }
}
