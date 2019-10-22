package com.msf.dbbase;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.msf.dbbase.entity.User;
import com.msf.dbbase.mapper.UserMapper;

import cn.hutool.core.collection.CollectionUtil;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestController {
	@Autowired
	UserMapper userMapper; 
	@Test
	public void test(){
		System.err.println("object:");
		System.err.println(userMapper.selectAll().size());
		List<User> users = userMapper.selectAll();
		if(CollectionUtil.isNotEmpty(users)) {
			userMapper.selectAll().stream().forEach(p->System.err.println(JSON.toJSON(p)));
		}
	}
}
