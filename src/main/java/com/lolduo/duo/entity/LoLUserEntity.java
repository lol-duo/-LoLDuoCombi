package com.lolduo.duo.entity;


import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.TypeDef;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@NoArgsConstructor
@Table(name = "lol_user")
@Getter
public class LoLUserEntity {
    @Id
    @Column(name = "puuid")
    private String  puuid;
    @Column
    private String tier;
    public LoLUserEntity(String puuid, String tier) {
        this.puuid = puuid;
        this.tier = tier;
    }

    public void setTier(String tier) {
        this.tier = tier;
    }
}
