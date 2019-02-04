package com.example.demo.controller;

import com.example.demo.domain.Fabrics;
import com.example.demo.repository.FabricsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AdminController {

    private final FabricsRepository fabricsRepository;

    @Autowired
    public AdminController(FabricsRepository fabricsRepository) {
        this.fabricsRepository = fabricsRepository;
    }

    @GetMapping("api/admin/factory-list")
    public Map<String, Object> factoryMarketList() {
        Map<String, Object> response = new HashMap<>();
        List<Fabrics> fabrics = fabricsRepository.findAll();
        response.put("fabrics", fabrics);
        return response;
    }

    @PostMapping("api/admin/add-factory")
    public Map<String, Object> addFabric(@RequestBody Map<String, String> body) {
        Map<String, Object> response = new HashMap<>();
        try {
            Fabrics fabric = new Fabrics(
                    Double.parseDouble(body.get("newPrice")),
                    body.get("newName"),
                    Double.parseDouble(body.get("newUpgrade")),
                    Double.parseDouble(body.get("newMining")),
                    body.get("image"));
            response.put("message",fabricsRepository.save(fabric));
        }catch (NullPointerException ex){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Lack of transmitted data to create an object.");
        }

        return response;
    }
}
