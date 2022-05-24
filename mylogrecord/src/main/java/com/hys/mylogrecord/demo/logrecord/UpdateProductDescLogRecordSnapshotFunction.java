package com.hys.mylogrecord.demo.logrecord;

import com.hys.mylogrecord.customfunction.MyLogRecordFunction;
import com.hys.mylogrecord.demo.dto.ProductContentDTO;
import com.hys.mylogrecord.util.LogRecordContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

/**
 * 修改商品操作说明
 *
 * @author Robert Hou
 * @since 2022年04月27日 01:44
 **/
@Component
public class UpdateProductDescLogRecordSnapshotFunction implements MyLogRecordFunction {

    @Override
    public String functionName() {
        return "product_updateProduct_desc_snapshot";
    }

    @Override
    public String apply(Object value) {
        List<Object> snapshots = LogRecordContext.getSnapshotCache();
        if (CollectionUtils.isEmpty(snapshots)) {
            return null;
        }
        Object snapshot = snapshots.get(0);
        ProductContentDTO oldProductContent;
        if (snapshot instanceof ProductContentDTO) {
            oldProductContent = (ProductContentDTO) snapshot;
        } else {
            return null;
        }
        ProductContentDTO productContent;
        if (value instanceof ProductContentDTO) {
            productContent = (ProductContentDTO) value;
        } else {
            return null;
        }

        return getDiff(productContent, oldProductContent);
    }

    private String getDiff(ProductContentDTO productContent, ProductContentDTO oldProductContent) {
        if (Objects.equals(productContent, oldProductContent)) {
            return "无变化";
        }
        String content = null;
        if (productContent != null) {
            content = productContent.getContent();
        }
        String oldContent = null;
        if (oldProductContent != null) {
            oldContent = oldProductContent.getContent();
        }
        if (StringUtils.isNotBlank(oldContent) && StringUtils.isBlank(content)) {
            return "\"" + oldContent + "\"删除了";
        } else if (StringUtils.isNotBlank(oldContent) && StringUtils.isNotBlank(content)) {
            return "\"" + oldContent + "\"修改为\"" + content + "\"";
        }
        return "";
    }
}
