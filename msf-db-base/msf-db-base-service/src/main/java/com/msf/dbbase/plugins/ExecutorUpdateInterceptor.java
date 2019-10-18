package com.msf.dbbase.plugins;

import java.lang.reflect.Field;
import java.sql.Wrapper;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.ibatis.binding.MapperMethod;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.defaults.DefaultSqlSession.StrictMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.msf.dbbase.encrypt.IEncryptDecryptServer;
import com.msf.dbbase.plugins.CryptFiledCache.CryptDescribe;

import cn.hutool.core.util.ReflectUtil;

//@Intercepts用于表明当前的对象是一个Interceptor
//@Signature则表明要拦截的接口、方法以及对应的参数类型
@Intercepts({ 
	@Signature(type = Executor.class, method = "update", args = { MappedStatement.class, Object.class}),
})
@Component
public class ExecutorUpdateInterceptor implements Interceptor {

	private static final Logger log = LoggerFactory.getLogger(ExecutorUpdateInterceptor.class);

	private static final String NAME_ENTITY = "et";
	private static final String NAME_ENTITY_WRAPPER = "ew";
	
	
	@Reference(interfaceClass=IEncryptDecryptServer.class,registry = "decrypt")
	 IEncryptDecryptServer encryptDecryptServer;
	

	//要进行拦截的时候要执行的方法
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		Object[] args = invocation.getArgs();
		MappedStatement ms = (MappedStatement) args[0];
		switch (ms.getSqlCommandType()) {
		case INSERT:
			processInsert(args);
			break;
		case UPDATE:
			processUpdate(args);
			break;
		case DELETE:
			processUpdate(args);
			break;
		default:
			break;
		}
		Object result = invocation.proceed();
		switch (ms.getSqlCommandType()) {
		case INSERT:
			processDecryptInsert(args);
			break;
		case UPDATE:
			processDecryptUpdate(args);
			break;
		case DELETE:
			processDecryptUpdate(args);
			break;
		default:
			break;
		}
		return result;
	}

	private void processDecryptUpdate(Object[] args) {

		Object param = args[1];
		if (param instanceof MapperMethod.ParamMap) {
			MapperMethod.ParamMap map = (MapperMethod.ParamMap) param;
			Wrapper ew = null;
			if (map.containsKey(NAME_ENTITY_WRAPPER)) {// mapper.update(updEntity, EntityWrapper<>(whereEntity);
				ew = (Wrapper) map.get(NAME_ENTITY_WRAPPER);
			}
			Object et = null;
			if (map.containsKey(NAME_ENTITY)) {
				et = map.get(NAME_ENTITY);
			}
			if (null != et) {
				CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(et.getClass());
				if (cryptDescribe.hasCryptFiled()) {
					for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
						decrypt(et, entry.getKey(), entry.getValue());
					}
				}
			}
		} else {
			CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(param.getClass());
			if (cryptDescribe.hasCryptFiled()) {
				for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
					decrypt(param, entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private void processDecryptInsert(Object[] args) {
		Object entity = args[1];
		if (null != entity) {
			CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(entity.getClass());
			if (cryptDescribe.hasCryptFiled()) {
				for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
					decrypt(entity, entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private void processInsert(Object[] args) {
		Object entity = args[1];
		Class clazz=entity.getClass();
		if(entity instanceof StrictMap){
			for(Entry<String,Object> mapEntry:((StrictMap<Object>) entity).entrySet()){
				List<?> list=(List<?>) mapEntry.getValue();
				if("list".equals(mapEntry.getKey())){
					for (Object object : list) {
						clazz=object.getClass();
						entity=object;
						CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(clazz);
						if (cryptDescribe.hasCryptFiled()) {
							for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
								encrypt(entity, entry.getKey(), entry.getValue());
							}
						}
					}
				}
			}
		}else if (null != entity) {
			CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(clazz);
			if (cryptDescribe.hasCryptFiled()) {
				for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
					encrypt(entity, entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private void processUpdate(Object[] args) {
		Object param = args[1];
		if (param instanceof MapperMethod.ParamMap) {
			MapperMethod.ParamMap map = (MapperMethod.ParamMap) param;
			Wrapper ew = null;
			if (map.containsKey(NAME_ENTITY_WRAPPER)) {// mapper.update(updEntity, EntityWrapper<>(whereEntity);
				ew = (Wrapper) map.get(NAME_ENTITY_WRAPPER);
			}
			Object et = null;
			if (map.containsKey(NAME_ENTITY)) {
				et = map.get(NAME_ENTITY);
			}
			if (null != et) {
				CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(et.getClass());
				if (cryptDescribe.hasCryptFiled()) {
					for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
						encrypt(et, entry.getKey(), entry.getValue());
					}
				}
			}
		} else {
			CryptDescribe cryptDescribe = CryptFiledCache.getCryptDescribe(param.getClass());
			if (cryptDescribe.hasCryptFiled()) {
				for (Entry<Field, String> entry : cryptDescribe.getCryptFields().entrySet()) {
					encrypt(param, entry.getKey(), entry.getValue());
				}
			}
		}
	}

	private void decrypt(Object entity, Field f, String type) {
		String value = (String) ReflectUtil.getFieldValue(entity, f);
		if (StringUtils.isNotBlank(value)) {
			log.info("待解密字段{},{},{},{}", entity.getClass().getName(), f.getName(),type,value);
			try {
				value=encryptDecryptServer.decryptByType(type, value);
				log.info(String.format("解密成功"));
			} catch (Exception e) {
				log.error("调用解密服务异常,异常信息{}",e);
				throw e;
			}
			ReflectUtil.setFieldValue(entity, f, value);
		}
	}

	private synchronized void encrypt(Object entity, Field f, String type) {
		String value = (String) ReflectUtil.getFieldValue(entity, f);
		if (StringUtils.isNotBlank(value)) {
			log.info("待加密字段{},{},{}", entity.getClass().getName(), f.getName(),type);
			try {
				value=encryptDecryptServer.encryptByType(type, value);
				log.info(String.format("加密后:%s", value));
			} catch (Exception e) {
				log.error("调用加密服务异常,异常信息{}",e);
				throw e;
			}
			ReflectUtil.setFieldValue(entity, f, value);
		}
	}

	//拦截器用于封装目标对象的，通过该方法我们可以返回目标对象本身，也可以返回一个它的代理
	@Override
	public Object plugin(Object target) {
		if (target instanceof Executor) {
			return Plugin.wrap(target, this);
		}
		return target;
	}

	//用于在Mybatis配置文件中指定一些属性的
	@Override
	public void setProperties(Properties properties) throws UnsupportedOperationException {
		//暂时不需要
	}

}
