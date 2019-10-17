package com.msf.user.model;

import java.io.Serializable;

import lombok.Data;

/**
 * 用户信息
 * @author polaris
 * @date 2019年10月16日
 */
@Data
public class User implements Serializable{
	private static final long serialVersionUID = 1L;
	private String name;
	private Integer age;
}
