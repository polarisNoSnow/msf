package com.msf.benchmark.dubbo.filter;

import org.apache.dubbo.common.extension.Activate;

/**
 * 消费方调用日志
 *
 * @author 北辰不落雪
 * @time 2018年8月2日
 * 
 */

@Activate
public class DubboConsumeFilter extends DubboServiceFilter
{
 
    private String logIn = "接口调用入参";
    private String logOut = "接口结果返回";
    
	public DubboConsumeFilter() {
		super();
		super.logIn = logIn;
		super.logOut = logOut;
	}

}

