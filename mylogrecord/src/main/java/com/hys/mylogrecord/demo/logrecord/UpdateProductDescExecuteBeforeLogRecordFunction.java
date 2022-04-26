package com.hys.mylogrecord.demo.logrecord;

import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import org.springframework.stereotype.Component;

/**
 * 修改商品操作说明（目标方法前执行）
 *
 * @author Robert Hou
 * @since 2022年04月27日 00:07
 **/
@Component
public class UpdateProductDescExecuteBeforeLogRecordFunction implements MyLogRecordFunction {

    @Override
    public boolean executeBefore() {
        return true;
    }

    @Override
    public String functionName() {
        return "product_updateProduct_desc_executeBefore";
    }

    @Override
    public String apply(Object value) {
        return "业务方法执行前的" + value;
    }
}
