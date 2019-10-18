package com.msf.dbbase.plugins;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.msf.benchmark.annotation.EncryptType;
import com.msf.dbbase.encrypt.IEncryptDecryptServer;
import com.msf.dbbase.plugins.CryptFiledCache.CryptDescribe;

import cn.hutool.core.util.ReflectUtil;

@Intercepts(@Signature(method = "query", type = Executor.class, args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }))
@Component
public class ExecutorQueryInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(ExecutorQueryInterceptor.class);
	
	@Reference(interfaceClass=IEncryptDecryptServer.class,registry = "decrypt")
	IEncryptDecryptServer encryptDecryptServer;

	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		beforeParamBinding(invocation);
		Object processResult = invocation.proceed();
		if (null != processResult) {
			List<?> list = (List<?>) processResult;
			if (!list.isEmpty()) {
				CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(list.get(0).getClass());
				if (cryptDescribe.hasCryptFiled()) {
					for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
						for (Object entity : list) {
							decrypt(entity, entry.getKey(), entry.getValue());
						}
					}
				}
			}
		}
		return processResult;
	}

	private void beforeParamBinding(Invocation invocation) {
		Object[] args = invocation.getArgs();
		if(args[1]==null){
			return ;
		}
		//暂时没有map传参
		if (args[1] instanceof Map) {
			Map<String, Object> paramMap = (Map<String, Object>) args[1];
			for (Entry<String, Object> entry : paramMap.entrySet()) {
				String key = entry.getKey();
				if (StringUtils.contains(key, EncryptType.ENCRYPT_TYPE)) {
					Object value = entry.getValue();
					if (value instanceof String) {
						String useValue = (String) value;
						if (StringUtils.isNotBlank(useValue)) {
							log.info(String.format("查询待加密字段:%s", key));
							try {
								useValue=encryptDecryptServer.encryptByType(key.split(EncryptType.SIGN)[1], useValue);
								log.info(String.format("查询加密后:%s", value));
							} catch (Exception e) {
								log.error("查询调用接口加密异常,异常信息{}",e);
								throw e;
							}
							paramMap.put(key, useValue);
						}
					}
				}
			}
		} else {
			CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(args[1].getClass());
			if (cryptDescribe.hasCryptFiled()) {
				for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
					encrypt(args[1], entry.getKey(), entry.getValue());
				}
			}
		}
	}
	
	private void encrypt(Object entity, Field f, String type) {
		String value = (String) ReflectUtil.getFieldValue(entity, f);
		if (StringUtils.isNotBlank(value)) {
			log.info("待加密字段{},{},{}", entity.getClass().getName(), f.getName(),type);
			try {
				value=encryptDecryptServer.encryptByType(type, value);
				log.info(String.format("加密后:%s", value));
			} catch (Exception e) {
				log.error("调用接口加密异常,异常信息{}",e);
				throw e;
			}
			ReflectUtil.setFieldValue(entity, f, value);
		}
	}

	private void decrypt(Object entity, Field f, String type) {
		String value = (String) ReflectUtil.getFieldValue(entity, f);
		if (StringUtils.isNotBlank(value)) {
			log.info("待解密字段{},{},{},{}", entity.getClass().getName(), f.getName(),type,value);
			try {
				synchronized (value) {
					value=encryptDecryptServer.decryptByType(type, value);
				}
				if(isMessyCode(value)){
					log.info("解密乱码{}",value);
					value=(String) ReflectUtil.getFieldValue(entity, f);
					throw new RuntimeException("解密乱码");
				}else{
					log.info(String.format("解密成功"));
				}
			} catch (Exception e) {
				log.error("调用接口解密异常");
				throw e;
			}
			ReflectUtil.setFieldValue(entity, f, value);
		}
	}
	
	/**
	 * 判断是否为乱码
	 * @param str
	 * @return
	 * @author 杨旋
	 * @since 2019年1月4日
	 */
	 public static boolean isMessyCode(String str) {
		  for (int i = 0; i < str.length(); i++) {
		   char c = str.charAt(i);
		   // 当从Unicode编码向某个字符集转换时，如果在该字符集中没有对应的编码，则得到0x3f（即问号字符?）
		   //从其他字符集向Unicode编码转换时，如果这个二进制数在该字符集中没有标识任何的字符，则得到的结果是0xfffd
		   //System.out.println("--- " + (int) c);
		   if ((int) c == 0xfffd) {
		    // 存在乱码
		    //System.out.println("存在乱码 " + (int) c);
		    return true;
		   }
		  }
		  return false; 
		 }

	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	@Override
	public void setProperties(Properties properties) {
		 // Do nothing because 暂时不需要
	}


}
