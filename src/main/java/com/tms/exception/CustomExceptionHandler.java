package com.tms.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler {

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleFieldExceptions(MethodArgumentNotValidException exception) {
		String errorMessage = "";
		List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
		for (FieldError fieldError : fieldErrors)
			errorMessage = errorMessage + fieldError.getDefaultMessage() + "\n";
		return new ResponseEntity<>(errorMessage, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<?> handleBusinessExceptions(BusinessException bex) {
		String errors = "";
		int code = 400;
		for (ErrorModel error : bex.getErrors()) {
			errors = errors + error.getErrorMessage() + "\n";
			code = error.getStatusCode();
		}
		return new ResponseEntity<>(errors, HttpStatus.valueOf(code));

	}

}
