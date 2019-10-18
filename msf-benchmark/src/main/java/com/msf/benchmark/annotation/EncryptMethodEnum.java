package com.msf.benchmark.annotation;
/**
 * 加密类型
 * @author polaris
 * @date 2019年10月18日
 */
public enum EncryptMethodEnum {
	
    //模糊查询加密方法：根据数据类型，截取出加密部分，并对加密部分进行逐个字符加密
	SINGLE_ENCRYPT_BY_TYPE, 
	//把加密数据按照数据类型，截取出需要加密的部分，进行整个加密
	ENCRYPT_BY_TYPE  
}
