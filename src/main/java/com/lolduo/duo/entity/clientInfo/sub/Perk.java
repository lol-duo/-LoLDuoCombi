package com.lolduo.duo.entity.clientInfo.sub;

import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
@Getter
public class Perk extends Sub {
    private Map<Long, List<Long>> perkMap;

    public Perk(Map<Long, List<Long>> perkMap, Long win,Long allCount) {
        super(win,allCount);
        this.perkMap = perkMap;
    }
}
