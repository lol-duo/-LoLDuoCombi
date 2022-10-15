package com.lolduo.duo.repository;

import com.lolduo.duo.entity.MatchDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MatchDetailRepository extends JpaRepository<MatchDetailEntity, Long> {
    List<MatchDetailEntity> findAllByDate(LocalDate localDate);
}
