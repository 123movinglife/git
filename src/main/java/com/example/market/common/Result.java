package com.example.market.common;

import lombok.Data;

@Data
public class Result<T> {
    private Integer code;//200成功其他失败
    private String message;
    private T data;

    public static <T> Result<T> ok(T data){
        Result<T> r =new Result<>();
        r.code = 200;r.message = "success";r.data = data;
        return r;
    }
    public static <T> Result<T> ok(){
        return ok(null);
    }
    public static <T> Result<T> fail(String message){
        Result<T> r = new Result<>();
        r.code =500;r.message = message;
        return r;
    }
}
