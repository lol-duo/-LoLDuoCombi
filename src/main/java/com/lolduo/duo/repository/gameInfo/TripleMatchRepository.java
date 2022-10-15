package com.lolduo.duo.repository.gameInfo;

import com.lolduo.duo.entity.gameInfo.TripleMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TripleMatchRepository extends JpaRepository<TripleMatchEntity,Long> ,IMatchRepository{
    @Query(value ="select * from triple_match where date = ?1 limit ?2,1000",nativeQuery = true)
    List<TripleMatchEntity> findAllByDate(LocalDate localDate,Long start);
    @Query(value ="select count(*) from triple_match where date =?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate localdate);
}
