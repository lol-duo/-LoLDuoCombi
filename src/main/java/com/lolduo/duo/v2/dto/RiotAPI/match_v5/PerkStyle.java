package com.lolduo.duo.v2.dto.RiotAPI.match_v5;

import lombok.Getter;

import java.util.List;

@Getter
public class PerkStyle {
    private String description;
    private List<PerkStyleSelection> selections;
    private Long style;

}
