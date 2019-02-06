package com.example.demo.controller;

import com.example.demo.domain.UserFabrics;
import com.example.demo.domain.Users;
import com.example.demo.repository.UserFabricsRepository;
import com.example.demo.repository.UsersRepository;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public abstract class SpecialTasks {

    private static final Integer UNO_LEVEL_MORE = 1;

    @Autowired
    private UsersRepository usersRepository;

    @Autowired
    private UserFabricsRepository userFabricsRepository;

    public Users getUser(ServletRequest servletRequest, ServletResponse servletResponse) {
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        WebContext context = new J2EContext(request, response);
        return (Users) new ProfileManager<CommonProfile>(context).get(false).get().getAttribute("user");
    }

    public void update(UserFabrics fabric, Users user) {
        user.setIncrease(user.getIncrease() + fabric.getMiningPerSecond());
        fabric.setMiningPerSecond(fabric.getMiningPerSecond() + fabric.getMiningPerSecond());
        fabric.setFabricLevel(fabric.getFabricLevel() + UNO_LEVEL_MORE);
        user.setSilverBalance(user.getSilverBalance() - fabric.getUpgrade());
        usersRepository.save(user);
        userFabricsRepository.save(fabric);
    }
}
