package com.msf.dbbase.encrypt;

import org.springframework.stereotype.Component;

/**
 * 字段加解密实现类
 * @author polaris
 * @date 2019年10月18日
 */
@Component
public class EncryptDecryptServerImpl implements IEncryptDecryptServer{

	@Override
	public String encryptByType(String type, String value) {
		// TODO 待开发
		return value;
	}

	@Override
	public String decryptByType(String type, String value) {
		// TODO 待开发
		return value;
	}

}
