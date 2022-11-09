package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_match_detail",indexes = {
        @Index(name="all_count_index",columnList = "all_count"),
        @Index(name="solo_match_detail_index",columnList = "all_count, win_rate desc, solo_comb_id"),
        @Index(name="multiIndex",columnList = "solo_comb_id, item_comb_id, rune_comb_id, spell_comb_id")})
public class SoloMatchDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solo_comb_id")
    private Long soloCombId;
    @Column(name = "item_comb_id")
    private Long itemCombId;
    @Column(name = "rune_comb_id")
    private Long runeCombId;
    @Column(name = "spell_comb_id")
    private Long spellCombId;

    @Column(name = "win_count")
    private Long winCount;
    @Column(name = "all_count")
    private Long allCount;
    @Column(name = "win_rate")
    private Double winRate;


    public SoloMatchDetailEntity(Long soloCombId, Long itemCombId, Long runeCombId, Long spellCombId, Long winCount, Long allCount, Double winRate) {
        this.soloCombId = soloCombId;
        this.itemCombId = itemCombId;
        this.runeCombId = runeCombId;
        this.spellCombId = spellCombId;
        this.winCount = winCount;
        this.allCount = allCount;
        this.winRate = winRate;
    }

    public void setWinCount(Long winCount) {
        this.winCount = winCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public void setWinRate(Double winRate) {
        this.winRate = winRate;
    }
}
