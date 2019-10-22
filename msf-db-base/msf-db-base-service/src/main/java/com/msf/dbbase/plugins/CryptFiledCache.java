package com.msf.dbbase.plugins;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.persistence.Column;

import org.apache.commons.lang.StringUtils;

import com.msf.benchmark.annotation.CryptField;


class CryptFiledCache {

	private static ConcurrentMap<String, CryptDescribe> cache = new ConcurrentHashMap<String, CryptDescribe>();
	
	public static CryptDescribe getCryptDescribe(Class<?> clazz) {
		String key = clazz.getName();
		CryptDescribe describe = cache.get(clazz.getName());
		if(null == describe) {
			describe = buildCryptDescribe(clazz);
			cache.putIfAbsent(key, describe);
		}
		return describe;
	}

	/**
	 * 保存@CryptField字段及类型
	 * @param clazz
	 * @return
	 */
	private static CryptDescribe buildCryptDescribe(Class<?> clazz) {
		CryptDescribe describe = new CryptDescribe();
		Field[] fields = clazz.getDeclaredFields();
		for (Field field : fields) {
			CryptField crypt = field.getAnnotation(CryptField.class);
			if (null != crypt) {
				String type = crypt.type();
				describe.cryptField.put(field, type);
				Column tableField = field.getAnnotation(Column.class);
				if (tableField != null) {
					String columnName = tableField.name();
					if (!StringUtils.isBlank(columnName)) {
						describe.crypyColumn.put(columnName.toUpperCase(), type);
					}
				}
			}
		}
		return describe;

	}
	/**
	 * cryptField: 字段及加解密类型
	 * crypyColumn: 大写字段名及加解密类型
	 * @Description 
	 *
	 * @author 北辰不落雪
	 * @date 2019年10月18日
	 */
	static class CryptDescribe {
		private Map<Field, String> cryptField = new HashMap<Field, String>();

		private Map<String, String> crypyColumn = new HashMap<String, String>();

		public boolean hasCryptFiled() {
			return !cryptField.isEmpty();
		}

		public Map<Field, String> getCryptFields() {
			return cryptField;
		}

		public String getCryptColumnDesKey(String columnName) {
			return this.crypyColumn.get(columnName.toUpperCase());
		}

	}

}
