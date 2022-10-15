package com.lolduo.duo.entity;

import com.fasterxml.jackson.databind.JsonNode;
import com.lolduo.duo.dto.RiotAPI.match_v5.MatchDto;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@NoArgsConstructor
@Table(name = "match_detail")
@Getter
@TypeDef(name = "json", typeClass = JsonType.class,defaultForType = JsonNode.class)
public class MatchDetailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "date")
    private LocalDate date;

    @Type(type = "json")
    @Column(name = "match_info", columnDefinition = "json")
    private MatchDto matchInfo;

    @Type(type = "json")
    @Column(name = "item_list", columnDefinition = "json")
    private List<List<Long>> playerItemList = new ArrayList<>();

    @Type(type = "json")
    @Column(name = "puuid_list", columnDefinition = "json")
    private Map<String, Long> puuIdMap = new HashMap<>();

    @Column(name = "tier")
    private String tier;

    public MatchDetailEntity(LocalDate date, MatchDto matchInfo, List<List<Long>> playerItemList, Map<String, Long> puuIdMap, String tier) {
        this.date = date;
        this.matchInfo = matchInfo;
        this.playerItemList = playerItemList;
        this.puuIdMap = puuIdMap;
        this.tier = tier;
    }
}
