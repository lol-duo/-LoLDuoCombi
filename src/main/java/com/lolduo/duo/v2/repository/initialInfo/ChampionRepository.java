package com.lolduo.duo.v2.repository.initialInfo;

import com.lolduo.duo.v2.entity.initialInfo.ChampionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionRepository extends JpaRepository<ChampionEntity, Long> {
}
