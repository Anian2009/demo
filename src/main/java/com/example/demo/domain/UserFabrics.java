package com.example.demo.domain;

import javax.persistence.*;

@Entity
@Table(name = "user_fabrics_info")
public class UserFabrics {
    private static final Integer START_LEVEL = 1;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    @JoinColumn(name = "user_id")
    private Integer master;

    @JoinColumn(name = "fabric_id")
    private Integer fabric;

    @JoinColumn(name = "fabric_leval")
    private Integer fabricLevel;

    @JoinColumn(name = "fab_mining_p_s")
    private Double miningPerSecond;
    private String name;
    private String img;
    private Double upgrade;

    public UserFabrics() {
    }

    public UserFabrics(Integer master, Integer fabric, Integer fabricLevel, Double miningPerSecond, String name, String img, Double upgrade) {
        this.master = master;
        this.fabric = fabric;
        this.fabricLevel = fabricLevel;
        this.miningPerSecond = miningPerSecond;
        this.name = name;
        this.img = img;
        this.upgrade = upgrade;
    }

    public UserFabrics(Integer user, Integer fabric, Double miningPerSecond, String name, String img, Double upgrade) {
        this.master = user;
        this.fabric = fabric;
        this.fabricLevel = START_LEVEL;
        this.miningPerSecond = miningPerSecond;
        this.name = name;
        this.img = img;
        this.upgrade = upgrade;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMaster() {
        return master;
    }

    public void setMaster(Integer master) {
        this.master = master;
    }

    public Integer getFabric() {
        return fabric;
    }

    public void setFabric(Integer fabric) {
        this.fabric = fabric;
    }

    public Integer getFabricLevel() {
        return fabricLevel;
    }

    public void setFabricLevel(Integer fabricLevel) {
        this.fabricLevel = fabricLevel;
    }

    public Double getMiningPerSecond() {
        return miningPerSecond;
    }

    public void setMiningPerSecond(Double miningPerSecond) {
        this.miningPerSecond = miningPerSecond;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Double getUpgrade() {
        return upgrade;
    }

    public void setUpgrade(Double upgrade) {
        this.upgrade = upgrade;
    }
}
