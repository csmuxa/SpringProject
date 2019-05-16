package com.springProject.project.service;

import com.springProject.project.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface Service extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id, UserDto userDto);
    void deleteUser(String id);
    List<UserDto> getUsers(int page,int limit);

}
