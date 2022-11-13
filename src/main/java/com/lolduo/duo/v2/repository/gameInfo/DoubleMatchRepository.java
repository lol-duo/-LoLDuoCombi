package com.lolduo.duo.v2.repository.gameInfo;

import com.lolduo.duo.v2.entity.gameInfo.DoubleMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DoubleMatchRepository extends JpaRepository<DoubleMatchEntity, Long> {
    @Query(value ="select * from double_match where all_count > ?1",nativeQuery = true)
    List<DoubleMatchEntity> findAllByAllCount(Long allCount);
    @Query(value = "select floor(sum(all_count)/20) from double_match",nativeQuery = true)
    Optional<Long> getAllCountSum();
    @Query(value ="select count(date) from double_match where date =?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate localdate);
    Optional<DoubleMatchEntity> findByPosition1AndPosition2AndChampionId1AndChampionId2AndMainRune1AndMainRune2(String position1,String position2,Long champion1,Long champion2,Long mainRune1,Long mainRune2);
}
