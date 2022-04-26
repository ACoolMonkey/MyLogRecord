package com.hys.mylogrecord.exception;

/**
 * 日志记录解析异常
 *
 * @author Robert Hou
 * @since 2022年04月23日 19:42
 **/
public class LogRecordParseException extends RuntimeException {

    private static final long serialVersionUID = 3440216136472432095L;

    public LogRecordParseException() {
        super();
    }

    public LogRecordParseException(String message) {
        super(message);
    }
}
