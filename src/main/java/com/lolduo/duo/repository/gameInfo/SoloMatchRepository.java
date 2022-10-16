package com.lolduo.duo.repository.gameInfo;

import com.lolduo.duo.entity.gameInfo.SoloMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SoloMatchRepository extends JpaRepository<SoloMatchEntity, Long>{
    @Query(value ="select * from solo_match where date = ?1 limit ?2,1000",nativeQuery = true)
    List<SoloMatchEntity> findAllByDate(LocalDate localDate,Long start);
    @Query(value ="select count(*) from solo_match where date =?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate localdate);

    Optional<SoloMatchEntity> findByPositionAndChampionIdAndMainRune(String position,Long championId,Long mainRune);
    @Async
    @Override
    SoloMatchEntity save(SoloMatchEntity soloMatchEntity);
}
