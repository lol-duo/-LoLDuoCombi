package com.lolduo.duo.v2.entity.detail;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_match_detail",indexes = {
        @Index(name="solo_match_index",columnList = "solo_comb_id, all_count DESC"),
        @Index(name="multiIndex",columnList = "solo_comb_id, item_comb_id, rune_comb_id, spell_comb_id"),
        @Index(name="multiIndex2",columnList = "all_count, solo_comb_id, item_comb_id, rune_comb_id, spell_comb_id DESC")})
public class SoloMatchDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "solo_comb_id")
    private Long soloCombId;
    @Column(name = "win_count")
    private Long winCount;
    @Column(name = "all_count")
    private Long allCount;

    @Column(name = "item_comb_id")
    private Long itemCombId;
    @Column(name = "rune_comb_id")
    private Long runeCombId;
    @Column(name = "spell_comb_id")
    private Long spellCombId;

    public SoloMatchDetailEntity(Long soloCombId, Long winCount, Long allCount, Long itemCombId, Long runeCombId, Long spellCombId) {
        this.soloCombId = soloCombId;
        this.winCount = winCount;
        this.allCount = allCount;
        this.itemCombId = itemCombId;
        this.runeCombId = runeCombId;
        this.spellCombId = spellCombId;
    }

    public void setWinCount(Long winCount) {
        this.winCount = winCount;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }
}
