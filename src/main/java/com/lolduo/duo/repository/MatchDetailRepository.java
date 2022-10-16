package com.lolduo.duo.repository;

import com.lolduo.duo.entity.MatchDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetailEntity, Long> {
    @Query(value ="select * from match_detail where date = ?1 limit ?2,100",nativeQuery = true)
    List<MatchDetailEntity> findAllByDate(LocalDate date,Long start);
    @Query(value ="select count(date) from match_detail where date = ?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate date);
}
