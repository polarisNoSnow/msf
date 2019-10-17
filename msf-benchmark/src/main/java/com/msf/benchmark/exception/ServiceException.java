package com.msf.benchmark.exception;

/** 各个业务模块抛出的异常
 * @Description
 *
 * @author 北辰不落雪
 * @time 2019年10月16日
 * 
 */
public class ServiceException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String errorCode = "520";
	private String errorMessage;
	
	public ServiceException() {
		super();
	}
	public ServiceException(String errorMessage) {
		super(errorMessage);
		this.errorMessage = errorMessage;
    }
	public ServiceException(String errorCode, String errorMessage) {
		super("["+errorCode+"]"+errorMessage);
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}
	
	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	
}
