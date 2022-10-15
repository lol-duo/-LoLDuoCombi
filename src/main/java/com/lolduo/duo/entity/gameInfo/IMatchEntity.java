package com.lolduo.duo.entity.gameInfo;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public interface IMatchEntity {
    public Long getId();
    public LocalDate getDate() ;
    public String getTier() ;
    public Boolean getWin() ;
    public Map<Long, String> getPositionMap() ;
    public Map<Long, List<Long>> getItemListMap() ;
    public Map<Long, TreeSet<Long>> getSpellListMap();
    public TreeSet<Long> getChampionList();
    public Map<Long, List<Long>> getPerkListMap() ;

}
