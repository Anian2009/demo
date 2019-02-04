package com.example.demo.repository;

import com.example.demo.domain.Fabrics;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FabricsRepository extends JpaRepository<Fabrics, Long> {
    Fabrics findById(Integer id);

}
