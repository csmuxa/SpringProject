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
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;

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
        ModelMapper mapper = new ModelMapper();
        UserDto userDto = mapper.map(userDetails, UserDto.class);
        UserDto createdUser = userService.createUser(userDto);
        returnValue = mapper.map(createdUser, UserRest.class);
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

    @DeleteMapping(path = "/{id}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
    public OperationStatusModel deleteUser(@PathVariable String id) {
        OperationStatusModel returnValue = new OperationStatusModel();

        returnValue.setOperationName(RequestOperationName.DELETE.name());

        userService.deleteUser(id);

        returnValue.setOperationResult(RequestOperationStatus.SUCCESS.name());

        return returnValue;
    }

    @GetMapping
    public List<UserRest> getAllUsers(@RequestParam(value = "page", defaultValue = "0") int page, @RequestParam(value = "limit", defaultValue = "25") int limit) {

        List<UserRest> returnValue = new ArrayList<>();
        List<UserDto> users = userService.getUsers(page, limit);
        for (UserDto user : users) {
            UserRest rest = new UserRest();
            BeanUtils.copyProperties(user, rest);
            returnValue.add(rest);
        }
        return returnValue;
    }


    @GetMapping(path = "/{id}/addresses", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,"application/hal+json"})
    public Resources<AddressesRest> getAddresses(@PathVariable String id) {

        List<AddressesRest> returnValue = new ArrayList<>();

        List<AddressDto> addressDto = addressesService.getAddresses(id);
        Type listType = new TypeToken<List<AddressesRest>>() {
        }.getType();
        returnValue = new ModelMapper().map(addressDto, listType);
        for(AddressesRest addressesRest:returnValue){
            Link addressLink=linkTo(methodOn(UserController.class).getUserAddress(id,addressesRest.getAddressId())).withSelfRel();
            Link userLink=linkTo(methodOn(UserController.class).getUser(id)).withRel("user");
            addressesRest.add(addressLink);
            addressesRest.add(userLink);
        }
        return new Resources<>(returnValue);
    }


    @GetMapping(path = "/{userId}/addresses/{addressId}", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,"application/hal+json"})
    public Resource<AddressesRest> getUserAddress(  @PathVariable String userId,@PathVariable String addressId   ) {
        AddressDto addressDto = addressesService.getAddress(addressId);
        ModelMapper mapper = new ModelMapper();
        Link addressLink = linkTo(methodOn(UserController.class).getUserAddress(userId,addressId)).withSelfRel();
        Link userLink=linkTo(methodOn(UserController.class).getUser(userId)).withRel("user");
        Link addressesLink=linkTo(methodOn(UserController.class).getAddresses(userId)).withRel("addresses");
        AddressesRest returnValue = mapper.map(addressDto, AddressesRest.class);
        returnValue.add(addressLink);
        returnValue.add(userLink);
        returnValue.add(addressesLink);
        return new Resource<>(returnValue);

    }

    @GetMapping(path = "/email-verification", produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE,"application/hal+json"})
    public OperationStatusModel verifyEmailToken(@RequestParam(value = "token")String token){
        OperationStatusModel model=new OperationStatusModel();
        model.setOperationName(RequestOperationName.EMAIL_VERIFY.name());
        boolean isVerified=userService.verifyEmailToken(token);
        if(isVerified)
            model.setOperationResult(RequestOperationStatus.SUCCESS.name());
        else
            model.setOperationResult(RequestOperationStatus.ERROR.name());
        return model;

    }



}
