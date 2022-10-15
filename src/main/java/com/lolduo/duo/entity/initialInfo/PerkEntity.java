package com.lolduo.duo.entity.initialInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "perk_id")
public class PerkEntity {
    @Id
    private Long id;
    @Column
    private String name;
    @Column
    private String imgUrl;

    public PerkEntity(Long id, String name, String imgUrl) {
        this.id = id;
        this.name = name;
        this.imgUrl = imgUrl;
    }
}
