package com.springProject.project.shared;

import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;
@Component
public class Utils {
    private final Random RANDOM=new SecureRandom();
    private final String ALPHABET="0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";

    private String generateRandomString(int length){
    StringBuffer buffer=new StringBuffer(length);
    for(int i=0;i<length;i++){
        buffer.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
    }
    return new String(buffer);
    }
    public String generateUserId(int length){
        return generateRandomString(length);
    }

    public String generateAddressId(int length){
        return generateRandomString(length);
    }
    
}