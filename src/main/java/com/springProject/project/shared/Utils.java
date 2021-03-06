package com.springProject.project.shared;

import com.springProject.project.security.SecurityConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.Random;

@Service
public class Utils {
    private final Random RANDOM = new SecureRandom();
    private final String ALPHABET = "0123456789QWERTYUIOPASDFGHJKLZXCVBNMqwertyuiopasdfghjklzxcvbnm";



    private String generateRandomString(int length) {
        StringBuffer buffer = new StringBuffer(length);
        for (int i = 0; i < length; i++) {
            buffer.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return new String(buffer);
    }

    public String generateUserId(int length) {
        return generateRandomString(length);
    }

    public String generateAddressId(int length) {
        return generateRandomString(length);
    }

    public boolean hasTokenExpired(String token) {
        boolean returnValue=true;
        try {

            Claims claims = Jwts.parser().setSigningKey(SecurityConstants.getToken()).parseClaimsJws(token).getBody();

            Date tokenExpirationDate = claims.getExpiration();
            Date todayDate = new Date();

            returnValue= tokenExpirationDate.before(todayDate);
        }
        catch (ExpiredJwtException e){
            returnValue= true;
        }

        return returnValue;

    }


    public String generateEmailVerificationToken(String userId) {
        String token = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.EXPIRATION_TIME)).signWith(SignatureAlgorithm.HS512, SecurityConstants.getToken()).compact();
        return token;
    }
    public  String generatePasswordResetToken(String userId) {
        String token = Jwts.builder().setSubject(userId).setExpiration(new Date(System.currentTimeMillis() + SecurityConstants.PASSWORD_RESET_EXPIRATION_TIME)).signWith(SignatureAlgorithm.HS512, SecurityConstants.getToken()).compact();
        return token;
    }

}