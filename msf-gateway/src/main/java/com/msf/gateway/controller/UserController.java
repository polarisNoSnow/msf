package com.msf.gateway.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msf.user.api.UserService;
import com.msf.user.model.User;

/**
 * 用户处理
 * @author polaris
 * @date 2019年10月16日
 */
@RestController
public class UserController {
	
	@Reference(interfaceClass=UserService.class)
	private UserService userService;
	
	@RequestMapping("/sayHello")
	public String sayHello(@RequestParam String name) {
		return userService.sayHello(name);
	}
	
	@RequestMapping("/user")
	public User user(@RequestParam String name) {
		User user = new User();
		user.setName(name);
		return userService.findUserByName(user);
	}
}
