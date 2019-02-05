package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.FabricsRepository;
import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Charge;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DashboardController extends UserFromSecurity{

    @Value("${stripe.apiKey}")
    private String myApiKey;

    @Value("${stripe.price}")
    private Integer price;

    @Value("${stripe.currency}")
    private String currency;

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.description}")
    private String description;

    private final FabricsRepository fabricsRepository;

    private final UserFabricsRepository userFabricsRepository;

    private final UsersRepository usersRepository;

    @Autowired
    public DashboardController(FabricsRepository fabricsRepository, UserFabricsRepository userFabricsRepository, UsersRepository usersRepository) {
        this.fabricsRepository = fabricsRepository;
        this.userFabricsRepository = userFabricsRepository;
        this.usersRepository = usersRepository;
    }

    @GetMapping("api/user/dashboard")
    public ResponseEntity<Map<String, Object>> myFabric(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Map<String, Object> response = new HashMap<>();
        Users user = getUser(httpRequest,httpResponse);
        List<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
        List<Users> users;
        (users = usersRepository.findAll()).sort((o1, o2) -> o2.getTotalBalance().compareTo(o1.getTotalBalance()));
        response.put("fabrics", fabrics);
        response.put("user", user);
        response.put("users", users);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("api/user/factory-market")
    public Map<String, Object> factoryMarketList() {
        Map<String, Object> response = new HashMap<>();
        List<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @PostMapping("api/user/buy-factory")
    public ResponseEntity<Map<String, Object>> bayFabric(HttpServletRequest httpRequest, HttpServletResponse httpResponse, @RequestBody Map<String,Object> body) {
        Map<String, Object> response = new HashMap<>();
        Users user = getUser(httpRequest,httpResponse);
        Fabrics fabric = fabricsRepository.findById(Integer.parseInt(body.get("id").toString()));
        if (fabric == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Such a plant does not exist in the database.");
        }
        if (user.getSilverBalance() < fabric.getPrice()){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "The user lacks money.");
        }
        else {
            UserFabrics userFabric = new UserFabrics(user, fabric, fabric.getMiningPerSecond());
            response.put("userFabric",
                    userFabricsRepository.save(userFabric));
            user.setIncrease(user.getIncrease() + fabric.getMiningPerSecond());
            user.setSilverBalance(user.getSilverBalance() - fabric.getPrice());
            response.put("user",
                    usersRepository.save(user));
        }

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PutMapping("api/user/upgrade-factory/{id}")
    public ResponseEntity<Map<String, Object>> upgrade(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        UserFabrics fabric = userFabricsRepository.findById(id);

        try {
            Users user = fabric.getMaster();
            if (user.getSilverBalance() < fabric.getFabric().getUpgrade()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user does not have enough money to complete the operation.");
            }
            else {
                userFabricsRepository.save(fabric.update());
                List<UserFabrics> fabrics = userFabricsRepository.findByMaster(user);
                response.put("fabrics", fabrics);
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }catch (NullPointerException ex){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Such a user does not exist in the database.");
        }
    }

    @GetMapping("api/user/buy-gold-status")
    public ResponseEntity<Map<String, Object>> buyGoldStatus(HttpServletRequest httpRequest, HttpServletResponse httpResponse,@RequestParam Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        Stripe.apiKey = myApiKey;
        String token = request.get("stripeToken").toString();
        Map<String, Object> params = new HashMap<>();
        params.put("amount", price);
        params.put("currency", currency);
        params.put("description", description);
        params.put("source", token);
        try {
            Charge charge = Charge.create(params);
            if (charge.getStatus().equals("succeeded")) {
                Users user = getUser(httpRequest,httpResponse);
                usersRepository.save(user.changeStatus());
                response.put("message", "Ok");
                return new ResponseEntity<>(response, HttpStatus.OK);
            } else{
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        charge.getStatus()+ " Payment failed.");
            }
        } catch (StripeException e) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    e.getMessage());
        }

    }

    @GetMapping("api/user/exchange")
    public ResponseEntity<Map<String, Object>> exchangeGold(HttpServletRequest httpRequest, HttpServletResponse httpResponse,@RequestParam Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        double mySilverCoins = Double.parseDouble(request.get("mySilverCoins").toString());
        double myGoldCoins = Double.parseDouble(request.get("myGoldCoins").toString());
        Users user = getUser(httpRequest,httpResponse);
        if (myGoldCoins<0){
            if (Math.abs(myGoldCoins)>user.getGoldBalance()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user does not have the amount specified in the account.");
            }else{
                user.setGoldBalance(user.getGoldBalance() + myGoldCoins);
                user.setSilverBalance(user.getSilverBalance() + (Math.abs(myGoldCoins)*rateGold));
                response.put("user",usersRepository.save(user));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        }else{
            if (Math.abs(mySilverCoins)>user.getSilverBalance()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "The user does not have the amount specified in the account.");
            }else{
                user.setGoldBalance(user.getGoldBalance() + (Math.abs(mySilverCoins)/rateSilver));
                user.setSilverBalance(user.getSilverBalance() + mySilverCoins);
                response.put("user",usersRepository.save(user));
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        }
    }
}
