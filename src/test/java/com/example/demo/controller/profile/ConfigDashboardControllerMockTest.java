package com.example.demo.controller.profile;


import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.stripe.model.Charge;
import org.mockito.Mockito;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

@Profile("dashboardControllerMockProfile")
@Configuration
public class ConfigDashboardControllerMockTest {

    @Bean
    @Primary
    public UsersRepository findByToken(){
        return Mockito.mock(UsersRepository.class);
    }

    @Bean
    @Primary
    public FabricsRepository save(){return Mockito.mock(FabricsRepository.class);}

    @Bean
    @Primary
    public UserFabricsRepository findByMaster() {return Mockito.mock(UserFabricsRepository.class);}

}
