package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.controller.profile.TakeInputDataForTest;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.example.demo.security.Pac4jConfig;
import junit.framework.Assert;
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
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ActiveProfiles("dashboardControllerMockProfile")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DashboardControllerTest extends ReadFromFile {

    @Value("${stripe.apiKey}")
    private String myApiKey;

    @Value("${stripe.price}")
    private Integer price;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${stripe.description}")
    private String description;

    @Autowired
    private TestRestTemplate rest;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private FabricsRepository fabricsRepository;

    @Autowired
    private UserFabricsRepository userFabricsRepository;

    @Before
    public void setUp() {

        Mockito.reset(usersRepository);
        Mockito.reset(fabricsRepository);
        Mockito.reset(userFabricsRepository);

        userFoTest = new Users("SomeUser","User@some.net","USER","somePassword","user-token");

        headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"user-token");
    }

    private Users userFoTest;
    private HttpHeaders headers;

    @Test
    public void myFabricEverythingOkExpectedOk() throws IOException, JSONException {

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findByMaster(userFoTest)).thenReturn(TakeInputDataForTest.userFabricList());
        Mockito.when(usersRepository.findAll()).thenReturn(TakeInputDataForTest.usersList());

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=7", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToMyFabricList.json"), response.getBody(), false);
    }

    @Test
    public void tryMyFabricWrongTokenExpectedUnauthorized() {

        userFoTest.setToken("wrong-token");

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=10", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void factoryMarketListEverythingOkExpectedOk() throws IOException, JSONException {

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findAll()).thenReturn(TakeInputDataForTest.fabricList());

        ResponseEntity<String> response = rest.exchange("/api/user/factory-market", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToFactoryMarketList.json"), response.getBody(), false);

    }

    @Test
    public void buyFabricAllOkExpectedOk() throws JSONException {

        userFoTest.setSilverBalance(10.0);

        Map<String,String> request = new HashMap<>();
        request.put("id","1");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(TakeInputDataForTest.getOneFabric());

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"OK\"}", response.getBody(), false);
    }

    @Test
    public void tryBuyFabricLuckOfMoneyExpectedBadRequest() throws JSONException {

        userFoTest.setSilverBalance(0.0);

        Map<String,String> request = new HashMap<>();
        request.put("id","1");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(1)).thenReturn(TakeInputDataForTest.getOneFabric());

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user lacks money.\"}", response.getBody(), false);
    }


    @Test
    public void tryBuyFabricWithWrongIdExpectedNotFound() throws JSONException {

        Map<String,String> request = new HashMap<>();
        request.put("id","5");

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());

    }

    @Test
    public void tryUpgradeFactoryAllOkExpectedOk() throws JSONException, IOException {

        userFoTest.setSilverBalance(10.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(2)).thenReturn(TakeInputDataForTest.getUserFabric(userFoTest));
        Mockito.when(userFabricsRepository.findByMaster(userFoTest)).thenReturn(TakeInputDataForTest.userFabricList());

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToUpgradeFabric.json"), response.getBody(), false);
    }

    @Test
    public void tryUpgradeFactoryLackOfMoneyExpectedBadRequest() throws JSONException {

        userFoTest.setSilverBalance(1.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(2)).thenReturn(TakeInputDataForTest.getUserFabric(userFoTest));

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have enough money to complete the operation.\"}",
                response.getBody(), false);

    }

    @Test
    public void tryUpgradeFactoryWithWrongFabricIdExpectedBadRequest() throws JSONException {

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/10", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Such a user does not exist in the database.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryExchangeGoldToSilverAllOkExpectedOk() {

        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=100&myGoldCoins=-1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void tryExchangeSilverToGoldAllOkExpectedOk() {

        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-200&myGoldCoins=1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());

    }

    @Test
    public void tryExchangeGoldToSilverChiterExpectedBadRequest() throws JSONException {

        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=500&myGoldCoins=-5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have the amount specified in the account.\"}",
                response.getBody(), false);


    }

    @Test
    public void tryExchangeSilverToGoldChiterExpectedBadRequest() {

        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-1000&myGoldCoins=5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());

    }

}