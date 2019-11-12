package com.msf.gateway.controller;

import org.apache.dubbo.config.annotation.Reference;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.msf.gateway.UserVO;
import com.msf.user.api.UserService;
import com.msf.user.model.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

/**
 * 用户处理
 * @author polaris
 * @date 2019年10月16日
 */
@Api("用户操作处理接口")
@RestController
public class UserController {
	
	@Reference(interfaceClass=UserService.class)
	private UserService userService;
	
	@ApiOperation(value = "say hello", notes = "返回${name}+hello")
	@ApiImplicitParam(name = "name", value = "姓名", paramType = "query", required = true, dataType = "String")
	@RequestMapping(value = "/sayHello", method=RequestMethod.GET)
	public String sayHello(@RequestParam String name) {
		return userService.sayHello(name);
	}
	
	@ApiOperation(value = "用户信息", notes = "根据用户名查询用户信息")
	@PostMapping("/user")
	public UserVO user(@RequestParam String name) {
		User user = new User();
		user.setName(name);
		user = userService.findUserByName(user);
		UserVO target = new UserVO();
		BeanUtils.copyProperties(user, target);
		return target;
	}
}
