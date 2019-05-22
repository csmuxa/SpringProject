package com.springProject.project.service.serviceImpl;

import com.springProject.project.exceptions.UserServiceException;
import com.springProject.project.UserRepository;
import com.springProject.project.io.entity.UserEntity;
import com.springProject.project.iu.model.response.ErrorMessages;
import com.springProject.project.service.Service;
import com.springProject.project.shared.Utils;
import com.springProject.project.shared.dto.AddressDto;
import com.springProject.project.shared.dto.UserDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.List;


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
        if(userRepository.findByEmail(userDto.getEmail())!=null)
        throw new RuntimeException("Record already exists");

        for(int i=0;i<userDto.getAddresses().size();i++){
            AddressDto address=userDto.getAddresses().get(i);
            address.setUserDetails(userDto);
            address.setAddressId(utils.generateAddressId(30));
            userDto.getAddresses().set(i,address);
        }

        ModelMapper mapper=new ModelMapper();
        UserEntity userEntity=mapper.map(userDto,UserEntity.class);
        userEntity.setEncryptedPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
        userEntity.setUserId(utils.generateUserId(30));

        UserEntity returnEntity = userRepository.save(userEntity);
        UserDto returnDto=mapper.map(returnEntity,UserDto.class);
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
    public UserDto updateUser(String id, UserDto userDto) {
        UserDto returnValue=new UserDto();
        UserEntity userEntity=userRepository.findByUserId(id);
        if (userEntity==null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        UserEntity updatedUser=userRepository.save(userEntity);
        BeanUtils.copyProperties(updatedUser,returnValue);

        return returnValue;
    }

    @Override
    public void deleteUser(String id) {
        UserEntity userEntity=userRepository.findByUserId(id);
        if (userEntity==null) throw new UserServiceException(ErrorMessages.NO_RECORD_FOUND.getErrorMessage());
        userRepository.delete(userEntity);
    }

    @Override
    public List<UserDto> getUsers(int page, int limit) {
        List<UserDto> returnValue=new ArrayList<>();
        if(page>0)page--;
        Pageable  pageRequest= PageRequest.of(page,limit);
        Page<UserEntity> pages=userRepository.findAll(pageRequest);
        List<UserEntity> users=pages.getContent();
        for(UserEntity entity : users){
            UserDto dto=new UserDto();
            BeanUtils.copyProperties(entity,dto);
            returnValue.add(dto);
        }
        return returnValue;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) throw new UsernameNotFoundException(email);
        return new User(userEntity.getEmail(), userEntity.getEncryptedPassword(), new ArrayList<>());
    }
}
