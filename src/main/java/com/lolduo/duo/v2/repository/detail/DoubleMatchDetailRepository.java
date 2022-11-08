package com.lolduo.duo.v2.repository.detail;

import com.lolduo.duo.v2.entity.detail.DoubleMatchDetailEntity;
import com.lolduo.duo.v2.entity.detail.SoloMatchDetailEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.Index;
import java.util.Optional;

public interface DoubleMatchDetailRepository extends JpaRepository<DoubleMatchDetailEntity,Long> {
    Optional<DoubleMatchDetailEntity> findByChampionCombId1AndItemCombId1AndRuneCombId1AndSpellCombId1AndChampionCombId2AndItemCombId2AndRuneCombId2AndSpellCombId2(Long championCombId1,Long itemCombId1,Long runeCombId1,Long spellCombId1,Long championCombId2,Long itemCombId2,Long runeCombId2,Long spellCombId2);
}
