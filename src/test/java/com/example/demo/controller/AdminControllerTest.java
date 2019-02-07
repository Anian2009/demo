package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.domain.Fabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import junit.framework.Assert;
import org.json.JSONException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ActiveProfiles("adminControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AdminControllerTest extends ReadFromFile {

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FabricsRepository fabricsRepository;

    @Before
    public void setUp() {
        Mockito.reset(usersRepository);
        Mockito.reset(fabricsRepository);

        userForTest = new Users("adminUser", "adminUser@some.net", "ADMIN",
                new BCryptPasswordEncoder(4).encode("somePassword"), "admin-token");
        userForTest.setActivationCode("true");

        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "admin-token");

    }

    @After
    public void check() {
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
    }

    private Users userForTest;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void getFactoryMarketList() throws IOException, JSONException {
        List<Fabrics> fabrics = Arrays.asList(
                new Fabrics(1.0, "firstFabric", 3.0, 0.00001, "image-1"),
                new Fabrics(5.0, "secondFabric", 15.0, 0.00006, "image-2"),
                new Fabrics(10.0, "threadFabric", 30.0, 0.00015, "image-3"),
                new Fabrics(50.0, "forthFabric", 150.0, 0.0008, "image-4")
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userForTest);
        Mockito.when(fabricsRepository.findAll()).thenReturn(fabrics);

        ResponseEntity<String> response = rest.exchange("/api/admin/factory-list", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src/test/resources/responseToFactoryMarketList.json"), response.getBody(), false);

        verify(usersRepository).findByToken("admin-token");
        verify(fabricsRepository).findAll();
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
    }

    @Test
    public void createNewFactory() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("newPrice", "1.0");
        request.put("newName", "SomeName");
        request.put("newUpgrade", "1.0");
        request.put("newMining", "1.0");
        request.put("image", "image");

        Fabrics fabric = new Fabrics(1.0, "SomeName", 1.0, 1.0, "image");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userForTest);
        Mockito.when(fabricsRepository.save(any())).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":{\"id\":null,\"price\":1.0,\"fabricName\":\"SomeName\"," +
                "\"upgrade\":1.0,\"miningPerSecond\":1.0,\"img\":\"image\"}}", response.getBody(), false);

        verify(usersRepository).findByToken("admin-token");
        verify(fabricsRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
    }

    @Test
    public void tryCreatedNewFactoryWithoutData() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("newPrice", "1");
        request.put("newName", "SomeName");
        request.put("newUpgrade", null);//NullPointerException generated
        request.put("newMining", "1");
        request.put("image", "image");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Lack of transmitted data to create an object.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("admin-token");
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
    }
}