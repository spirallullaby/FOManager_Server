package com.FOManager.Server;

public class ApiResultModel<T> {
    public boolean success;
    
    public String errorMessage;

    public T result;
}