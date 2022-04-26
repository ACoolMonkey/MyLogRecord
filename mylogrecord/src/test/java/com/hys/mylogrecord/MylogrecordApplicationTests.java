package com.hys.mylogrecord;

import com.hys.mylogrecord.demo.MyLogRecordTest;
import com.hys.mylogrecord.demo.dto.ProductContentDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MylogrecordApplicationTests {

    @Autowired
    private MyLogRecordTest myLogRecordTest;

    @Test
    void contextLoads() {
        ProductContentDTO productContentDTO = new ProductContentDTO();
        productContentDTO.setContent("商品参数");
        myLogRecordTest.snapshotTest(123L, 456L, productContentDTO);
    }
}
