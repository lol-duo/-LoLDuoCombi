package com.lolduo.duo.v2.entity;

import com.lolduo.duo.v2.dto.RiotAPI.match_v5.MatchDto;
import com.lolduo.duo.v2.dto.RiotAPI.timeline.MatchTimeLineDto;
import com.vladmihalcea.hibernate.type.json.JsonStringType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Table(name = "match_detail")
@Getter
@TypeDef(name = "json", typeClass = JsonStringType.class)
public class MatchDetailEntity {
    @Id
    @Column(name = "match_id")
    private String matchId;

    @Column(name = "date")
    private LocalDate date;

    @Type(type = "json")
    @Column(name = "match_info", columnDefinition = "json")
    private MatchDto matchInfo;

    @Type(type = "json")
    @Column(name = "match_timeline_info", columnDefinition = "json")
    private MatchTimeLineDto matchTimeLineDto;

    public MatchDetailEntity(String matchId, LocalDate date, MatchDto matchInfo,MatchTimeLineDto matchTimeLineDto) {
        this.matchId = matchId;
        this.date = date;
        this.matchInfo = matchInfo;
        this.matchTimeLineDto = matchTimeLineDto;
    }
}

