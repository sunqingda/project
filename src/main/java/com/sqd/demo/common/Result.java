package com.sqd.demo.common;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.List;

@Data
@Builder
public class Result<T>{
    private T data;
    private boolean success;
    private String errorCode;
    private String message;

    public static<T> Result success(T data){
        return new Result(data, true, null, null);
    }

    public static<T> Result<T> fail(String errorCode, String message){
        return new Result(Collections.emptyList(), false, errorCode, message);
    }
}
