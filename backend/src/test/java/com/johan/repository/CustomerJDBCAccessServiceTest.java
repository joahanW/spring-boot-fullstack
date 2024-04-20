package com.johan.repository;

import com.johan.AbstractTestContainers;
import com.johan.model.Customer;
import com.johan.model.CustomerRowMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerJDBCAccessServiceTest extends AbstractTestContainers {

    private final CustomerRowMapper customerRowMapper = new CustomerRowMapper();
    private CustomerJDBCAccessService underTest;

    @BeforeEach
    void setUp() {
        underTest = new CustomerJDBCAccessService(
                getJdbcTemplate(),
                customerRowMapper
        );
    }

    @Test
    void selectAllCustomer() {
        // GIVEN
        Customer customer = new Customer(
                FAKER.name().firstName(),
                FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID(),
                20
        );
        underTest.insertCustomer(customer);

        // When
        List<Customer> actual = underTest.selectAllCustomer();

        // Then
        System.out.println(actual.get(0).getId());
        assertThat(actual).isNotEmpty();

    }

    @Test
    void getByCustomerId() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().firstName(),
                email,
                20
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer()
                .stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Optional<Customer> actual = underTest.getByCustomerId(id.intValue());

        // Then
        assertThat(actual).isPresent()
                .hasValueSatisfying(c -> {
                    assertThat(c.getId()).isEqualTo(id);
                    assertThat(c.getName()).isEqualTo(customer.getName());
                    assertThat(c.getEmail()).isEqualTo(customer.getEmail());
                    assertThat(c.getAge()).isEqualTo(customer.getAge());
                });
    }

    @Test
    void willReturnEmptyWhenCustomerNotFound() {
        // Given
        int id = -1;

        // When
        Optional<Customer> actual = underTest.getByCustomerId(id);

        // Then
        assertThat(actual).isEmpty();
    }

    @Test
    void existsPersonWithEmail() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        underTest.insertCustomer(customer);

        // When
        boolean b = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(b).isTrue();

    }

    @Test
    void existsPersonWithEmailReturnsFalseWhenDoesNotExists() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();

        // When
        boolean actual = underTest.existsPersonWithEmail(email);

        // Then
        assertThat(actual).isFalse();
    }

    @Test
    void insertCustomer() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        underTest.insertCustomer(customer);

        // When
        Customer actual = underTest.selectAllCustomer()
                .stream().filter(c -> c.getEmail().equals(email))
                .findFirst()
                .orElseThrow();

        // Then
        assertThat(actual).isNotNull();
    }


    @Test
    void deleteCustomer() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        underTest.deleteCustomer(id.intValue());

        // Then
        Optional<Customer> actual = underTest.getByCustomerId(id.intValue());
        assertThat(actual).isNotPresent();

    }

    @Test
    void updateCustomer() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Customer update = new Customer(
                "John Doe",
                "johndoe@gmail.com",
                88
        );
        update.setId(id);
        underTest.updateCustomer(update);

        // Then
        Optional<Customer> actual = underTest.getByCustomerId(id.intValue());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(update.getName());
            assertThat(c.getEmail()).isEqualTo(update.getEmail());
            assertThat(c.getAge()).isEqualTo(update.getAge());
        });

    }

    @Test
    void willNotUpdateWhenNothingToUpdate() {
        // GIVEN
        String email = FAKER.internet().safeEmailAddress() + "-" + UUID.randomUUID();
        Customer customer = new Customer(
                FAKER.name().fullName(),
                email,
                25
        );
        underTest.insertCustomer(customer);

        Long id = underTest.selectAllCustomer().stream()
                .filter(c -> c.getEmail().equals(email))
                .map(Customer::getId)
                .findFirst()
                .orElseThrow();

        // When
        Customer update = new Customer();
        update.setId(id);

        // Then
        Optional<Customer> actual = underTest.getByCustomerId(id.intValue());
        assertThat(actual).isPresent().hasValueSatisfying(c -> {
            assertThat(c.getId()).isEqualTo(id);
            assertThat(c.getName()).isEqualTo(customer.getName());
            assertThat(c.getEmail()).isEqualTo(customer.getEmail());
            assertThat(c.getAge()).isEqualTo(customer.getAge());
        });

    }
}