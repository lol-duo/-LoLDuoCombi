package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_champion_comb",indexes = {
        @Index(name="champion_id_index",columnList = "champion_id"),
        @Index(name="position_index",columnList = "position"),
        @Index(name="mainRune_index",columnList = "main_rune"),
        @Index(name="multi_index1",columnList = "champion_id, position, main_rune",unique = true),
        @Index(name="multi_index2",columnList = "position, champion_id, main_rune",unique = true)})
public class SoloChampionCombEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "champion_id")
    private Long championId;

    @Column(name = "position")
    private String position;

    @Column(name = "main_rune")
    private Long mainRune;

    public SoloChampionCombEntity(Long id, Long championId, String position, Long mainRune) {
        this.id = id;
        this.championId = championId;
        this.position = position;
        this.mainRune = mainRune;
    }
}
