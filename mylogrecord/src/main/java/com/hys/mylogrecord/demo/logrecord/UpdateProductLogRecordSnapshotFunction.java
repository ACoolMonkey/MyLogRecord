package com.hys.mylogrecord.demo.logrecord;

import com.hys.mylogrecord.customfunction.MyLogRecordSnapshotFunction;
import com.hys.mylogrecord.demo.dto.ProductContentDTO;
import org.springframework.stereotype.Component;

/**
 * 修改商品保存快照
 *
 * @author Robert Hou
 * @since 2022年04月27日 01:38
 **/
@Component
public class UpdateProductLogRecordSnapshotFunction implements MyLogRecordSnapshotFunction {

    @Override
    public String functionName() {
        return "product_updateProduct_snapshot";
    }

    @Override
    public Object snapshotApply(Object value) {
        Long spuId;
        if (value instanceof Long) {
            spuId = (Long) value;
        } else {
            return null;
        }

        ProductContentDTO productContentDTO = new ProductContentDTO();
        productContentDTO.setContent("执行业务方法前的实体");
        return productContentDTO;
    }
}
