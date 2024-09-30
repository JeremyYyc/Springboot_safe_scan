package org.safescan.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Result<T> {
    private Integer code;
    private String message;
    private T data;

    public static <E> Result<E> success(E data){
        return new Result<>(0, "Successful operation", data);
    }

    public static <Object> Result<Object> success(){
        return new Result<>(0, "Successful operation",null);
    }

    public static <E> Result<E> success(String message, E data){
        return new Result<>(0, message, data);
    }

    public static <Object> Result<Object> error(String message){
        return new Result<>(1, message, null);
    }

    public static <E> Result <E> error(String message, E data) {
        return new Result<>(1, message, data);
    }

}
