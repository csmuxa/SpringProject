package com.springProject.project.iu.controller;


import com.springProject.project.iu.model.request.RequestOperationStatus;
import com.springProject.project.iu.model.response.AddressesRest;
import com.springProject.project.iu.model.response.OperationStatusModel;
import com.springProject.project.service.AddressService;
import com.springProject.project.service.UserService;
import com.springProject.project.shared.dto.AddressDto;
import com.springProject.project.shared.dto.UserDto;
import com.springProject.project.iu.model.request.UserDetailsRequestModel;
import com.springProject.project.iu.model.response.UserRest;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("users")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    AddressService addressesService;

    @GetMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest getUser(@PathVariable String id) {

        UserRest returnValue = new UserRest();

        UserDto dto = userService.getUserByUserId(id);

        BeanUtils.copyProperties(dto, returnValue);

        return returnValue;
    }

    @PostMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})

    public UserRest createUser(@RequestBody UserDetailsRequestModel userDetails) throws Exception {
        UserRest returnValue = new UserRest();
        ModelMapper mapper=new ModelMapper();
        UserDto userDto=mapper.map(userDetails,UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        returnValue=mapper.map(createdUser,UserRest.class);
        return returnValue;
    }

    @PutMapping(path = "/{id}",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE},
            consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public UserRest updateUser(@RequestBody UserDetailsRequestModel userDetails, @PathVariable String id) {
        UserRest returnValue = new UserRest();
        UserDto userDto = new UserDto();
        BeanUtils.copyProperties(userDetails, userDto);
        UserDto updateUser = userService.updateUser(id, userDto);
        BeanUtils.copyProperties(updateUser, returnValue);

        return returnValue;
    }

    @DeleteMapping(path = "/{id}",produces ={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE} )
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();

        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping
    public List<UserRest> getAllUsers(@RequestParam(value = "page",defaultValue = "0")int page, @RequestParam(value = "limit",defaultValue = "25")int limit){

        List<UserRest> returnValue=new ArrayList<>();
        List<UserDto> users=userService.getUsers(page,limit);
        for (UserDto user:users){
            UserRest rest=new UserRest();
            BeanUtils.copyProperties(user,rest);
            returnValue.add(rest);
        }
        return returnValue;
    }



    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public List<AddressesRest> getAddresses(@PathVariable String id) {

        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressDto=addressesService.getAddresses(id);
        Type listType = new TypeToken<List<AddressesRest>>() {}.getType();
        returnValue = new ModelMapper().map(addressDto, listType);


        return returnValue;
    }


    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public AddressesRest getUserAddress(@PathVariable String addressId) {
        AddressDto addressDto=addressesService.getAddress(addressId);
        ModelMapper mapper=new ModelMapper();
        AddressesRest returnValue=mapper.map(addressDto,AddressesRest.class);
        return returnValue;

    }



    }
