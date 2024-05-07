package com.johan.service;

import com.johan.exception.DuplicateResourceException;
import com.johan.exception.RequestValidationException;
import com.johan.exception.ResourceNotFound;
import com.johan.model.Customer;
import com.johan.model.CustomerRegistrationRequest;
import com.johan.model.Gender;
import com.johan.repository.CustomerDao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerDao customerDao;
    private CustomerService underTest;

    @BeforeEach
    void setUp() {

        underTest = new CustomerService(customerDao);
    }

    @Test
    void getAllCustomers() {
        // When
        underTest.getAllCustomers();

        // Then
        Mockito.verify(customerDao)
                .selectAllCustomer();

    }

    @Test
    void canGetCustomer() {
        // GIVEN
        Long id = 1L;
        Customer customer = new Customer(
                id, "Johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id.intValue()))
                .thenReturn(Optional.of(customer));

        // When
        Customer actual = underTest.getCustomer(id.intValue());

        // Then
        assertThat(actual).isEqualTo(customer);

    }

    @Test
    void willThrowWhenGetCustomerReturnsEmptyOptional() {
        // GIVEN
        Long id = 1L;
        Customer customer = new Customer(
                id, "Johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id.intValue()))
                .thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.getCustomer(id.intValue()))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage(
                        "Customer with id [%s] not found".formatted(id)
                );
    }

    @Test
    void addCustomer() {
        // GIVEN
        String email = "johan@gmail.com";
        Mockito.when(customerDao.existsPersonWithEmail(email))
                .thenReturn(false);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "johan", email, 22, Gender.MALE
        );

        // When
        underTest.addCustomer(request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);
        Mockito.verify(customerDao).insertCustomer(customerArgumentCaptor.capture());

        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getEmail()).isNotNull();
        assertThat(captureCustomer.getName()).isEqualTo(request.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(request.email());
        assertThat(captureCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenEmailExistsWhileAddCustomer() {
        // GIVEN
        String email = "johan@gmail.com";
        Mockito.when(customerDao.existsPersonWithEmail(email))
                .thenReturn(true);

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "johan", email, 22, Gender.MALE
        );

        // When
        assertThatThrownBy(()-> underTest.addCustomer(request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        // Then
        Mockito.verify(customerDao, Mockito.never()).insertCustomer(Mockito.any());
    }

    @Test
    void deleteCustomer() {
        // GIVEN
        int id = 1;
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(new Customer(
                        1L, "Johan", "johan@gmail.com",20,
                        Gender.MALE)));

        // When
        underTest.deleteCustomer(id);

        // Then
        Mockito.verify(customerDao)
                .deleteCustomer(id);
    }

    @Test
    void willThrowDeleteCustomerWhenIdDoesNotExist() {
        // GIVEN
        int id = 1;
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.empty());

        // When
        // Then
        assertThatThrownBy(() -> underTest.deleteCustomer(id))
                .isInstanceOf(ResourceNotFound.class)
                .hasMessage("Customer with id [%s] not found".formatted(id));
    }

    @Test
    void canUpdateAllCustomerProperties() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Joko", "joko@gmail.com", 22, Gender.MALE
        );

        Mockito.when(customerDao.existsPersonWithEmail(request.email()))
                .thenReturn(false);

        // When
        underTest.updateCustomer(id,request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(request.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(request.email());
        assertThat(captureCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void canUpdateOnlyCustomerName() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                "Joko", null, null, null
        );

        // When
        underTest.updateCustomer(id,request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(request.name());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerEmail() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "jow@gmail.com";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                null, newEmail, null, null
        );

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(false);

        // When
        underTest.updateCustomer(id,request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(newEmail);
        assertThat(captureCustomer.getAge()).isEqualTo(customer.getAge());

    }

    @Test
    void canUpdateOnlyCustomerAge() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                null, null, 100, null
        );

        // When
        underTest.updateCustomer(id,request);

        // Then
        ArgumentCaptor<Customer> customerArgumentCaptor = ArgumentCaptor.forClass(Customer.class);

        Mockito.verify(customerDao).updateCustomer(customerArgumentCaptor.capture());
        Customer captureCustomer = customerArgumentCaptor.getValue();

        assertThat(captureCustomer.getName()).isEqualTo(customer.getName());
        assertThat(captureCustomer.getEmail()).isEqualTo(customer.getEmail());
        assertThat(captureCustomer.getAge()).isEqualTo(request.age());

    }

    @Test
    void willThrowWhenTryingToUpdateSameCustomerEmail() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        String newEmail = "jow@gmail.com";

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                null, newEmail, null, null
        );

        Mockito.when(customerDao.existsPersonWithEmail(newEmail)).thenReturn(true);

        // When
        assertThatThrownBy(()-> underTest.updateCustomer(id,request))
                .isInstanceOf(DuplicateResourceException.class)
                .hasMessage("Email already taken");

        // Then
        Mockito.verify(customerDao, Mockito.never()).updateCustomer(Mockito.any());

    }

    @Test
    void willThrowWhenCustomerUpdateHasNoChanges() {
        // GIVEN
        int id = 1;
        Customer customer = new Customer(
                "johan", "johan@gmail.com",20,
                Gender.MALE);
        Mockito.when(customerDao.getByCustomerId(id))
                .thenReturn(Optional.of(customer));

        CustomerRegistrationRequest request = new CustomerRegistrationRequest(
                customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender()
        );

        // When
        assertThatThrownBy(()-> underTest.updateCustomer(id,request))
                .isInstanceOf(RequestValidationException.class)
                .hasMessage("No data changes found");

        // Then
        Mockito.verify(customerDao,Mockito.never()).updateCustomer(Mockito.any());

    }
}