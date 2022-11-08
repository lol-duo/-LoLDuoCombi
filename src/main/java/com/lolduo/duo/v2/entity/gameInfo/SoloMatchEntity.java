package com.lolduo.duo.v2.entity.gameInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_match",indexes = {
        @Index(name="date_index",columnList = "date"),
        @Index(name="multi_index",columnList = "position, champion_id, main_rune",unique = true),
        @Index(name="solo_match_index",columnList = "all_count,win_rate desc, position, champion_id")})
public class SoloMatchEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "position")
    private String position;
    @Column(name = "champion_id")
    private Long championId;
    @Column(name = "main_rune")
    private Long mainRune;
    @Column(name = "all_count")
    private Long allCount;
    @Column(name = "win_count")
    private Long winCount;
    @Column(name = "win_rate")
    private Double winRate;
    @Column(name = "solo_comb_id")
    private Long soloCombId;

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public void setWinCount(Long winCount) {
        this.winCount = winCount;
    }

    public void setWinRate(Double winRate) { this.winRate = winRate; }

    public SoloMatchEntity(LocalDate date, String position, Long championId, Long mainRune, Long allCount, Long winCount, Double winRate, Long soloCombId) {
        this.date = date;
        this.position = position;
        this.championId = championId;
        this.mainRune = mainRune;
        this.allCount = allCount;
        this.winCount = winCount;
        this.winRate = winRate;
        this.soloCombId = soloCombId;
    }
}
