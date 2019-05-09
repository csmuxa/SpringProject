package com.springProject.project.service.serviceImpl;

import com.springProject.project.io.repositories.UserRepository;
import com.springProject.project.io.entity.UserEntity;
import com.springProject.project.service.Service;
import com.springProject.project.shared.Utils;
import com.springProject.project.shared.dto.UserDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;


@org.springframework.stereotype.Service
public class ServiceImpl implements Service {

    @Autowired
    UserRepository userRepository;
    @Autowired
    Utils utils;
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder;


    @Override
    public UserDto createUser(UserDto userDto) {
        UserEntity userEntity = new UserEntity();

        BeanUtils.copyProperties(userDto, userEntity);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        UserEntity returnEntity = userRepository.save(userEntity);
        UserDto returnDto = new UserDto();
        BeanUtils.copyProperties(returnEntity, returnDto);
        return returnDto;
    }

    @Override
    public UserDto getUser(String email) {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        UserDto returnDto = new UserDto();
        BeanUtils.copyProperties(userEntity, returnDto);
        return returnDto;
    }

    @Override
    public UserDto getUserByUserId(String id) {
        UserDto returnValue=new UserDto();
        UserEntity userEntity=userRepository.findByUserId(id);
        if (userEntity==null) throw new UsernameNotFoundException(id);
        BeanUtils.copyProperties(userEntity,returnValue);
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
