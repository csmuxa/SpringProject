package com.springProject.project.service.serviceImpl;

import com.springProject.project.ProjectApplication;
import com.springProject.project.UserRepository;
import com.springProject.project.exceptions.UserServiceException;
import com.springProject.project.io.entity.AddressEntity;
import com.springProject.project.io.entity.UserEntity;
import com.springProject.project.io.repositories.PasswordResetTokenRepository;
import com.springProject.project.shared.AmazonSES;
import com.springProject.project.shared.Utils;
import com.springProject.project.shared.dto.AddressDto;
import com.springProject.project.shared.dto.UserDto;
import net.bytebuddy.build.ToStringPlugin;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.MapKey;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.verify;

class ServiceImplTest extends Object {

    @InjectMocks
    ServiceImpl service;

    @Mock
    UserRepository userRepository;

    @Mock
    Utils utils;

    @Mock
    AmazonSES amazonSES;

    @Mock
    BCryptPasswordEncoder bCryptPasswordEncoder;

    String userId = "ps5b3kv66vb";
    String encryptedPassword = "5beb6b7v3n";
    UserEntity userEntity = new UserEntity();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userEntity.setId(1L);
        userEntity.setFirstName("Muxammed");
        userEntity.setLastName("Qafarov");
        userEntity.setUserId("dhs7f7sig7s8g76v");
        userEntity.setEncryptedPassword(encryptedPassword);
        userEntity.setEmail("sasamba@gmail.com");
        userEntity.setEmailVerificationToken("Sadasda");
        userEntity.setAddresses(getAddressesEntity());
    }

    @Test
    void getUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);
        UserDto userDto = service.getUser("csmuxa@gmail.com");
        assertNotNull(userDto);
        assertEquals("Muxammed", userDto.getFirstName());
    }

    @Test
    void getUser_Exception() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThrows(UsernameNotFoundException.class,
                () -> {
                    service.getUser("test");
                }
        );
    }

    @Test
    final void testCreateUser_Exception(){
        when(userRepository.findByEmail(anyString())).thenReturn(userEntity);

        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Muxammed");
        userDto.setLastName("Qafarov");
        userDto.setEmail("tst@test.ru");
        userDto.setPassword("3123112313");

        assertThrows(UserServiceException.class,
                () -> {
                    service.createUser(userDto);
                }
        );
    }


    @Test
    final void testCreateUser() {

        when(userRepository.findByEmail(anyString())).thenReturn(null);
        when(utils.generateAddressId(anyInt())).thenReturn("dfdfgf6f7h78s");
        when(utils.generateUserId(anyInt())).thenReturn(userId);
        when(bCryptPasswordEncoder.encode(anyString())).thenReturn(encryptedPassword);
        when(userRepository.save(any(UserEntity.class))).thenReturn(userEntity);
        Mockito.doNothing().when(amazonSES).verifyEmail(any(UserDto.class));


        UserDto userDto = new UserDto();
        userDto.setAddresses(getAddressesDto());
        userDto.setFirstName("Muxammed");
        userDto.setLastName("Qafarov");
        userDto.setEmail("tst@test.ru");
        userDto.setPassword("3123112313");

        UserDto storedUser = service.createUser(userDto);
        assertNotNull(storedUser);
        assertEquals(userEntity.getFirstName(), storedUser.getFirstName());
        assertEquals(userEntity.getLastName(), storedUser.getLastName());
        assertNotNull(storedUser.getUserId());
        assertEquals(storedUser.getAddresses().size(), userEntity.getAddresses().size());
        verify(utils,times(storedUser.getAddresses().size())).generateAddressId(30);
        verify(bCryptPasswordEncoder,times(1)).encode("3123112313");
        verify(userRepository,times(1)).save(any(UserEntity.class));
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

    private List<AddressEntity> getAddressesEntity() {
        List<AddressDto> addresses = getAddressesDto();
        Type listType = new TypeToken<List<AddressEntity>>() {
        }.getType();
        return new ModelMapper().map(addresses, listType);

    }
}