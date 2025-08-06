package com.example.repository;

import com.example.model.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DiscountRepository extends JpaRepository<Discount, Integer> {

}