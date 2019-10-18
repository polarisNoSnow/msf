package com.msf.dbbase;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 *
 * @author 北辰不落雪
 * @time 2019年3月4日
 * 
 */
@SpringBootApplication
@EnableDubbo(multipleConfig = true)
public class ApplicationDbBaseService {
	public static void main(String[] args) {
		SpringApplication.run(ApplicationDbBaseService.class, args);
	}
}
