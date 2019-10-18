package com.msf.benchmark.annotation;

/**
 * 数据加密类型,01-证件号,02-手机号,03-结算账号,04-户名,05-地址,06-有效期,07-邮箱,08-默认
 * @author polaris
 * @date 2019年10月18日
 *
 */
public abstract class EncryptType {
    
    public static final String ENCRYPT_TYPE             = "ENCRYPT_TYPE";
    public static final String SIGN                     = ",";
    public static final String ENCRYPT_TYPE_CERTNO      = "01";
    public static final String ENCRYPT_TYPE_MOBILE      = "02";
    public static final String ENCRYPT_TYPE_CARDNO      = "03";
    public static final String ENCRYPT_TYPE_ACCOUNTNAME = "04";
    public static final String ENCRYPT_TYPE_ADDRESS     = "05";
    public static final String ENCRYPT_TYPE_VALIDDATE   = "06";
    public static final String ENCRYPT_TYPE_EMAIL       = "07";
    public static final String ENCRYPT_TYPE_DEFAULT     = "08";
}
