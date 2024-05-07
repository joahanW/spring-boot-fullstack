package com.johan.service;

import com.johan.model.Gender;
import com.johan.repository.CustomerDao;
import com.johan.model.Customer;
import com.johan.model.CustomerRegistrationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class CustomerServiceJDBC {

    private final CustomerDao customerDao;

    public CustomerServiceJDBC(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = new Customer(
            customerRegistrationRequest.email(),
            customerRegistrationRequest.name(),
            customerRegistrationRequest.age(),
                Gender.MALE);
        customerDao.insertCustomer(customer);
    }

}
