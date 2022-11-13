package com.lolduo.duo.v2.repository.front;

import com.lolduo.duo.v2.entity.front.SoloMatchFrontEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface SoloMatchFrontRepository extends JpaRepository<SoloMatchFrontEntity,Long> {
    Optional<SoloMatchFrontEntity> findBySoloMatchId(Long soloMatchId);
}
