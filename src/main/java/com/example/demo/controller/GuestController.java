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
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.apache.commons.codec.digest.DigestUtils.md5Hex;

@RestController
public class GuestController extends SpecialTasks {

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
    public GuestController(UsersRepository usersRepository, MailSender mailSender) {
        this.usersRepository = usersRepository;
        this.mailSender = mailSender;
    }

    @PostMapping(value = {"/api/guest/log-in"})
    public ResponseEntity<Map<String, Object>> login(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
        Map<String, Object> response = new HashMap<>();
        Users user = getUser(httpRequest, httpResponse);
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
        if (!new EmailValidator().validate(body.get("email"))) {
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
            } catch (MailSendException ex) {
                System.out.println(ex.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        body.get("email") + " - Account was suspended due to inactivity");
            }
            Users user = new Users(
                    body.get("name"),
                    body.get("email").toLowerCase(),
                    RoleType.USER.toString(),
                    bCryptPasswordEncoder.encode(body.get("password")),
                    md5Hex(body.get("password") + body.get("name")));
            user.setActivationCode(activationCode);
            usersRepository.save(user);
            response.put("message", "A message was sent to your email with further instructions");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
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
            response.put("message", "Activation completed successfully. " +
                    "You can enter the game using your email address and password.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "A user with such an activation key was not found in the database.");
        }
    }

    @PostMapping("api/guest/forgotPassword")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByEmail(body.get("email"));
        if ( user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The database does not contain information about the specified e-mail account.");
        } else {
            String message = String.format(
                    "Hello %s! \n" +
                            "If you want to change the access password, go to the specified link. \n" +
                            " %s/changePassword.html?code=%s",
                    user.getName(), adress, user.getToken());
            try {
                mailSender.send(user.getEmail(), "Change password", message);
            } catch (MailSendException ex) {
                System.out.println(ex.getMessage());
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        body.get("email") + " - Account was suspended due to inactivity");
            }
            response.put("message", "A message was sent to your email with further instructions.");
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }

    @PutMapping("changePasswordCode/{code}")
    public ResponseEntity<Map<String, Object>> changePasswordCode(@PathVariable String code) {
        Map<String, Object> response = new HashMap<>();
        Users user = usersRepository.findByToken(code);
        if (user != null) {
            return new ResponseEntity<>(response, HttpStatus.OK);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "A user with such an activation key was not found in the database.");
        }
    }

    @PostMapping("api/guest/changePassword")
    public ResponseEntity<Map<String, Object>> changePassword(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Users user = usersRepository.findByToken(body.get("code"));
        if ( user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "The database does not contain information about the specified e-mail account.");
        } else {
            user.setPassword(bCryptPasswordEncoder.encode(body.get("password")));
            user.setToken(md5Hex(body.get("password") + user.getName()));
            usersRepository.save(user);
            return new ResponseEntity<>(response, HttpStatus.OK);
        }
    }
}
