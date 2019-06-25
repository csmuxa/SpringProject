package com.springProject.project.shared;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class UtilsTest extends Object {

    @Autowired
    Utils utils;

    @BeforeEach
    void setUp() throws Exception{

    }

    @Test
    void generateUserId() {
        String userId=utils.generateUserId(30);
        String userId2=utils.generateUserId(30);
        assertNotNull(userId2);
        assertNotNull(userId);
        assertTrue(userId.length()==30);
        assertTrue(!userId.equalsIgnoreCase(userId2));
    }

    @Test
    void hasTokenExpired() {
        String token=utils.generateEmailVerificationToken(utils.generateUserId(30));
        boolean hasTokenExpired=utils.hasTokenExpired(token);
        assertFalse(hasTokenExpired);
    }
}