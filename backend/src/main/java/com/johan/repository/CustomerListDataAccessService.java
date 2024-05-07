package com.johan.repository;

import com.johan.model.Customer;
import com.johan.model.Gender;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository("list")
public class CustomerListDataAccessService implements CustomerDao {

    //db
    private static List<Customer> customers;

    static {
        customers = new ArrayList<>();
        Customer alex = new Customer(
                1L,
                "Alex",
                "alex@gmail.com",
                21,
                Gender.MALE);
        customers.add(alex);

        Customer jamila = new Customer(
                2L,
                "Jamila",
                "jamila@gmail.com",
                25,
                Gender.MALE);
        customers.add(jamila);

    }

    @Override
    public List<Customer> selectAllCustomer() {
        return customers;
    }

    @Override
    public Optional<Customer> getByCustomerId(Integer customerId) {
        return customers.stream()
                .filter(c->c.getId().intValue() == customerId)
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        return false;
    }

    @Override
    public void deleteCustomer(Integer customerId) {

    }

    @Override
    public void updateCustomer(Customer customer) {

    }
}
