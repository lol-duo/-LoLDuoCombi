package com.lolduo.duo.v2.service;

import com.lolduo.duo.v2.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.v2.entity.detail.*;
import com.lolduo.duo.v2.repository.detail.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class SoloParser {
    private final SpellCombRepository spellCombRepository;
    private final ItemCombRepository itemCombRepository;
    private final RuneCombRepository runeCombRepository;
    private final SoloChampionCombRepository soloChampionCombRepository;
    private final SoloMatchDetailRepository soloMatchDetailRepository;

    public SpellCombEntity toSpellComb(Participant participant){
        List<Long> spellList = new ArrayList<>();
        spellList.add(participant.getSummoner1Id());
        spellList.add(participant.getSummoner2Id());
        Collections.sort(spellList);
        SpellCombEntity spellCombEntity;
        spellCombEntity = spellCombRepository.findBySpell1AndSpell2(spellList.get(0),spellList.get(1)).orElse(null);
        if(spellCombEntity ==null){
            spellCombEntity = new SpellCombEntity(spellList.get(0),spellList.get(1));
            log.info("spellCombEntity save, spell1 : {}, spell2: {}",spellCombEntity.getSpell1(),spellCombEntity.getSpell2());
            spellCombEntity = spellCombRepository.save(spellCombEntity);
        }
        return spellCombEntity;
    }
    public ItemCombEntity toItemComb(Participant participant,Map<String,List<Long>> ParticipantsItemMap) {
        List<Long> itemList =null;
        for(String puuid : ParticipantsItemMap.keySet()){
            if(puuid.equals(participant.getPuuid())){
                itemList = ParticipantsItemMap.get(puuid);
                break;
            }
        }
        ItemCombEntity itemCombEntity;
        itemCombEntity = itemCombRepository
                .findByItem1AndItem2AndItem3(itemList.get(0), itemList.get(1),itemList.get(2))
                .orElse(null);
        if(itemCombEntity ==null){
            itemCombEntity = new ItemCombEntity(itemList.get(0),itemList.get(1),itemList.get(2));
            log.info("ItemCombEntity save, item1 : {}, item2: {}, item3: {}",itemCombEntity.getItem1(),itemCombEntity.getItem2(),itemCombEntity.getItem3());
            itemCombEntity = itemCombRepository.save(itemCombEntity);
        }

        return itemCombEntity;
    }
    public RuneCombEntity toRuneComb(Participant participant){
        Long mainRuneConcept =  participant.getPerks().getStyles().get(0).getStyle();
        Long mainRune0 =  participant.getPerks().getStyles().get(0).getSelections().get(0).getPerk();
        Long subRuneConcept = participant.getPerks().getStyles().get(1).getStyle();

        Long mainRune1 = participant.getPerks().getStyles().get(0).getSelections().get(1).getPerk();
        Long mainRune2 = participant.getPerks().getStyles().get(0).getSelections().get(2).getPerk();
        Long mainRune3 = participant.getPerks().getStyles().get(0).getSelections().get(3).getPerk();

        List<Long> subRuneList = new ArrayList<>();
        subRuneList.add(participant.getPerks().getStyles().get(1).getSelections().get(0).getPerk());
        subRuneList.add(participant.getPerks().getStyles().get(1).getSelections().get(1).getPerk());
        Collections.sort(subRuneList);
        RuneCombEntity runeCombEntity;
        runeCombEntity = runeCombRepository
                .findByMainRuneConceptAndMainRune0AndSubRuneConceptAndMainRune1AndMainRune2AndMainRune3AndSubRune1AndSubRune2(
                        mainRuneConcept,mainRune0,subRuneConcept,
                        mainRune1,mainRune2,mainRune3,
                        subRuneList.get(0),subRuneList.get(1)
                ).orElse(null);
        if(runeCombEntity ==null) {
            runeCombEntity = new RuneCombEntity(mainRuneConcept,mainRune0,subRuneConcept,
                    mainRune1,mainRune2,mainRune3,
                    subRuneList.get(0),subRuneList.get(1));
            log.info("RuneCombEntity save, mainRuneConcept : {}, mainRune0: {}, subRuneConcept: {}, mainRune1: {} \n" +
                            ",mainRune2: {},mainRune3: {},subRune1: {},subRune2: {}",
                    runeCombEntity.getMainRune1(), runeCombEntity.getMainRune0(), runeCombEntity.getSubRuneConcept(),
                    runeCombEntity.getMainRune1(), runeCombEntity.getMainRune2(), runeCombEntity.getMainRune3(),
                    runeCombEntity.getSubRune1(), runeCombEntity.getSubRune2());
            runeCombEntity = runeCombRepository.save(runeCombEntity);
        }
        return runeCombEntity;
    }
    public SoloChampionCombEntity toSoloChampionComb(Participant participant){
        Long championId = participant.getChampionId();
        String position = participant.getTeamPosition();
        Long mainRune =participant.getPerks().getStyles().get(0).getSelections().get(0).getPerk();;
        SoloChampionCombEntity soloChampionCombEntity;
        soloChampionCombEntity = soloChampionCombRepository.
                findByChampionIdAndPositionAndMainRune(championId,position,mainRune).orElse(null);
        if(soloChampionCombEntity ==null) {
            soloChampionCombEntity = new SoloChampionCombEntity(championId, position, mainRune);
            log.info("SoloChampionCombEntity save, championId : {}, position: {}, mainRune: {}",
                    soloChampionCombEntity.getChampionId(), soloChampionCombEntity.getPosition(), soloChampionCombEntity.getMainRune());
            soloChampionCombEntity = soloChampionCombRepository.save(soloChampionCombEntity);
        }
        return soloChampionCombEntity;
    }

    public SoloMatchDetailEntity toSoloMatchDetailComb(boolean win, Long soloCombId, Long itemCombId,Long runeCombId,Long spellCombId){
        SoloMatchDetailEntity soloMatchDetailEntity;
        soloMatchDetailEntity = soloMatchDetailRepository.findBySoloCombIdAndItemCombIdAndRuneCombIdAndSpellCombId(
                soloCombId,itemCombId,runeCombId,spellCombId).orElse(null);
        if(soloMatchDetailEntity == null){
            if(win)
                soloMatchDetailEntity = new SoloMatchDetailEntity(soloCombId,1L,1L,itemCombId,runeCombId,spellCombId);
            else
                soloMatchDetailEntity = new SoloMatchDetailEntity(soloCombId,0L,1L,itemCombId,runeCombId,spellCombId);
        }
        else{
            if(win)
                soloMatchDetailEntity.setWinCount(soloMatchDetailEntity.getWinCount()+1);
            soloMatchDetailEntity.setAllCount(soloMatchDetailEntity.getAllCount()+1);

        }
        log.info("SoloMatchDetailEntity save, soloCombId : {}, winCount : {}, allCount : {}, itemCombId: {}, runeCombId: {}, spellCombId: {}",
                soloMatchDetailEntity.getSoloCombId(),soloMatchDetailEntity.getWinCount(), soloMatchDetailEntity.getAllCount(), soloMatchDetailEntity.getItemCombId(),soloMatchDetailEntity.getRuneCombId(),soloMatchDetailEntity.getSpellCombId() );
        return soloMatchDetailRepository.save(soloMatchDetailEntity);
    }
}
