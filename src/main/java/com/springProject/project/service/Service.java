package com.springProject.project.service;

import com.springProject.project.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface Service extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
}
