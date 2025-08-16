package org.demon.vulmanage.common;

/**
 * 统一接口返回对象
 * @param <T> 返回数据类型
 */
public class Result<T> {
    
    /**
     * 状态码，200为成功，其他为失败
     */
    private Integer code;
    
    /**
     * 返回消息，成功返回"成功"，失败返回失败原因
     */
    private String message;
    
    /**
     * 返回给前端的具体业务数据
     */
    private T data;
    
    public Result() {}
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }
    
    /**
     * 成功返回，带数据
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(200, "成功", data);
    }
    
    /**
     * 成功返回，不带数据
     */
    public static <T> Result<T> success() {
        return new Result<>(200, "成功", null);
    }
    
    /**
     * 成功返回，自定义消息
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(200, message, data);
    }
    
    /**
     * 失败返回
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(500, message, null);
    }
    
    /**
     * 失败返回，自定义状态码
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    public Integer getCode() {
        return code;
    }
    
    public void setCode(Integer code) {
        this.code = code;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public T getData() {
        return data;
    }
    
    public void setData(T data) {
        this.data = data;
    }
}