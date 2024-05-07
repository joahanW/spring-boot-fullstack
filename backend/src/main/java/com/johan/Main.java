package com.johan;

import com.github.javafaker.Faker;
import com.johan.model.Customer;
import com.johan.repository.CustomerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Random;

@SpringBootApplication
public class Main {

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
//        String[] beanDefinitionNames = run.getBeanDefinitionNames();
//        for (String beanDefinitionName : beanDefinitionNames) {
//            System.out.println(beanDefinitionName);
//        }
    }

    @Bean
    CommandLineRunner runner(CustomerRepository customerRepository){
        return args -> {
            Faker faker = new Faker();
            Random random = new Random();
            String firstname = faker.name().firstName();
            String lastname = faker.name().lastName();

            Customer customer = new Customer(
                    firstname+ " " + lastname,
                    firstname.toLowerCase() + "." + lastname.toLowerCase()+"@johan.com",
                    random.nextInt(16,99)
            );
            customerRepository.save(customer);
        };
    }

}
