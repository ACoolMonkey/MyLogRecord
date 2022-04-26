package com.hys.mylogrecord;

import com.hys.mylogrecord.demo.MyLogRecordTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MylogrecordApplicationTests {

    @Autowired
    private MyLogRecordTest myLogRecordTest;

    @Test
    void contextLoads() {
        myLogRecordTest.simpleTest();
    }
}
