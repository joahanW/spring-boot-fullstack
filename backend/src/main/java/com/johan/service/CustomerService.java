package com.johan.service;

import com.johan.repository.CustomerDao;
import com.johan.exception.DuplicateResourceException;
import com.johan.exception.RequestValidationException;
import com.johan.exception.ResourceNotFound;
import com.johan.model.Customer;
import com.johan.model.CustomerRegistrationRequest;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerDao customerDao;

public CustomerService(@Qualifier("jdbc") CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public List<Customer> getAllCustomers() {
        return customerDao.selectAllCustomer();
    }

    public Customer getCustomer(int id) {
        return customerDao.getByCustomerId(id)
                .orElseThrow(()-> new ResourceNotFound("Customer with id [%s] not found".formatted(id)));
    }

    public void addCustomer(CustomerRegistrationRequest customerRegistrationRequest){
        // Check if email
        if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())){
            throw new DuplicateResourceException("Email already taken");
        }
        // add
        Customer customer = new Customer(
                customerRegistrationRequest.name(),
                customerRegistrationRequest.email(),
                customerRegistrationRequest.age()
        );
        customerDao.insertCustomer(customer);
    }

    public void deleteCustomer(int customerId){
        getCustomer(customerId);
        customerDao.deleteCustomer(customerId);
    }

    public void updateCustomer(int id, CustomerRegistrationRequest customerRegistrationRequest){
        Customer customer = getCustomer(id);
        boolean change = false;

        if(customerRegistrationRequest.name() != null && !customer.getName().equals(customerRegistrationRequest.name())){
            customer.setName(customerRegistrationRequest.name());
            change=true;
        }

        if(customerRegistrationRequest.age() != null && !customer.getAge().equals(customerRegistrationRequest.age())){
            customer.setAge(customerRegistrationRequest.age());
            change=true;
        }

        if(customerRegistrationRequest.email() != null && !customer.getEmail().equals(customerRegistrationRequest.email())){
            if (customerDao.existsPersonWithEmail(customerRegistrationRequest.email())){
                throw new DuplicateResourceException("Email already taken");
            }
            customer.setEmail(customerRegistrationRequest.email());
            change=true;
        }

        if(!change){
            throw new RequestValidationException("No data changes found");
        }
        customerDao.updateCustomer(customer);
    }

}
