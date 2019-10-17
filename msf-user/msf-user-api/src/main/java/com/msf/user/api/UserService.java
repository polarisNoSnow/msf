package com.msf.user.api;

import com.msf.user.model.User;

/**
 * 用户接口
 * @author polaris
 * @date 2019年10月16日
 */
public interface UserService {
	/**
	 * say hello
	 * 
	 * @param name 用户名
	 * @return 欢迎语
	 */
	public String sayHello(String name); 
	/**
	 * find one user by user.name
	 * 
	 * @param user 参数中只需要setname
	 * @return 根据用户名返回用户信息
	 */
	public User findUserByName(User user); 
}
