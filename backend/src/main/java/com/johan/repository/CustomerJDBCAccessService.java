package com.johan.repository;

import com.johan.model.Customer;
import com.johan.model.CustomerRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository("jdbc")
public class CustomerJDBCAccessService implements CustomerDao{

    private final JdbcTemplate jdbcTemplate;
    private final CustomerRowMapper customerRowMapper;


    public CustomerJDBCAccessService(JdbcTemplate jdbcTemplate, CustomerRowMapper customerRowMapper) {
        this.jdbcTemplate = jdbcTemplate;
        this.customerRowMapper = customerRowMapper;
    }

    @Override
    public List<Customer> selectAllCustomer() {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                """;
        return jdbcTemplate.query(sql,customerRowMapper);
    }

    @Override
    public Optional<Customer> getByCustomerId(Integer customerId) {
        var sql = """
                SELECT id, name, email, age, gender
                FROM customer
                WHERE id = ?
                """;
        return jdbcTemplate.query(sql,customerRowMapper, customerId)
                .stream()
                .findFirst();
    }

    @Override
    public void insertCustomer(Customer customer) {
        var sql = """
                INSERT INTO customer (name,email,age,gender)
                VALUES (?,?,?,?)
                """;
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender().name());
        System.out.println("jdbcTemplate.update = " +update);
    }

    @Override
    public boolean existsPersonWithEmail(String email) {
        var sql = """
                SELECT count(id)
                FROM customer
                WHERE email = ?
                """;
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, email);
        return count != null && count > 0;
    }

    @Override
    public void deleteCustomer(Integer customerId) {
        var sql = """
                DELETE FROM customer
                WHERE id = ?
                """;
        int delete = jdbcTemplate.update(sql, customerId);
        System.out.println("Delete Customer result = "+ delete);
    }

    @Override
    public void updateCustomer(Customer customer) {
        var sql = """
                UPDATE customer
                SET name=?, email=?, age=?, gender=?
                WHERE id = ?
                """;
        int update = jdbcTemplate.update(sql, customer.getName(), customer.getEmail(), customer.getAge(), customer.getGender().name(), customer.getId());
        System.out.println("Update Customer result = "+update);
    }
}
