package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.json.JSONException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

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

    private Users userForTest;

    @Test
    public void loginAllIsOkExpectedOk() throws JSONException, IOException {

        Map<String, String> request = new HashMap<>();
        request.put("email", "SomeUser@some.net");
        request.put("password", "somePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in",
                HttpMethod.POST, new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToLogin.json"), response.getBody(), false);
    }

    @Test
    public void tryLoginWithoutActivationByCodeExpectedBadRequest() throws JSONException {

        userForTest.setActivationCode("The user has not passed the activation code");//Not activated

        Map<String, String> request = new HashMap<>();
        request.put("email", "SomeUser@some.net");
        request.put("password", "somePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in",
                HttpMethod.POST, new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"User has not completed registration." +
                " Check your email and follow the instructions.\"}", response.getBody(), false);
    }

    @Test
    public void tryLoginWithAnUnregisteredEmailExpectedNotFound() throws JSONException {

        Map<String, String> request = new HashMap<>();
        request.put("email", "UnregistredEmail@some.net");//This e-mail is not in the database
        request.put("password", "somePassword");

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"User with this email not found in DB.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryLoginWithWrongPasswordExpectedBadRequest() throws JSONException {

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userForTest);

        Map<String, String> request = new HashMap<>();
        request.put("email", "SomeUser@some.net");
        request.put("password", "someWrongPassword");//Typed wrong password

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Password is incorrect.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryLoginWithoutPasswordExpectedBadRequest() throws JSONException {

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(userForTest);

        Map<String, String> request = new HashMap<>();
        request.put("email", "SomeUser@some.net");
        //There are no password in the request

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user did not provide enough information to identify.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryLoginWithoutAnyDataExpectedBadRequest() throws JSONException {

        //There are no data in the request

        ResponseEntity<String> response = rest.exchange("/api/guest/log-in", HttpMethod.POST,
                new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Content type 'application/x-www-form-urlencoded;charset=UTF-8' " +
                "not supported\"}", response.getBody(), false);
    }

    @Test
    public void registrationEverythingIsOkExpectedOk() throws JSONException {

        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some.net");
        request.put("password", "SomePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"name\":\"" + request.get("name") + "\"}", response.getBody(), false);
    }

    @Test
    public void tryToRegistrationWithNotValidEmailExpectedBadRequest() throws JSONException {

        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some");//Invalid Email
        request.put("password", "SomePassword");

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"E-mail incorrectly written.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryToRegistrationWithEmailAlreadyExistExpectedBadRequest() throws JSONException {

        Map<String, String> request = new HashMap<>();
        request.put("name", "SomeName");
        request.put("email", "SomeUser@some.net");
        request.put("password", "SomePassword");

        Mockito.when(usersRepository.findByEmail("SomeUser@some.net")).thenReturn(new Users());//Email is already exists

        ResponseEntity<String> response = rest.exchange("/api/guest/registration", HttpMethod.POST,
                new HttpEntity<>(request, null), String.class);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an email already exists.\"}",
                response.getBody(), false);
    }

    @Test
    public void activationCodeEverythingIsOkExpectedOk() throws JSONException {

        userForTest.setActivationCode("SomeCode");

        Mockito.when(usersRepository.findByActivationCode("SomeCode")).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeCode", HttpMethod.PUT,
                new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
    }

    @Test
    public void tryActivationCodeWithWrongCodeExpectedNotFound() throws JSONException {

        userForTest.setActivationCode("SomeCode");

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeElseCode",//Wrong code
                HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an activation key was not found in the database.\"}",
                response.getBody(), false);


    }

    @Test
    public void tryToReactivateTheCodeExpectedNotFound() throws JSONException {

        userForTest.setActivationCode("true");

        ResponseEntity<String> response = rest.exchange("/activation-code/SomeCode",//This code is no longer valid
                HttpMethod.PUT, new HttpEntity<>(null), String.class);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"A user with such an activation key was not found in the database.\"}",
                response.getBody(), false);


    }
}