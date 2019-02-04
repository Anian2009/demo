package com.example.demo.security.auth;

import com.example.demo.domain.RoleType;
import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.util.CommonHelper;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

public class NamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

    private final UsersRepository usersRepository;

    public NamePasswordAuthenticator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials, WebContext webContext) {

        final String username = credentials.getUsername();
        final String password = credentials.getPassword();

        if (CommonHelper.isBlank(username) || CommonHelper.isBlank(password)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "Empty username or password");
        }

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);

        Users user = usersRepository.findByEmail(username);
        if (user == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,
                    "User with this email not found in DB.");
        }
        if (!Boolean.valueOf(user.getActivationCode())){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "User has not completed registration. Check your email and follow the instructions.");
        }

        if (bCryptPasswordEncoder.matches(password,user.getPassword())) {

            CommonProfile profile = new CommonProfile();
            profile.addRole(RoleType.USER.toString());
            profile.addAttribute("user", user);
            credentials.setUserProfile(profile);
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Wrong password.");
        }
    }
}
