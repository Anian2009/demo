package com.example.demo.controller.profile;


import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("registrationControllerMockProfile")
@Configuration
public class ConfigRegistrationControllerMockTest {

    @Bean
    @Primary
    public UsersRepository findByEmail(){
        return Mockito.mock(UsersRepository.class);
    }

    @Bean
    @Primary
    public FabricsRepository findByAll(){
        return Mockito.mock(FabricsRepository.class);
    }

    @Bean
    @Primary
    public MailSender send(){return Mockito.mock(MailSender.class);}

}
