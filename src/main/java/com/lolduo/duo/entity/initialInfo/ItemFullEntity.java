package com.lolduo.duo.entity.initialInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "item_full")
public class ItemFullEntity {
    @Id
    private Long id;

    public ItemFullEntity(Long id) {
        this.id = id;
    }
}
