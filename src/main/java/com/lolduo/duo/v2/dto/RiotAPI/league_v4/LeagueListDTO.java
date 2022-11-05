package com.lolduo.duo.v2.dto.RiotAPI.league_v4;

import lombok.Getter;

import java.util.List;

@Getter
public class LeagueListDTO {
    private String leagueId;
    private List<LeagueItem> entries;
    private String tier;
    private String name;
    private String queue;
}
