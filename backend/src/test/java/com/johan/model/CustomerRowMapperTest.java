package com.johan.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.sql.ResultSet;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

class CustomerRowMapperTest {

    private CustomerRowMapper underTest;

    @Test
    void mapRow() throws SQLException {
        // GIVEN
        CustomerRowMapper customerRowMapper = new CustomerRowMapper();
        ResultSet resultSet = Mockito.mock(ResultSet.class);
        Mockito.when(resultSet.getLong("id")).thenReturn(1L);
        Mockito.when(resultSet.getInt("age")).thenReturn(20);
        Mockito.when(resultSet.getString("name")).thenReturn("Joko");
        Mockito.when(resultSet.getString("email")).thenReturn("joko@gmail.com");
        Mockito.when(resultSet.getString("gender")).thenReturn("MALE");

        // When
        Customer actual = customerRowMapper.mapRow(resultSet, 1);

        // Then
        Customer expected = new Customer(
                1L,"Joko","joko@gmail.com",20,
                Gender.MALE);
        assertThat(actual).isEqualTo(expected);
    }
}