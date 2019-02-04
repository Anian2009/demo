package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.controller.profile.TakeInputDataForTest;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import com.fasterxml.jackson.core.JsonProcessingException;
import junit.framework.Assert;
import org.json.JSONException;
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
import java.util.HashMap;
import java.util.Map;

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

        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"admin-token");

    }

    private Users userForTest;

    private HttpHeaders headers = new HttpHeaders();

    @Test
    public void factoryMarketListAllIsOkExpectedOk() throws IOException, JSONException {

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(userForTest);
        Mockito.when(fabricsRepository.findAll()).thenReturn(TakeInputDataForTest.fabricList());

        ResponseEntity<String> response = rest.exchange("/api/admin/factory-list", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToFactoryMarketList.txt"), response.getBody(), false);
    }

    @Test
    public void tryAddFabricEverythingIsOkExpectedOk() throws JSONException, JsonProcessingException {

        Map<String,String> request = new HashMap<>();
        request.put("newPrice","1");
        request.put("newName","SomeName");
        request.put("newUpgrade","1");
        request.put("newMining","1");
        request.put("image","../immage/fab_none-13.jpg");


//        ObjectMapper mapperObj = new ObjectMapper();
//        String jsonStr = mapperObj.writeValueAsString(request);
//        System.out.println(jsonStr);

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(userForTest);

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
    }

    @Test
    public void tryAddFabricWithoutDataExpectedBadRequest() throws JSONException {

        Mockito.when(usersRepository.findByToken("admin-token")).thenReturn(userForTest);

        Map<String,String> request = new HashMap<>();
        request.put("newPrice","1");
        request.put("newName","SomeName");
        request.put("newUpgrade",null);//NullPointerException generated
        request.put("newMining","1");
        request.put("image","../immage/fab_none-13.jpg");

        ResponseEntity<String> response = rest.exchange("/api/admin/add-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Lack of transmitted data to create an object.\"}", response.getBody(), false);
    }
}