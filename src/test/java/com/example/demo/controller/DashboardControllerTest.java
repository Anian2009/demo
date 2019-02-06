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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        userFoTest = new Users("SomeUser","User@some.net","USER","somePassword","user-token");

        headers = new HttpHeaders();
        headers.add(Pac4jConfig.HEADER_TOKEN_NAME,"user-token");
    }

    private Users userFoTest;
    private HttpHeaders headers;

    @Test
    public void myFabricEverythingOkExpectedOk() throws IOException, JSONException {

        List<Users> usersListFoTest = Arrays.asList(
                new Users("firstUser","firstUser@some.net","USER","somePassword-1","someToken-1"),
                new Users("secondUser","secondUser@some.net","USER","somePassword-2","someToken-2"),
                new Users("threadUser","threadUser@some.net","USER","somePassword-3","someToken-3"),
                new Users("forthUser","forthUser@some.net","USER","somePassword-4","someToken-4")
        );

        List<UserFabrics> usersFabricListFoTest = Arrays.asList(
                new UserFabrics(userFoTest,
                        new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                        1,0.00001),
                new UserFabrics(userFoTest,
                        new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2"),
                        2,0.00012)
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findByMaster(any())).thenReturn(usersFabricListFoTest);
        Mockito.when(usersRepository.findAll()).thenReturn(usersListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=7", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        verify(usersRepository, times(1)).findAll();
        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository).findByMaster(userFoTest);
        verifyNoMoreInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToMyFabricList.json"), response.getBody(), false);
    }

    @Test
    public void tryMyFabricWrongTokenExpectedUnauthorized() {

        userFoTest.setToken("wrong-token");

        ResponseEntity<String> response = rest.exchange("/api/user/dashboard?id=10", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(userFabricsRepository);
        Assert.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    public void factoryMarketListEverythingOkExpectedOk() throws IOException, JSONException {

        List<Fabrics> listOfFabrics = Arrays.asList(
                new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2"),
                new Fabrics(10.0,"threadFabric",30.0,0.00015,"image-3"),
                new Fabrics(50.0,"forthFabric",150.0,0.0008,"image-4")
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findAll()).thenReturn(listOfFabrics);

        ResponseEntity<String> response = rest.exchange("/api/user/factory-market", HttpMethod.GET,
                new HttpEntity<String>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository, times(1)).findAll();
        verifyNoMoreInteractions(usersRepository);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToFactoryMarketList.json"), response.getBody(), false);

    }

    @Test
    public void buyFabricAllOkExpectedOk() throws JSONException, IOException {

        userFoTest.setSilverBalance(10.0);

        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);
        UserFabrics userFabric = new UserFabrics(userFoTest,fabric,fabric.getMiningPerSecond());

        Map<String,String> request = new HashMap<>();
        request.put("id","1");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(fabric);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verify(usersRepository, times(1)).save(any());
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository, times(1)).findById(1);
        verifyNoMoreInteractions(fabricsRepository);
        verify(userFabricsRepository, times(1)).save(any());
        verifyNoMoreInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToBuyFabric.json"), response.getBody(), false);

    }

    @Test
    public void tryBuyFabricLuckOfMoneyExpectedBadRequest() throws JSONException {

        userFoTest.setSilverBalance(0.0);

        Fabrics fabric = new Fabrics(1.0, "OneOfFabric", 3.0, 0.00001);

        Map<String,String> request = new HashMap<>();
        request.put("id","1");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(fabric);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository, times(1)).findById(1);
        verifyNoMoreInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user lacks money.\"}", response.getBody(), false);
    }


    @Test
    public void tryBuyFabricWithWrongIdExpectedNotFound() throws JSONException {

        Map<String,String> request = new HashMap<>();
        request.put("id","5");

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(fabricsRepository.findById(anyInt())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/user/buy-factory", HttpMethod.POST,
                new HttpEntity<>(request,headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(fabricsRepository, times(1)).findById(5);
        verifyNoMoreInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Such a plant does not exist in the database.\"}",
                response.getBody(), false);

    }

    @Test
    public void tryUpgradeFactoryAllOkExpectedOk() throws JSONException, IOException {

        userFoTest.setSilverBalance(10.0);

        List<UserFabrics> usersFabricListFoTest = Arrays.asList(
                new UserFabrics(userFoTest,
                        new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                        1,0.00001),
                new UserFabrics(userFoTest,
                        new Fabrics(5.0,"secondFabric",15.0,0.00006,"image-2"),
                        2,0.00012)
        );

        UserFabrics userFabric = new UserFabrics(userFoTest,
                new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                1,0.00001
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(userFabric);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(userFabric);
        Mockito.when(userFabricsRepository.findByMaster(any())).thenReturn(usersFabricListFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository, times(1)).findById(2);
        verify(userFabricsRepository, times(1)).save(any());
        verify(userFabricsRepository, times(1)).findByMaster(userFoTest);
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);

        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals(readFromFile("responseToUpgradeFabric.json"), response.getBody(), false);
    }

    @Test
    public void tryUpgradeFactoryLackOfMoneyExpectedBadRequest() throws JSONException {

        userFoTest.setSilverBalance(1.0);

        UserFabrics userFabric = new UserFabrics(userFoTest,
                new Fabrics(1.0,"firstFabric",3.0,0.00001,"image-1"),
                1,0.00001
        );

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(userFabric);
        Mockito.when(userFabricsRepository.save(any())).thenReturn(userFabric);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/2", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository, times(1)).findById(2);
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have enough money to complete the operation.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryUpgradeFactoryWithWrongFabricIdExpectedBadRequest() throws JSONException {

        Mockito.when(usersRepository.findByToken("user-token")).thenReturn(userFoTest);
        Mockito.when(userFabricsRepository.findById(anyInt())).thenReturn(null);

        ResponseEntity<String> response = rest.exchange("/api/user/upgrade-factory/10", HttpMethod.PUT,
                new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verify(userFabricsRepository, times(1)).findById(10);
        verifyNoMoreInteractions(userFabricsRepository);
        verifyZeroInteractions(fabricsRepository);

        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"Such a user does not exist in the database.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryExchangeGoldToSilverAllOkExpectedOk() throws JSONException {

        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=100&myGoldCoins=-1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verify(usersRepository, times(1)).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"user\":{\"id\":null,\"name\":\"SomeUser\"," +
                        "\"silverBalance\":100.0,\"goldBalance\":1.0,\"increase\":1.0E-5}}",
                response.getBody(), false);

    }

    @Test
    public void tryExchangeSilverToGoldAllOkExpectedOk() throws JSONException {

        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);
        Mockito.when(usersRepository.save(any())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-200&myGoldCoins=1",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verify(usersRepository, times(1)).save(any());
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);
        Assert.assertEquals(HttpStatus.OK, response.getStatusCode());
        JSONAssert.assertEquals("{\"user\":{\"id\":null,\"name\":\"SomeUser\"," +
                        "\"silverBalance\":100.0,\"goldBalance\":1.0,\"increase\":1.0E-5}}",
                response.getBody(), false);
    }

    @Test
    public void tryExchangeGoldToSilverChiterExpectedBadRequest() throws JSONException {

        userFoTest.setGoldBalance(2.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=500&myGoldCoins=-5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have the amount specified in the account.\"}",
                response.getBody(), false);
    }

    @Test
    public void tryExchangeSilverToGoldChiterExpectedBadRequest() throws JSONException {

        userFoTest.setSilverBalance(300.0);

        Mockito.when(usersRepository.findByToken(anyString())).thenReturn(userFoTest);

        ResponseEntity<String> response = rest.exchange("/api/user/exchange?mySilverCoins=-1000&myGoldCoins=5",
                HttpMethod.GET, new HttpEntity<>(headers), String.class);

        verify(usersRepository, times(1)).findByToken("user-token");
        verifyNoMoreInteractions(usersRepository);
        verifyZeroInteractions(fabricsRepository);
        verifyZeroInteractions(userFabricsRepository);

        Assert.assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        JSONAssert.assertEquals("{\"message\":\"The user does not have the amount specified in the account.\"}",
                response.getBody(), false);

    }

}