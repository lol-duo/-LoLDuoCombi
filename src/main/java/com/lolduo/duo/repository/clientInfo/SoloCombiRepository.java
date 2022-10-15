package com.lolduo.duo.repository.clientInfo;

import com.lolduo.duo.entity.clientInfo.DoubleCombiEntity;
import com.lolduo.duo.entity.clientInfo.SoloCombiEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SoloCombiRepository extends JpaRepository<SoloCombiEntity,Long> , ICombiRepository {
    @Query(value = "select * from solo_combi where json_contains(champion_id,?1) and json_contains(position,?2) order by win_count / all_count DESC limit 30",nativeQuery = true)
    List<SoloCombiEntity> findAllByChampionIdAndPositionDesc(String championId, String position, String positionList, String excludePositionList);

    @Query(value = "select * from solo_combi where json_contains(champion_id,?1) and json_contains(position,?2) order by win_count / all_count ASC limit 30",nativeQuery = true)
    List<SoloCombiEntity> findAllByChampionIdAndPositionAsc(String championId, String position, String positionList, String excludePositionList);

    @Query(value = "select * from solo_combi where json_contains(champion_id,?1) and json_contains(position,?2) and perk_myth_item = ?3",nativeQuery = true)
    Optional<SoloCombiEntity> findByChampionIdAndPositionAndPerkMythItem(String championId, String position, String perkMythItem);
}