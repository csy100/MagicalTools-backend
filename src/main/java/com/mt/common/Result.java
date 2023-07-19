package com.mt.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Author: csy100
 * Date: 2023/7/15
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Result {
    
    private Boolean success;
    private String error;
    private Object data;
    private Long total;
    
    public static Result ok(){
        return new Result(true, null, null, null);
    }
    public static Result ok(Object data){
        return new Result(true, null, data, null);
    }
    public static Result ok(List<?> data, Long total){
        return new Result(true, null, data, total);
    }
    public static Result fail(String error){
        return new Result(false, error, null, null);
    }
}
