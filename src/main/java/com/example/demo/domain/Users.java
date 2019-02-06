package com.example.demo.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;

@Entity
@Table(name = "users")
public class Users /* implements Comparable<Users> */{

    private final static Integer HAVE_STATUS = 1;

    private final static Integer HAVE_NO_STATUS = 0;

    private final static Double START_BALANCE = 0.0;

    private final static Double START_INCREASE = 0.00001;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    @JsonIgnore
    private String email;
    private Double silverBalance;
    private Double goldBalance;
    @JsonIgnore
    private String userRole;

    @JsonIgnore
    private String password;
    @JsonIgnore
    private String token;
    @JsonIgnore
    private Integer goldStatus;
    @JsonIgnore
    private Integer silverStatus;
    private Double increase;
    @JsonIgnore
    private Double totalBalance;
    @JsonIgnore
    private String activationCode;

    public Users() {
    }

    public Users(String name, String email, String role, String password, String token) {
        this.name = name;
        this.email = email;
        this.silverBalance = START_BALANCE;
        this.goldBalance = START_BALANCE;
        this.userRole = role;
        this.password = password;
        this.token = token;
        this.goldStatus = HAVE_NO_STATUS;
        this.silverStatus = HAVE_STATUS;
        this.increase = START_INCREASE;
        this.totalBalance = START_BALANCE;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Double getSilverBalance() {
        return silverBalance;
    }

    public void setSilverBalance(Double silverBalance) {
        this.silverBalance = silverBalance;
    }

    public Double getGoldBalance() {
        return goldBalance;
    }

    public void setGoldBalance(Double goldBalance) {
        this.goldBalance = goldBalance;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getGoldStatus() {
        return goldStatus;
    }

    public void setGoldStatus(Integer goldStatus) {
        this.goldStatus = goldStatus;
    }

    public Integer getSilverStatus() {
        return silverStatus;
    }

    public void setSilverStatus(Integer silverStatus) {
        this.silverStatus = silverStatus;
    }

    public Double getIncrease() {
        return increase;
    }

    public void setIncrease(Double increase) {
        this.increase = increase;
    }

    public Double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(Double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public Users changeStatus() {
        setGoldStatus(HAVE_STATUS);
        setSilverStatus(HAVE_NO_STATUS);
        return this;
    }
}
