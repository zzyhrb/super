package com.newgrand.superviseanalysis;

import org.activiti.spring.boot.SecurityAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class, SecurityFilterAutoConfiguration.class, org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration.class})
@MapperScan({"com.newgrand.superviseanalysis.*.*.mapper"})
public class SuperviseanalysisApplication {
    private static final Logger logger = LoggerFactory.getLogger(SuperviseanalysisApplication.class);
     public static void main(String[] args) {
        SpringApplication.run(SuperviseanalysisApplication.class, args);
         logger.info("-----------------项目启动完成------------------");

     }

}
