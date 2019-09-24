package com.msf.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 *
 * @author 北辰不落雪
 * @time 2019年3月4日
 * 
 */
//@EnableDubbo(multipleConfig = true) //多注册中心集群，低版本使用
@SpringBootApplication
public class Application {
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}
}
