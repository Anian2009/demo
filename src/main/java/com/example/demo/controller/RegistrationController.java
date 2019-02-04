package com.example.demo.controller;

import com.example.demo.EmailValidator;
import com.example.demo.domain.RoleType;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import com.example.demo.service.MailSender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailSendException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RestController
public class RegistrationController extends UserFromSecurity {

    @Value("${exchange.rateGold}")
    private Integer rateGold;

    @Value("${exchange.rateSilver}")
    private Integer rateSilver;

    @Value("${stripe.price}")
    private Integer price;

    @Value("${adress}")
    private String adress;

    private final UsersRepository usersRepository;

    private final MailSender mailSender;

    @Autowired
    public RegistrationController(UsersRepository usersRepository, MailSender mailSender) {
        this.usersRepository = usersRepository;
        this.mailSender = mailSender;
    }

    @PostMapping(value = {"/api/guest/log-in","/api/admin/log-in"})
    public ResponseEntity<Map<String, Object>> login(HttpServletRequest httpRequest, HttpServletResponse httpResponse/*, @RequestBody Map<String, String> body*/){
//        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Map<String, Object> response = new HashMap<>();
//        Users user = usersRepository.findByEmail(body.get("email"));
//        if (user == null){
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
//                    "User with this email not found in DB.");
//        }
//        if (!Boolean.valueOf(user.getActivationCode())){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "User has not completed registration. Check your email and follow the instructions.");
//        }
//        try {
//            if (!bCryptPasswordEncoder.matches(body.get("password"),user.getPassword())){
//                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                        "Password is incorrect.");
//            }
//        }catch (NullPointerException ex){
//            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
//                    "The user did not provide enough information to identify.");
//        }

        Users user = getUser(httpRequest,httpResponse);


        response.put("message", user.getToken());
        response.put("role", user.getUserRole());
        response.put("email", user.getEmail());
        response.put("price", price);
        response.put("rateGold", rateGold);
        response.put("rateSilver", rateSilver);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/guest/registration")
    public ResponseEntity<Map<String, Object>> addUser(@RequestBody Map<String, String> body) {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Map<String, Object> response = new HashMap<>();

        if (!new EmailValidator().validate(body.get("email"))){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "E-mail incorrectly written.");
        }
        if (usersRepository.findByEmail(body.get("email")) == null) {

            String activationCode = UUID.randomUUID().toString();
            String message = String.format(
                    "Hello %s! \n" +
                            "Welcome to 'Oligarch'. " +
                            "\n" +
                            "To complete registration, please follow the link.: - %s/activation.html?code=%s",
                    body.get("name"), adress, activationCode);
            try {
                mailSender.send(body.get("email"), "Activation code", message);
            } catch (MailSendException ex){
                System.out.println(ex.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        body.get("email")+" - Account was suspended due to inactivity");
            }

            Users user = new Users(
                    body.get("name"),
                    body.get("email").toLowerCase(),
                    RoleType.USER.toString(),
                    bCryptPasswordEncoder.encode(body.get("password")),
                    md5Hex(body.get("password") + body.get("name")));
            user.setActivationCode(activationCode);
            usersRepository.save(user);

            response.put("name", body.get("name"));
            return new ResponseEntity<>(response,HttpStatus.OK);
        } else{
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "A user with such an email already exists.");
        }
    }

    @PutMapping("activation-code/{code}")
    public ResponseEntity<Map<String, Object>> activationCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByActivationCode(code);
        if (user != null) {
            user.setActivationCode("true");
            usersRepository.save(user);
            response.put("message","OK");
            return new ResponseEntity<>(response,HttpStatus.OK);
        }else{
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "A user with such an activation key was not found in the database.");
        }
    }
}
