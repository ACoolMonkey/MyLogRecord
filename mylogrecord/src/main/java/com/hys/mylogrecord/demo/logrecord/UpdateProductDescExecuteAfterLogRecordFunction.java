package com.hys.mylogrecord.demo.logrecord;

import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import org.springframework.stereotype.Component;

/**
 * 修改商品操作说明（目标方法后执行）
 *
 * @author Robert Hou
 * @since 2022年04月26日 23:49
 **/
@Component
public class UpdateProductDescExecuteAfterLogRecordFunction implements MyLogRecordFunction {

    @Override
    public String functionName() {
        return "product_updateProduct_desc_executeAfter";
    }

    @Override
    public String apply(Object value) {
        return "业务方法执行后的" + value;
    }
}
