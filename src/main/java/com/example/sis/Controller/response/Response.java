package com.example.sis.Controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Response<T> { // 여러 분야에 많이 쓰이기 때문에 Generic(타입을 신경 안씀)으로 처리해준다
    private String resultCode;
    private T result;

    // 실패했을 경우
    public static Response<Void> error(String errorCode) {
        return new Response<>(errorCode, null);
    }
    //성공했을 경우
    public static <T> Response<T> success(T result) {
        return new Response<>("SUCCESS", result);
    }
    public static Response<Void> success() {
        return new Response<>("SUCCESS", null);
    }

    public String toStream() {
        if (result == null) {
            return "{" +
                    "\"resultCode\":" + "\"" + resultCode + "\"," +
                    "\"result\":" + null +
                    "}";
        }
        return "{" +
                "\"resultCode\":" + "\"" + resultCode + "\"," +
                "\"result\":" + "\"" + result + "\"," +
                "}";
    }
}
