package com.springProject.project.service;

import com.springProject.project.shared.dto.UserDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {
    UserDto createUser(UserDto userDto);
    UserDto getUser(String email);
    UserDto getUserByUserId(String id);
    UserDto updateUser(String id, UserDto userDto);
    void deleteUser(String id);
    List<UserDto> getUsers(int page,int limit);
    boolean verifyEmailToken(String token);
    boolean requestPasswordReset(String email);

    boolean resetPassword(String token, String password);
}
