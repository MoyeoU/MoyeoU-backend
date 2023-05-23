package com.moyeou.moyeoubackend.post.repository;

import com.moyeou.moyeoubackend.post.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
}
