package com.springProject.project.service.serviceImpl;

import com.springProject.project.UserRepository;
import com.springProject.project.io.entity.AddressEntity;
import com.springProject.project.io.entity.UserEntity;
import com.springProject.project.io.repositories.AddressRepository;
import com.springProject.project.service.AddressService;
import com.springProject.project.shared.dto.AddressDto;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AddressRepository addressRepository;

    @Override
    public List<AddressDto> getAddresses(String userId) {
        List<AddressDto> returnValue = new ArrayList<>();
        ModelMapper mapper = new ModelMapper();
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null)
            return returnValue;

        Iterable<AddressEntity> addresses = addressRepository.findAllByUserDetails(userEntity);
        for (AddressEntity addressEntity : addresses) {
            returnValue.add(mapper.map(addressEntity, AddressDto.class));
        }

        return returnValue;
    }

    @Override
    public AddressDto getAddress(String addressId) {
        AddressDto returnValue=new AddressDto();
        AddressEntity addressEntity=addressRepository.findByAddressId(addressId);
        if (addressEntity!=null)
        returnValue=new ModelMapper().map(addressEntity,AddressDto.class);
        return returnValue;
    }
}
