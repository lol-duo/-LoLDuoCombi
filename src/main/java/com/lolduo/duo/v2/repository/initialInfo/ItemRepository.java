package com.lolduo.duo.v2.repository.initialInfo;

import com.lolduo.duo.v2.entity.initialInfo.ItemEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {
}
