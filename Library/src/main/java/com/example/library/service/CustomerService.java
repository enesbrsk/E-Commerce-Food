package com.example.library.service;

import com.example.library.dto.CustomerDto;
import com.example.library.model.Customer;

public interface CustomerService {

    Customer save(CustomerDto customerDto);
    Customer findByUsername(String username);
    CustomerDto getCustomer(String username);
    Customer changePass(CustomerDto customerDto);
}
