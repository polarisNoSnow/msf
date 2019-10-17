package com.msf.benchmark.dubbo.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 生产者被调用日志
 *
 * @author 北辰不落雪
 * @time 2018年8月2日
 * 
 */

@Slf4j
@Activate
public class DubboServiceFilter implements Filter
{

	private static List<String> filterMethod = new ArrayList<String>();
	static {
		filterMethod.add("loginMobileService");
		filterMethod.add("setPassword");
		filterMethod.add("updateLoginPassword");
	}
 
    protected String LOG_IN = "请求入参";
    protected String LOG_OUT = "请求应答";
    
    public Result invoke(Invoker<?> invoker, Invocation invocation)throws RpcException {
    	//因为脱敏日志处理不支持非对象参数的脱敏处理，所以对包含有string类型的不过滤,也就是不打印日志
    	boolean isLog=true;
    	Object[]  arguments=invocation.getArguments();
    	for (Object argu : arguments) {
			if(argu instanceof String) {
				isLog=false;
				break;
			}
		}

    	//针对特定的方法进行过滤处理，不打印日志
		for (String method : filterMethod){
			if (method.equals(invocation.getMethodName())){
				isLog = false;
				break;
			}
		}
		//此处需要记录入参，dubbo-2xx会污染参数
		Object[] reqParam = null;
    	if(isLog) {
    		reqParam = invocation.getArguments();
    		log.info(LOG_IN+"method:[{}],request:{}",invocation.getMethodName(), JSON.toJSON(reqParam));
    	}
    	
        Result result = null;
        Long takeTime = 0L;
        boolean errFlag = false;
        try
        {
            Long startTime = System.currentTimeMillis();
            result = invoker.invoke(invocation);
            takeTime = System.currentTimeMillis() - startTime;
            if(result.hasException()) {
            	errFlag = true;
            }
        }
        catch (Exception e)
        {	
        	errFlag = true;
        	if(isLog) {
        		log.error("Exception:{},request{},curr error:{},msg:{}", invocation.getClass(),
                        invocation.getArguments(), e.toString(), ExceptionUtils.getRootCause(e));
        	}
        }
        finally
        {	/*异常无需打印json*/
        	if(errFlag){
        		if(isLog) {
        			log.info(LOG_OUT+"method:[{}],takeTime:{} ms,request:{},response:{}",
	        				invocation.getMethodName(), takeTime,JSON.toJSON(invocation.getArguments()), result);
        		}
        	}else{
        		if(isLog) {
        			log.info(LOG_OUT+"method:[{}],request:{},response:{},takeTime:{} ms",
	                        invocation.getMethodName(),JSON.toJSON(reqParam), JSON.toJSON(result.getValue()),takeTime);
        		}
        	}
        }
        return result;
    }
}

