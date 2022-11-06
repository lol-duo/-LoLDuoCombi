package com.lolduo.duo.v2.repository.detail;

import com.lolduo.duo.v2.entity.detail.SpellCombEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpellCombRepository extends JpaRepository<SpellCombEntity,Long> {
    Optional<SpellCombEntity> findBySpell1AndSpell2(Long spell1, Long spell2);
}
