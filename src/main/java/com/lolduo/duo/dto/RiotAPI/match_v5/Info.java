package com.lolduo.duo.dto.RiotAPI.match_v5;

import lombok.Getter;

import java.util.List;

@Getter
public class Info {
    private Long gameCreation;
    private Long gameDuration;
    private Long gameEndTimestamp;
    private Long gameId;
    private String gameMode;
    private String gameName;
    private Long gameStartTimestamp;
    private String gameType;
    private String gameVersion;
    private Long mapId;
    private List<Participant> participants;
    private String platformId;
    private Long queueId;
    private List<Team> teams;
    private String tournamentCode;

}
