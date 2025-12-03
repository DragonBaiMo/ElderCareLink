package com.eldercarelink.common;

/**
 * 业务异常，用于向上抛出可预期的提示信息。
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
