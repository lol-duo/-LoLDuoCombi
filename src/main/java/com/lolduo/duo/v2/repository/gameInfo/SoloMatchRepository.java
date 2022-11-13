package com.lolduo.duo.v2.repository.gameInfo;

import com.lolduo.duo.v2.entity.gameInfo.SoloMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SoloMatchRepository extends JpaRepository<SoloMatchEntity, Long>{
    @Query(value ="select * from solo_match where all_count > ?1",nativeQuery = true)
    List<SoloMatchEntity> findAllByAllCount(Long allCount);

    @Query(value = "select floor(sum(all_count)/10) from solo_match",nativeQuery = true)
    Optional<Long> getAllCountSum();

    @Query(value ="select count(date) from solo_match where date =?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate localdate);

    Optional<SoloMatchEntity> findByPositionAndChampionIdAndMainRune(String position,Long championId,Long mainRune);
}
