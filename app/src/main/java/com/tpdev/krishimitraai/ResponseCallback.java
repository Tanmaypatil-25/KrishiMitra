package com.tpdev.krishimitraai;

public interface ResponseCallback {
    void onResponse(String response);
    void onError(Throwable throwable);
}
