package com.lolduo.duo.entity.gameInfo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_match", indexes = {@Index(name = "champion_id_index",columnList = "champion_id"),@Index(name = "position_index",columnList = "position"),@Index(name = "main_rune_index",columnList = "main_rune") })
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

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }

    public void setWinCount(Long winCount) {
        this.winCount = winCount;
    }

    public SoloMatchEntity(LocalDate date, String position, Long championId, Long mainRune, Long allCount, Long winCount) {
        this.date = date;
        this.position = position;
        this.championId = championId;
        this.mainRune = mainRune;
        this.allCount = allCount;
        this.winCount = winCount;
    }
}
