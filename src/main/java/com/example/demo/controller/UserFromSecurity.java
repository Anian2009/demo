package com.example.demo.controller;

import com.example.demo.domain.Users;
import org.pac4j.core.context.J2EContext;
import org.pac4j.core.context.WebContext;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserFromSecurity  {

    public Users getUser(ServletRequest servletRequest, ServletResponse servletResponse){
        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        WebContext context = new J2EContext(request, response);
        return (Users)new ProfileManager<CommonProfile>(context).get(false).get().getAttribute("user");
    }
}
