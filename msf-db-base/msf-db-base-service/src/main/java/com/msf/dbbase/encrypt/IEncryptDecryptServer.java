package com.msf.dbbase.encrypt;

/**
 * 数据字段加解密
 * @author polaris
 * @date 2019年10月18日
 */
public interface IEncryptDecryptServer {

	/**
	 * 加密数据
	 * @param type 加密类型
	 * @param value 待加密字符串
	 * @return
	 */
	String encryptByType(String type, String value);

	/**
	 * 解密数据
	 * @param type 解密类型
	 * @param value 待解密字符串
	 * @return
	 */
	String decryptByType(String type, String value);

}
