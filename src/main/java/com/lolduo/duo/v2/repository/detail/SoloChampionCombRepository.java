package com.lolduo.duo.v2.repository.detail;

import com.lolduo.duo.v2.entity.detail.SoloChampionCombEntity;
import com.lolduo.duo.v2.entity.detail.SpellCombEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SoloChampionCombRepository extends JpaRepository<SoloChampionCombEntity,Long> {
    Optional<SoloChampionCombEntity>findByChampionIdAndPositionAndMainRune(Long championId,String position,Long mainRune);
}
