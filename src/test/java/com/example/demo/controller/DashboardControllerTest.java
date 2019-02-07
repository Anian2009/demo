package com.example.demo.controller;

import com.example.demo.controller.profile.ReadFromFile;
import com.example.demo.domain.Fabrics;
import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

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

        userFoTest = new Users("SomeUser", "User@some.net", "USER", "somePassword", "user-token");

        headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME, "user-token");
    }

    @After
    public void check() {
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
        verifyNoMoreInteractions(userFabricsRepository);
    }

    private Users userFoTest;
    private HttpHeaders headers;

    @Test
    public void getInformationForMainPage() throws IOException, JSONException {
        userFoTest.setId(4);

        List<Users> usersListFoTest = new ArrayList<>();
        Users u1 = new Users("firstUser", "firstUser@some.net", "USER", "somePassword-1", "someToken-1");
        Users u2 = new Users("secondUser", "secondUser@some.net", "USER", "somePassword-2", "someToken-2");
        Users u3 = new Users("threadUser", "threadUser@some.net", "USER", "somePassword-3", "someToken-3");
        u1.setId(1);
        u2.setId(2);
        u3.setId(3);
        usersListFoTest.add(u1);
        usersListFoTest.add(u2);
        usersListFoTest.add(u3);
        usersListFoTest.add(userFoTest);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.findAll()).thenReturn(usersListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src\\test\\resources\\responseDashboardInfo.json"), response.getBody(), false);

        verify(usersRepository).findAll();
        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(userFabricsRepository);
    }

    @Test
    public void getListOfMyFabric() throws IOException, JSONException {
        userFoTest.setId(4);

        List<UserFabrics> usersFabricListFoTest = new ArrayList<>();
        UserFabrics uf1 = new UserFabrics(userFoTest.getId(), 1, 1, 0.00001, "First", "img-1", 3.0);
        UserFabrics uf2 = new UserFabrics(userFoTest.getId(), 2, 1, 0.00006, "Second", "img-2", 15.0);
        usersFabricListFoTest.add(uf1);
        usersFabricListFoTest.add(uf2);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findByMaster(any())).thenReturn(usersFabricListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/myFabric", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src\\test\\resources\\responseMyFabricList.json"), response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verify(userFabricsRepository).findByMaster(userFoTest.getId());
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(userFabricsRepository);
    }

    @Test
    public void tryGetInformationForMainPageWithWrongToken() {
        userFoTest.setToken("wrong-token");

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=10", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(userFabricsRepository);
    }

    @Test
    public void getListOfFactories() throws IOException, JSONException {
        List<Fabrics> listOfFabrics = Arrays.asList(
                new Fabrics(1.0, "firstFabric", 3.0, 0.00001, "image-1"),
                new Fabrics(5.0, "secondFabric", 15.0, 0.00006, "image-2"),
                new Fabrics(10.0, "threadFabric", 30.0, 0.00015, "image-3"),
                new Fabrics(50.0, "forthFabric", 150.0, 0.0008, "image-4")
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findAll()).thenReturn(listOfFabrics);

        ResponseEntity<String> response = rest.exchange("/api/user/factory-market", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src\\test\\resources\\responseToFactoryMarketList.json"), response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository).findAll();
        verifyNoMoreInteractions(usersRepository);

    }

    @Test
    public void buyFabric() throws JSONException {
        userFoTest.setSilverBalance(10.0);

        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);
        UserFabrics userFabric = new UserFabrics(userFoTest.getId(), fabric.getId(), fabric.getMiningPerSecond(),
                "First", "img-1", 3.0);

        Map<String, String> request = new HashMap<>();
        request.put("id", "1");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(fabric);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Congratulations! You have become the owner of a new plant. " +
                "Information about your factories is on the main page.\"}", response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verify(usersRepository).save(any());
        verify(fabricsRepository).findById(1);
        verify(userFabricsRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyNoMoreInteractions(fabricsRepository);
        verifyNoMoreInteractions(userFabricsRepository);
    }

    @Test
    public void tryBuyFabricWithLuckOfMoney() throws JSONException {
        userFoTest.setSilverBalance(0.0);

        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String, String> request = new HashMap<>();
        request.put("id", "1");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user lacks money.\"}", response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository).findById(1);
        verifyNoMoreInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
    }


    @Test
    public void tryBuyFabricWithWrongId() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("id", "5");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request, headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Such a plant does not exist in the database.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository).findById(5);
        verifyNoMoreInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
    }

    @Test
    public void UpgradeFactory() throws JSONException, IOException {
        userFoTest.setId(5);
        userFoTest.setSilverBalance(10.0);

        List<UserFabrics> usersFabricListFoTest = new ArrayList<>();
        UserFabrics uf1 = new UserFabrics(userFoTest.getId(), 1, 1, 0.00001, "First", "img-1", 3.0);
        UserFabrics uf2 = new UserFabrics(userFoTest.getId(), 2, 1, 0.00006, "Second", "img-2", 15.0);
        uf1.setId(1);
        uf2.setId(2);
        usersFabricListFoTest.add(uf1);
        usersFabricListFoTest.add(uf2);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(uf1);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(uf2);
        Mockito.when(userFabricsRepository.findByMaster(any())).thenReturn(usersFabricListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("src\\test\\resources\\responseToUpgradeFabric.json"), response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verify(usersRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository).findById(2);
        verify(userFabricsRepository).save(any());
        verify(userFabricsRepository).findByMaster(userFoTest.getId());
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);
    }

    @Test
    public void tryUpgradeFactoryWithLackOfMoney() throws JSONException {
        userFoTest.setSilverBalance(1.0);

        List<UserFabrics> usersFabricListFoTest = new ArrayList<>();
        UserFabrics uf1 = new UserFabrics(userFoTest.getId(), 1, 1, 0.00001, "First", "img-1", 3.0);
        UserFabrics uf2 = new UserFabrics(userFoTest.getId(), 2, 1, 0.00006, "Second", "img-2", 15.0);
        usersFabricListFoTest.add(uf1);
        usersFabricListFoTest.add(uf2);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(uf1);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(uf1);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have enough money to complete the operation.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository).findById(2);
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);
    }

    @Test
    public void tryUpgradeFactoryWithWrongFabricId() throws JSONException {
        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/10", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Such a user does not exist in the database.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository).findById(10);
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);
    }

    @Test
    public void ExchangeGoldToSilver() throws JSONException {
        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=100&myGoldCoins=-1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"user\":{\"id\":null,\"name\":\"SomeUser\",\"silverBalance\":100.0," +
                        "\"goldBalance\":1.0,\"increase\":1.0E-5}}", response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verify(usersRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
    }

    @Test
    public void ExchangeSilverToGold() throws JSONException {
        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-200&myGoldCoins=1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"user\":{\"id\":null,\"name\":\"SomeUser\",\"silverBalance\":100.0," +
                        "\"goldBalance\":1.0,\"increase\":1.0E-5}}", response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verify(usersRepository).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);

    }

    @Test
    public void tryExchangeGoldToSilverWithWrongData() throws JSONException {
        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=500&myGoldCoins=-5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have the amount specified in the account.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
    }

    @Test
    public void ttryExchangeGoldToSilverWithWrongData() throws JSONException {

        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-1000&myGoldCoins=5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have the amount specified in the account.\"}",
                response.getBody(), false);

        verify(usersRepository).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
    }
}