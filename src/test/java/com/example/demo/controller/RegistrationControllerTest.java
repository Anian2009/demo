package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ActiveProfiles("registrationControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegistrationControllerTest extends ReadFromFile {

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.price}")
    private Integer price;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private MailSender mailSender;

    @Before
    public void setUp() {
        Mockito.reset(usersRepository);
        userForTest = new Users("SomeUser", "SomeUser@some.net", "USER",
                new BCryptPasswordEncoder(4).encode("somePassword"), "user-token");
        userForTest.setActivationCode("true");
    }

    @After
    public void check() {
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(mailSender);
    }

    private Users userForTest;

    @Test
    public void login() throws JSONException, IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("SomeUser@some.net","somePassword");

        Mockito.when(usersRepository.findByEmail(anyString())).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in",
                HttpMethod.POST, new HttpEntity<>(headers), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src/test/resources/responseToLogin.json"), response.getBody(), false);

        verify(usersRepository).findByEmail("SomeUser@some.net");
        verifyNoMoreInteractions(usersRepository);
    }

    @Test
    public void registration() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some.net");
        request.put("password", "SomePassword");

        Mockito.when(usersRepository.findByEmail(anyString())).thenReturn(null);
        Mockito.when(mailSender.send(anyString(),anyString(),anyString())).thenReturn("send");
        Mockito.when(usersRepository.save(any())).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A message was sent to your email with further instructions\"}",
                response.getBody(), false);

        verify(usersRepository).findByEmail("SomeUser@some.net");
        verify(mailSender).send(eq("SomeUser@some.net"),eq("Activation code"),anyString());
        verify(usersRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(mailSender);
    }

    @Test
    public void tryToRegistrationWithNotValidEmail() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some");//Invalid Email
        request.put("password", "SomePassword");

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"E-mail incorrectly written.\"}",
                response.getBody(), false);

        verifyZeroInteractions(usersRepository);
        verifyZeroInteractions(mailSender);
    }

    @Test
    public void tryToRegistrationWithEmailAlreadyExist() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some.net");
        request.put("password", "SomePassword");

        Mockito.when(usersRepository.findByEmail(anyString())).thenReturn(userForTest);//Email is already exists

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an email already exists.\"}",
                response.getBody(), false);

        verify(usersRepository).findByEmail("SomeUser@some.net");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(mailSender);
    }

    @Test
    public void activateCode() throws JSONException {
        userForTest.setActivationCode("SomeCode");
        userForTest.setPassword("somePassword");

        Mockito.when(usersRepository.findByActivationCode(anyString())).thenReturn(userForTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeCode", HttpMethod.PUT,
                new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Activation completed successfully. " +
                        "You can enter the game using your email address and password\"}", response.getBody(), false);

        verify(usersRepository).findByActivationCode("SomeCode");
        verify(usersRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
    }

    @Test
    public void tryActivateCodeWithWrongCode() throws JSONException {
        userForTest.setActivationCode("SomeCode");

        Mockito.when(usersRepository.findByActivationCode(anyString())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeElseCode",//Wrong code
                HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an activation key was not found in the database.\"}",
                response.getBody(), false);

        verify(usersRepository).findByActivationCode("SomeElseCode");
        verifyNoMoreInteractions(usersRepository);
    }

    @Test
    public void tryToReactivateTheCode() throws JSONException {
        userForTest.setActivationCode("true");

        Mockito.when(usersRepository.findByActivationCode(anyString())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeCode",//This code is no longer valid
                HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an activation key was not found in the database.\"}",
                response.getBody(), false);

        verify(usersRepository).findByActivationCode("SomeCode");
        verifyNoMoreInteractions(usersRepository);
    }
}