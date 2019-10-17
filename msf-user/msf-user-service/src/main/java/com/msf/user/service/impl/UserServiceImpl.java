package com.msf.user.service.impl;

import org.apache.dubbo.config.annotation.Service;

import com.msf.user.api.UserService;
import com.msf.user.model.User;

@Service
public class UserServiceImpl implements UserService {

	public String sayHello(String name) {
		return "hello,"+name;
	}

	public User findUserByName(User name) {
		name.setAge(25);
		return name;
	}

}
