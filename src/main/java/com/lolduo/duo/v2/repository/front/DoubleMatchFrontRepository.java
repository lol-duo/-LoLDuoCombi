package com.lolduo.duo.v2.repository.front;

import com.lolduo.duo.v2.entity.front.DoubleMatchFrontEntity;
import com.lolduo.duo.v2.entity.front.SoloMatchFrontEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface DoubleMatchFrontRepository extends JpaRepository<DoubleMatchFrontEntity, Long> {
    Optional<DoubleMatchFrontEntity> findByDoubleMatchId(Long doubleMatchId);
}
