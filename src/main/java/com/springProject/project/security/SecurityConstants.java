package com.springProject.project.security;

import com.springProject.project.SpringApplicationContext;

public class SecurityConstants {
    public static final long EXPIRATION_TIME = 864000000; // 10 days
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/SpringApp/users";
    public static final String EMAIL_VERIFICATION_URL="/SpringApp/users/email-verification";
    public static final String PASSWORD_RESET_URL="/SpringApp/users/password-reset";
    public static long PASSWORD_RESET_EXPIRATION_TIME=1000*60*60;
    public static String PASSWORD_RESET_REQUEST_URL="/SpringApp/users/password-reset-request";


    public static String getToken(){
        AppProperties appProperties=(AppProperties) SpringApplicationContext.getBean("appProperties");
        return appProperties.getToken();
    }

}
