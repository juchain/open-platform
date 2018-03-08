package com.blockshine.api.dto;

import lombok.Data;

@Data
public class BShineResponse<T> {

    public static <T> BShineResponse<T> createSuccessStatus() {
        return createSuccessStatus(null);
    }

    public static <T> BShineResponse<T> createSuccessStatus(T result) {
    		BShineResponse<T> status = createSuccessStatus();
        status.setResult(result);
        return status;
    }

    public static <T> BShineResponse<T> createErrorStatus(String message, Object... args) {
    	BShineResponse<T> status = new BShineResponse<>();
        status.setStatus(ResultStatus.Failed);
        status.setMessage(String.format(message, args));
        return status;
    }

    private ResultStatus status = ResultStatus.Successed;
    private String message;
    private T result;

    public enum ResultStatus {
    		Successed(0), Failed(1);
    		 
    		int code;
    		private ResultStatus(int code) {
    			this.code = code;
    		}
    }
    
}
