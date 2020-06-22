package com.rdc.zrj.flow.hystrixcloud.util;

public class UserContextHolder {
    public static final ThreadLocal<String> userContext = new ThreadLocal<>();

}
