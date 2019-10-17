package com.msf.user.api;

import com.msf.user.model.User;

/**
 * 用户接口
 * @author polaris
 * @date 2019年10月16日
 */
public interface UserService {
	/*
	 * say hello
	 */
	public String sayHello(String name); 
	/*
	 * find one user by user.name
	 */
	public User findUserByName(User name); 
}
