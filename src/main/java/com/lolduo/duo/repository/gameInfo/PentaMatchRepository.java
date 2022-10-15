package com.lolduo.duo.repository.gameInfo;

import com.lolduo.duo.entity.gameInfo.PentaMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PentaMatchRepository extends JpaRepository<PentaMatchEntity,Long> ,IMatchRepository{
    @Query(value ="select * from penta_match where date = ?1 limit ?2,1000",nativeQuery = true)
    List<PentaMatchEntity> findAllByDate(LocalDate localDate,Long start);
    @Query(value ="select count(*) from penta_match where date =?1",nativeQuery = true)
    Optional<Long> findSizeByDate(LocalDate localdate);
}
