package com.seng302.wasteless.service;

import com.seng302.wasteless.model.Address;
import com.seng302.wasteless.model.Business;
import com.seng302.wasteless.repository.AddressRepository;
import com.seng302.wasteless.repository.BusinessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AddressService {

    private final AddressRepository addressRepository;

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address createAddress(Address address) {
        return addressRepository.save(address);
    }
}
