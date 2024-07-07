package com.tms.exception;

public class ErrorModel {

	private int statusCode;
	private String errorMessage;
	public String getErrorMessage() {
		return errorMessage;
	}
	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}
	public int getStatusCode() {
		return statusCode;
	}
	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}
	@Override
	public String toString() {
		return "ErrorModel [statusCode=" + statusCode + ", errorMessage=" + errorMessage + "]";
	}
	public ErrorModel(int statusCode, String errorMessage) {
		super();
		this.statusCode = statusCode;
		this.errorMessage = errorMessage;
	}
	public ErrorModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
