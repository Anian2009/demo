package com.example.demo.controller.profile;


import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UsersRepository;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.junit4.SpringRunner;

@Profile("adminControllerMockProfile")
@Configuration
public class ConfigAdminControllerMockTest {

    @Bean
    @Primary
    public UsersRepository findByToken(){
        return Mockito.mock(UsersRepository.class);
    }

    @Bean
    @Primary
    public FabricsRepository save(){return Mockito.mock(FabricsRepository.class);}
}
