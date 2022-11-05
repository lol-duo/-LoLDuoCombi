package com.lolduo.duo.v2.dto.RiotAPI.match_v5;

import lombok.Getter;

import java.util.List;

@Getter
public class Team {
    private List<Ban> bans;
    private Objectives objectives;
    private Long teamId;
    private Boolean win;
}
