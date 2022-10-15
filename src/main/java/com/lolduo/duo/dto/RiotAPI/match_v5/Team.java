package com.lolduo.duo.dto.RiotAPI.match_v5;

import lombok.Getter;

import java.util.List;

@Getter
public class Team {
    private List<Ban> bans;
    private Objectives objectives;
    private Long teamId;
    private Boolean win;
}
