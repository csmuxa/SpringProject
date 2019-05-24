package com.springProject.project.service;

import com.springProject.project.shared.dto.AddressDto;

import java.util.List;

public interface AddressService {
    List<AddressDto> getAddresses(String userId);

    AddressDto getAddress(String addressId);

}
