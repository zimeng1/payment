package com.mc.payment.core.service.model.req;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UserSaveReqTest {

    public static final String REGEX = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[!@#$%^&*]).{8,30}$";
    private UserSaveReq userSaveReq;

    @BeforeEach
    public void setUp() {
        userSaveReq = new UserSaveReq();
    }

    @Test
    public void testPasswordValid() {
        String validPassword = "Aa123456!@#";
        userSaveReq.setPassword(validPassword);
        assertTrue(userSaveReq.getPassword().matches(REGEX));
    }

    @Test
    public void testPasswordInvalidLength() {
        String invalidPassword = "Aa12345";
        userSaveReq.setPassword(invalidPassword);
        assertFalse(userSaveReq.getPassword().matches(REGEX));
    }

    @Test
    public void testPasswordMissingUpperCase() {
        String invalidPassword = "aa123456!@#";
        userSaveReq.setPassword(invalidPassword);
        assertFalse(userSaveReq.getPassword().matches(REGEX));
    }

    @Test
    public void testPasswordMissingLowerCase() {
        String invalidPassword = "A123456!@#";
        userSaveReq.setPassword(invalidPassword);
        assertFalse(userSaveReq.getPassword().matches(REGEX));
    }

    @Test
    public void testPasswordMissingDigit() {
        String invalidPassword = "Aa!@#$%^&*";
        userSaveReq.setPassword(invalidPassword);
        assertFalse(userSaveReq.getPassword().matches(REGEX));
    }

    @Test
    public void testPasswordMissingSpecialCharacter() {
        String invalidPassword = "Aa123456";
        userSaveReq.setPassword(invalidPassword);
        assertFalse(userSaveReq.getPassword().matches(REGEX));
    }
}
