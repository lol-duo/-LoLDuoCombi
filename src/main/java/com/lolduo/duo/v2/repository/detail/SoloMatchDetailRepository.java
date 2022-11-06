package com.lolduo.duo.v2.repository.detail;

import com.lolduo.duo.v2.entity.detail.SoloChampionCombEntity;
import com.lolduo.duo.v2.entity.detail.SoloMatchDetailEntity;
import com.lolduo.duo.v2.entity.gameInfo.SoloMatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Column;
import java.util.Optional;

public interface SoloMatchDetailRepository extends JpaRepository<SoloMatchDetailEntity,Long> {
    Optional<SoloMatchDetailEntity> findBySoloCombIdAndItemCombIdAndRuneCombIdAndSpellCombId(Long soloCombId, Long itemCombId,Long runeCombId,Long spellCombId);

}
