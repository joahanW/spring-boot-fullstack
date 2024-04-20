package com.johan.repository;

import com.johan.model.Customer;

import java.util.List;
import java.util.Optional;

public interface CustomerDao {
    List<Customer> selectAllCustomer();
    Optional<Customer> getByCustomerId(Integer customerId);
    void insertCustomer(Customer customer);
    boolean existsPersonWithEmail(String email);
    void deleteCustomer(Integer customerId);
    void updateCustomer(Customer customer);
}
