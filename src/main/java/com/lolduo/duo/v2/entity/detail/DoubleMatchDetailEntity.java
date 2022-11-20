package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "double_match_detail",indexes = {
        @Index(name="double_match_detail_index",columnList = "all_count, win_rate desc, champion_comb_id1, champion_comb_id2 "),
        @Index(name="multiIndex",columnList = "champion_comb_id1, item_comb_id1, rune_comb_id1, spell_comb_id1, champion_comb_id2, item_comb_id2, rune_comb_id2, spell_comb_id2")})
public class DoubleMatchDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "champion_comb_id1")
    private Long championCombId1;
    @Column(name = "item_comb_id1")
    private Long itemCombId1;
    @Column(name = "rune_comb_id1")
    private Long runeCombId1;
    @Column(name = "spell_comb_id1")
    private Long spellCombId1;

    @Column(name = "champion_comb_id2")
    private Long championCombId2;
    @Column(name = "item_comb_id2")
    private Long itemCombId2;
    @Column(name = "rune_comb_id2")
    private Long runeCombId2;
    @Column(name = "spell_comb_id2")
    private Long spellCombId2;

    @Column(name = "win_count")
    private Long winCount;
    @Column(name = "all_count")
    private Long allCount;
    @Column(name = "win_rate")
    private Double winRate;

    public DoubleMatchDetailEntity(Long championCombId1, Long itemCombId1, Long runeCombId1, Long spellCombId1, Long championCombId2, Long itemCombId2, Long runeCombId2, Long spellCombId2, Long winCount, Long allCount, Double winRate) {
        this.championCombId1 = championCombId1;
        this.itemCombId1 = itemCombId1;
        this.runeCombId1 = runeCombId1;
        this.spellCombId1 = spellCombId1;
        this.championCombId2 = championCombId2;
        this.itemCombId2 = itemCombId2;
        this.runeCombId2 = runeCombId2;
        this.spellCombId2 = spellCombId2;
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
