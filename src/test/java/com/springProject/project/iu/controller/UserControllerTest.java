package com.springProject.project.iu.controller;

import com.springProject.project.iu.model.response.UserRest;
import com.springProject.project.service.serviceImpl.ServiceImpl;
import com.springProject.project.shared.dto.AddressDto;
import com.springProject.project.shared.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.persistence.ManyToOne;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

class UserControllerTest extends Object {

    private final String USER_ID = "bh4h2j5b8v4k3k5b7v3n";
    @InjectMocks
    UserController userController;

    @Mock
    ServiceImpl service;

    UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);

        userDto=new UserDto();
        userDto.setFirstName("Muxa");
        userDto.setLastName("Qafar");
        userDto.setEmail("test@test.com");
        userDto.setEmailVerificationStatus(false);
        userDto.setEmailVerificationToken(null);
        userDto.setUserId(USER_ID);
        userDto.setAddresses(getAddressesDto());
        userDto.setEncryptedPassword("ejsjfjjsnc6disic7s8");
    }


    @Test
    void getUser() {
        when(service.getUserByUserId(anyString())).thenReturn(userDto);
        UserRest userRest=userController.getUser(USER_ID);
        assertNotNull(userRest);
        assertEquals(USER_ID,userRest.getUserId());
        assertEquals(userDto.getFirstName(),userRest.getFirstName());
        assertEquals(userDto.getLastName(),userRest.getLastName());
        assertTrue(userDto.getAddresses().size()==userRest.getAddresses().size());
    }





    private List<AddressDto> getAddressesDto() {
        AddressDto addressDto = new AddressDto();
        addressDto.setType("shipping");
        addressDto.setCity("Vancuver");
        addressDto.setCountry("Canada");
        addressDto.setPostalCode("AB1234");
        addressDto.setStreetName("123 street");

        AddressDto billingAddressDto = new AddressDto();
        billingAddressDto.setType("shipping");
        billingAddressDto.setCity("Vancuver");
        billingAddressDto.setCountry("Canada");
        billingAddressDto.setPostalCode("AB1234");
        billingAddressDto.setStreetName("123 street");

        List<AddressDto> addresses = new ArrayList<AddressDto>();
        addresses.add(addressDto);
        addresses.add(billingAddressDto);

        return addresses;
    }



}