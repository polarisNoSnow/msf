package com.msf.user.service.impl;

import org.apache.dubbo.config.annotation.Service;

import com.msf.user.api.UserService;
import com.msf.user.model.User;

/**
 * 用户服务实现
 * @author polaris
 * @date 2019年10月17日
 */
@Service
public class UserServiceImpl implements UserService {

	@Override
	public String sayHello(String name) {
		return "hello,"+name;
	}
	
	@Override
	public User findUserByName(User name) {
		name.setAge(25);
		return name;
	}

}
