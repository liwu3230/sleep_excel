package org.example.backend.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableAsync
@ComponentScan(basePackages = {"org.example"})
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class WebApplication {

    private static final Logger logger = LoggerFactory.getLogger(WebApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(org.example.backend.web.WebApplication.class, args);
        logger.info(">>> " + org.example.backend.web.WebApplication.class.getSimpleName() + " is success!");
    }

}

