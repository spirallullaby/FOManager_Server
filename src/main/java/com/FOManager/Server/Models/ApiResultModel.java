package com.FOManager.Server.Models;

public class ApiResultModel<T> {
    public boolean Success = false;
    
    public String ErrorMessage = "";

    public T Result;

}