package com.hys.mylogrecord.demo.logrecord;

import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import org.springframework.stereotype.Component;

/**
 * 添加商品操作说明
 *
 * @author Robert Hou
 * @since 2022年04月26日 22:49
 **/
@Component
public class InsertProductDescLogRecordFunction implements MyLogRecordFunction {

    @Override
    public String functionName() {
        return "product_insertProduct_desc";
    }

    @Override
    public String apply(Object value) {
        String content;
        if (value instanceof String) {
            content = (String) value;
        } else {
            return null;
        }

        return content + "123";
    }
}
