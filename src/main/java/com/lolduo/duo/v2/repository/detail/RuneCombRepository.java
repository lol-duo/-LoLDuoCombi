package com.lolduo.duo.v2.repository.detail;

import com.lolduo.duo.v2.entity.detail.RuneCombEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RuneCombRepository extends JpaRepository<RuneCombEntity, Long> {
    Optional<RuneCombEntity> findByMainRuneConceptAndMainRune0AndSubRuneConceptAndMainRune1AndMainRune2AndMainRune3AndSubRune1AndSubRune2(
            Long mainRuneConcept,Long mainRune0,Long subRuneConcept,
            Long mainRune1,Long mainRune2,Long mainRune3,Long subRune1,Long subRune2);
}
