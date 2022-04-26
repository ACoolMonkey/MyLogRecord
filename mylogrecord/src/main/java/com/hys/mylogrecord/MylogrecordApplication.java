package com.hys.mylogrecord;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class MylogrecordApplication {

    public static void main(String[] args) {
        SpringApplication.run(MylogrecordApplication.class, args);
    }

}
