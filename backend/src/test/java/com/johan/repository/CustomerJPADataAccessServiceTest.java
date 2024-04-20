package com.johan.repository;

import com.johan.model.Customer;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

class CustomerJPADataAccessServiceTest {

    private CustomerJPADataAccessService underTest;
    private AutoCloseable autoCloseable;

    @Mock
    private CustomerRepository customerRepository;

    @BeforeEach
    void setUp() {
        autoCloseable = MockitoAnnotations.openMocks(this);
        underTest = new CustomerJPADataAccessService(customerRepository);
    }

    @AfterEach
    void tearDown() throws Exception {
        autoCloseable.close();
    }

    @Test
    void selectAllCustomer() {
        // When
        underTest.selectAllCustomer();

        // Then
        Mockito.verify(customerRepository)
                .findAll();
    }

    @Test
    void getByCustomerId() {
        // GIVEN
        int id = 1;

        // When
        underTest.getByCustomerId(id);

        // Then
        Mockito.verify(customerRepository)
                .findById(id);

    }

    @Test
    void insertCustomer() {
        // GIVEN
        Customer customer = new Customer(
                1L, "johan", "johan@gmail.com", 2
        );

        // When
        underTest.insertCustomer(customer);

        // Then
        Mockito.verify(customerRepository)
                .save(customer);
    }

    @Test
    void existsPersonWithEmail() {
        // GIVEN
        String email = "johan@gmail.com";

        // When
        underTest.existsPersonWithEmail(email);

        // Then
        Mockito.verify(customerRepository)
                .existsCustomerByEmail(email);
    }

    @Test
    void deleteCustomer() {
        // GIVEN
        int id = 1;

        // When
        underTest.deleteCustomer(id);

        // Then
        Mockito.verify(customerRepository)
                .deleteById(id);
    }

    @Test
    void updateCustomer() {
        // GIVEN
        Customer customer = new Customer(
                1L, "johan", "johan@gmail.com", 2
        );

        // When
        underTest.updateCustomer(customer);

        // Then
        Mockito.verify(customerRepository)
                .save(customer);
    }
}