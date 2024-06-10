package com.dainam.D2.repository.warehouse;

import com.dainam.D2.models.warehouse.Item;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IITemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByName(String name);
}
