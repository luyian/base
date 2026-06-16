package com.base.system;

import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@SpringBootApplication
@EnableScheduling
@ComponentScan(basePackages = {"com.base"})
public class BaseSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(BaseSystemApplication.class, args);
        log.info("====================================");
        log.info("========== 系统启动成功 ==========");
        log.info("====================================");
    }

}
