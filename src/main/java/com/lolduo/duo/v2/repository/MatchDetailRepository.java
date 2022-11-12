package com.lolduo.duo.v2.repository;

import com.lolduo.duo.v2.entity.MatchDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface MatchDetailRepository extends JpaRepository<MatchDetailEntity, Long> {
    @Query(value ="select * from match_detail where date = ?1 limit ?2,100",nativeQuery = true)
    List<MatchDetailEntity> findAllByDate(String date,Long start);

    @Query(value ="select * from match_detail where date = ?1 and match_id > ?2 limit 0,100",nativeQuery = true)
    List<MatchDetailEntity> findAllByDateAndMatchId(String date,String matchId);

    @Query(value ="select count(date) from match_detail where date = ?1",nativeQuery = true)
    Optional<Long> findSizeByDate(String date);
}
