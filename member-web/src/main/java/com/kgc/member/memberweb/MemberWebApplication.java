package com.kgc.member.memberweb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
public class MemberWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(MemberWebApplication.class, args);
    }

}
