package com.example.demo.security.auth;

import com.example.demo.domain.Users;
import com.example.demo.repository.UsersRepository;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.credentials.UsernamePasswordCredentials;
import org.pac4j.core.credentials.authenticator.Authenticator;
import org.pac4j.core.profile.CommonProfile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class NamePasswordAuthenticator implements Authenticator<UsernamePasswordCredentials> {

    private final UsersRepository usersRepository;

    public NamePasswordAuthenticator(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    @Override
    public void validate(UsernamePasswordCredentials credentials, WebContext webContext) {

        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder(4);
        Users user = usersRepository.findByEmail(credentials.getUsername());

        if (bCryptPasswordEncoder.matches(credentials.getPassword(),user.getPassword())) {

            CommonProfile profile = new CommonProfile();
            profile.addRole(user.getUserRole());
            profile.addAttribute("user", user);
            credentials.setUserProfile(profile);
        }
    }
}
