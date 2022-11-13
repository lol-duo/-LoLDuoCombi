package com.lolduo.duo.v2.entity.front;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "double_match_front")
public class DoubleMatchFrontEntity{
    @Id
    @Column(name = "double_match_id")
    private Long doubleMatchId;

    @Column(name = "champion_name1")
    private String championName1;
    @Column(name = "champion_name2")
    private String championName2;
    @Column(name = "champion_img_url1")
    private String championImgUrl1;
    @Column(name = "champion_img_url2")
    private String championImgUrl2;
    @Column(name = "main_rune_img_url1")
    private String mainRuneImgUrl1;
    @Column(name = "main_rune_img_url2")
    private String mainRuneImgUrl2;
    @Column(name = "position_img_url1")
    private String positionImgUrl1;
    @Column(name = "position_img_url2")
    private String positionImgUrl2;

    @Column(name = "position1")
    private String position1;
    @Column(name = "position2")
    private String position2;
    @Column(name = "champion_id1")
    private Long championId1;
    @Column(name = "champion_id2")
    private Long championId2;
    @Column(name = "main_rune1")
    private Long mainRune1;
    @Column(name = "main_rune2")
    private Long mainRune2;

    @Column(name = "win_rate")
    private Long winRate;

    public DoubleMatchFrontEntity(Long doubleMatchId, String championName1, String championName2, String championImgUrl1, String championImgUrl2, String mainRuneImgUrl1, String mainRuneImgUrl2, String positionImgUrl1, String positionImgUrl2, String position1, String position2, Long championId1, Long championId2, Long mainRune1, Long mainRune2, Long winRate) {
        this.doubleMatchId = doubleMatchId;
        this.championName1 = championName1;
        this.championName2 = championName2;
        this.championImgUrl1 = championImgUrl1;
        this.championImgUrl2 = championImgUrl2;
        this.mainRuneImgUrl1 = mainRuneImgUrl1;
        this.mainRuneImgUrl2 = mainRuneImgUrl2;
        this.positionImgUrl1 = positionImgUrl1;
        this.positionImgUrl2 = positionImgUrl2;
        this.position1 = position1;
        this.position2 = position2;
        this.championId1 = championId1;
        this.championId2 = championId2;
        this.mainRune1 = mainRune1;
        this.mainRune2 = mainRune2;
        this.winRate = winRate;
    }

    public void setWinRate(Long winRate) {
        this.winRate = winRate;
    }
}