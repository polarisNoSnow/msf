package com.msf.gateway;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 用户视图
 * @author polaris
 * @date 2019年11月11日
 */
@ApiModel(value="用户视图",description = "用于前端展示")
@Data
public class UserVO {
	@ApiModelProperty(value = "姓名")
	private String name;
	@ApiModelProperty(value = "年龄")	
	private Integer age;
}
