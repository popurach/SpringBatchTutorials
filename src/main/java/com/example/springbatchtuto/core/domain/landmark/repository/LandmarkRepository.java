package com.example.springbatchtuto.core.domain.landmark.repository;

import com.example.springbatchtuto.core.domain.landmark.model.entity.Landmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LandmarkRepository extends JpaRepository<Landmark, Long> {

}
