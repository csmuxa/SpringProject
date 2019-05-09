package com.springProject.project.iu.controller;


import com.springProject.project.service.Service;
import com.springProject.project.shared.Utils;
import com.springProject.project.shared.dto.UserDto;
import com.springProject.project.iu.model.request.UserDetailsRequestModel;
import com.springProject.project.iu.model.response.UserRest;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    Service userService;


    @GetMapping(path = "/{id}")
    public UserRest getUser(@PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto dto = userService.getUserByUserId(id);
        BeanUtils.copyProperties(dto, returnValue);
        return returnValue;
    }

    @PostMapping
    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) {
        UserRest returnValue = new UserRest();
        String name = userDetails.getFirstName();
        String last = userDetails.getLastName();

        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);


        UserDto createdUser = userService.createUser(userDto);

        BeanUtils.copyProperties(createdUser, returnValue);

        return returnValue;
    }

    @PutMapping
    public String updateUser() {
        return "put";
    }

    @DeleteMapping
    public String deleteUser() {
        return "delete";
    }

}
