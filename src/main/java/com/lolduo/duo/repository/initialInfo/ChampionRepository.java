package com.lolduo.duo.repository.initialInfo;

import com.lolduo.duo.entity.initialInfo.ChampionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChampionRepository extends JpaRepository<ChampionEntity, Long> {
}
