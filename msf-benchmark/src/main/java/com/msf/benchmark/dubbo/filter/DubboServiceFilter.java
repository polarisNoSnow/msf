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
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

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
	/**
	 * 特定方法无需日志打印
	 */
	static {
		filterMethod.add("loginMobileService");
		filterMethod.add("setPassword");
		filterMethod.add("updateLoginPassword");
	}
 
    protected String logIn = "请求入参";
    protected String logOut = "请求应答";
    
    @Override
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
		//底层服务可能会污染入参，此处需记录
		JSONArray reqParam = null;
    	if(isLog) {
    		reqParam = (JSONArray) JSON.toJSON(invocation.getArguments());
    		log.info(logIn+"method:[{}],request:{}",invocation.getMethodName(), reqParam);
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
        			log.info(logOut+"method:[{}],takeTime:{} ms,request:{},response:{}",
	        				invocation.getMethodName(), takeTime,JSON.toJSON(invocation.getArguments()), result);
        		}
        	}else{
        		if(isLog) {
        			log.info(logOut+"method:[{}],request:{},response:{},takeTime:{} ms",
	                        invocation.getMethodName(),reqParam, JSON.toJSON(result.getValue()),takeTime);
        		}
        	}
        }
        return result;
    }
}

