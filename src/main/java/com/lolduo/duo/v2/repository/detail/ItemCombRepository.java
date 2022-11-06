package com.lolduo.duo.v2.repository.detail;


import com.lolduo.duo.v2.entity.detail.ItemCombEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ItemCombRepository  extends JpaRepository<ItemCombEntity, Long> {
    Optional<ItemCombEntity> findByItem1AndItem2AndItem3(Long item1,Long item2,Long item3);
}
