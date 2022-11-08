package com.hys.mylogrecord;

import com.hys.mylogrecord.config.EnableMyLogRecord;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableMyLogRecord(scanBasePackages = "com.hys")
public class MylogrecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(MylogrecordApplication.class, args);
    }

}
