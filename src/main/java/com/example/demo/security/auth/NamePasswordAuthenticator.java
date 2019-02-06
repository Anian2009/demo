package com.example.demo.security.auth;

import com.example.demo.domain.RoleType;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class NamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

    private static final Logger logger = LoggerFactory.getLogger(NamePasswordAuthenticator.class);

    private final UsersRepository usersRepository;

    public NamePasswordAuthenticator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials, WebContext webContext) {

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();

        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(password)) {
            logger.info("Empty username or password");
            return;
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users user = usersRepository.findByEmail(username);
        if (user == null){
            logger.info("User with this email not found in DB.");
            return;
        }
        if (!Boolean.valueOf(user.getActivationCode())){
            logger.info("User has not completed registration. Check your email and follow the instructions.");
            return;
        }

        if (bCryptPasswordEncoder.matches(password,user.getPassword())) {

            CommonProfile profile = new CommonProfile();
            profile.addRole(RoleType.USER.toString());
            profile.addAttribute("user", user);
            credentials.setUserProfile(profile);
        } else {
            logger.info("Wrong password.");
            return;
        }
    }
}
