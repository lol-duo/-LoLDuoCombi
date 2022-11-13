package com.lolduo.duo.v2.entity.front;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "solo_match_front")
public class SoloMatchFrontEntity {
    @Id
    @Column(name = "solo_match_id")
    private Long soloMatchId;

    @Column(name = "champion_name")
    private String championName;
    @Column(name = "champion_img_url")
    private String championImgUrl;
    @Column(name = "main_rune_img_url")
    private String mainRuneImgUrl;
    @Column(name = "position_img_url")
    private String positionImgUrl;

    @Column(name = "position")
    private String position;
    @Column(name = "champion_id")
    private Long championId;
    @Column(name = "main_rune")
    private Long mainRune;

    @Column(name = "win_rate")
    private Long winRate;


    public void setWinRate(Long winRate) {
        this.winRate = winRate;
    }

    public SoloMatchFrontEntity(Long soloMatchId, String championName, String championImgUrl, String mainRuneImgUrl, String positionImgUrl, String position, Long championId, Long mainRune, Long winRate) {
        this.soloMatchId = soloMatchId;
        this.championName = championName;
        this.championImgUrl = championImgUrl;
        this.mainRuneImgUrl = mainRuneImgUrl;
        this.positionImgUrl = positionImgUrl;
        this.position = position;
        this.championId = championId;
        this.mainRune = mainRune;
        this.winRate = winRate;
    }
}