package com.base.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 系统启动类
 *
 * @author base
 * @since 2026-01-12
 */
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.base"})
public class BaseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseSystemApplication.class, args);
        System.out.println("====================================");
        System.out.println("========== 系统启动成功 ==========");
        System.out.println("====================================");
    }

}
