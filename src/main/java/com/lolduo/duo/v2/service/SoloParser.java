package com.lolduo.duo.v2.service;

import com.lolduo.duo.v2.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.v2.dto.RiotAPI.match_v5.PerkStyle;
import com.lolduo.duo.v2.entity.detail.*;
import com.lolduo.duo.v2.entity.front.DoubleMatchFrontEntity;
import com.lolduo.duo.v2.entity.front.SoloMatchFrontEntity;
import com.lolduo.duo.v2.entity.gameInfo.DoubleMatchEntity;
import com.lolduo.duo.v2.entity.gameInfo.SoloMatchEntity;
import com.lolduo.duo.v2.entity.initialInfo.ChampionEntity;
import com.lolduo.duo.v2.entity.initialInfo.PerkEntity;
import com.lolduo.duo.v2.repository.detail.*;
import com.lolduo.duo.v2.repository.front.DoubleMatchFrontRepository;
import com.lolduo.duo.v2.repository.front.SoloMatchFrontRepository;
import com.lolduo.duo.v2.repository.gameInfo.DoubleMatchRepository;
import com.lolduo.duo.v2.repository.gameInfo.SoloMatchRepository;
import com.lolduo.duo.v2.repository.initialInfo.ChampionRepository;
import com.lolduo.duo.v2.repository.initialInfo.PerkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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
    private final DoubleMatchDetailRepository doubleMatchDetailRepository;

    private final SoloMatchRepository soloMatchRepository;
    private final DoubleMatchRepository doubleMatchRepository;

    private final SoloMatchFrontRepository soloMatchFrontRepository;
    private final DoubleMatchFrontRepository doubleMatchFrontRepository;

    private final PerkRepository perkRepository;
    private final ChampionRepository championRepository;

    private final Long PICK_RATE_CONSTANT = 200L;
    private final String FILE_EXTENSION =".svg";
    private final String cloudFrontBaseUrl ="https://d2d4ci5rabfoyr.cloudfront.net";

    public void updateSoloMatchFront(int number){
        Long MINIMUM_NUMBER;
        if(number == 1){
            MINIMUM_NUMBER =soloMatchRepository.getAllCountSum().orElse(200L)/PICK_RATE_CONSTANT;
            getSoloMatchFrontListWhereAllCountIsEnough(MINIMUM_NUMBER);
        }
        else if(number == 2) {
            MINIMUM_NUMBER = doubleMatchRepository.getAllCountSum().orElse(200L)/PICK_RATE_CONSTANT;
            getDoubleMatchFrontListWhereAllCountIsEnough(MINIMUM_NUMBER);
        }
    }
    private void getSoloMatchFrontListWhereAllCountIsEnough(Long allCount){
        List<SoloMatchEntity> soloMatchEntityList = soloMatchRepository.findAllByAllCount(allCount);
        if(soloMatchEntityList == null || soloMatchEntityList.size()==0)
            log.info("allCount : {} solo_match 값존재 x",allCount);
        for(SoloMatchEntity soloMatchEntity : soloMatchEntityList){
            SoloMatchFrontEntity soloMatchFrontEntity;
            soloMatchFrontEntity = soloMatchFrontRepository.findBySoloMatchId(soloMatchEntity.getId()).orElse(null);
            if(soloMatchFrontEntity == null)
                soloMatchFrontEntity = parseToSoloMatchEntityToFront(soloMatchEntity);
            else
                soloMatchFrontEntity.setWinRate((long)(((double)soloMatchEntity.getWinCount()/soloMatchEntity.getAllCount()) * 10000));
            soloMatchFrontRepository.save(soloMatchFrontEntity);
        }
        log.info("getSoloMatchFrontListWhereAllCountIsEnough end !!");
    }
    private SoloMatchFrontEntity parseToSoloMatchEntityToFront(SoloMatchEntity soloMatchEntity){
        String[] championInfo = getChampionInfoUrlByChampionId(soloMatchEntity.getChampionId());
        String championName = championInfo[0];
        String championImgUrl = championInfo[1];
        String mainRuneImgUrl = getRuneImgUrlByRuneId(soloMatchEntity.getMainRune(),true);
        String positionImgUrl = cloudFrontBaseUrl + "/mainPage/position/" + soloMatchEntity.getPosition() + FILE_EXTENSION;
        String position = soloMatchEntity.getPosition();
        Long championId = soloMatchEntity.getChampionId();
        Long mainRune = soloMatchEntity.getMainRune();
        Long winRate = (long)(((double)soloMatchEntity.getWinCount()/soloMatchEntity.getAllCount()) * 10000);
        return new SoloMatchFrontEntity(soloMatchEntity.getId(),championName,championImgUrl,mainRuneImgUrl,positionImgUrl,position,championId,mainRune,winRate);
    }
    private DoubleMatchFrontEntity parseToDoubleMatchEntityToFront(DoubleMatchEntity doubleMatchEntity){
        String[] championInfo1 = getChampionInfoUrlByChampionId(doubleMatchEntity.getChampionId1());
        String[] championInfo2 = getChampionInfoUrlByChampionId(doubleMatchEntity.getChampionId2());
        String championName1 = championInfo1[0];
        String championName2 = championInfo2[0];
        String championImgUrl1 = championInfo1[1];
        String championImgUrl2 = championInfo2[1];
        String mainRuneImgUrl1 = getRuneImgUrlByRuneId(doubleMatchEntity.getMainRune1(),true);
        String mainRuneImgUrl2 = getRuneImgUrlByRuneId(doubleMatchEntity.getMainRune2(),true);
        String positionImgUrl1 = cloudFrontBaseUrl + "/mainPage/position/" + doubleMatchEntity.getPosition1() + FILE_EXTENSION;
        String positionImgUrl2 = cloudFrontBaseUrl + "/mainPage/position/" + doubleMatchEntity.getPosition2() + FILE_EXTENSION;
        String position1 = doubleMatchEntity.getPosition1();
        String position2 = doubleMatchEntity.getPosition2();
        Long championId1 = doubleMatchEntity.getChampionId1();
        Long championId2 = doubleMatchEntity.getChampionId2();
        Long mainRune1 = doubleMatchEntity.getMainRune1();
        Long mainRune2 = doubleMatchEntity.getMainRune2();
        Long winRate = (long)(((double)doubleMatchEntity.getWinCount()/doubleMatchEntity.getAllCount()) * 10000);
        return new DoubleMatchFrontEntity(doubleMatchEntity.getId(),championName1,championName2,championImgUrl1,championImgUrl2,mainRuneImgUrl1,mainRuneImgUrl2,positionImgUrl1,positionImgUrl2,position1,position2,championId1,championId2,mainRune1,mainRune2,winRate);

    }
    private void getDoubleMatchFrontListWhereAllCountIsEnough(Long allCount){
        List<DoubleMatchEntity> doubleMatchEntityList = doubleMatchRepository.findAllByAllCount(allCount);
        if(doubleMatchEntityList ==null || doubleMatchEntityList.size()==0)
            log.info("allCount : {} double_match 값 존재 x",allCount);
        for(DoubleMatchEntity doubleMatchEntity : doubleMatchEntityList){
            DoubleMatchFrontEntity doubleMatchFrontEntity;
            doubleMatchFrontEntity = doubleMatchFrontRepository.findByDoubleMatchId(doubleMatchEntity.getId()).orElse(null);
            if(doubleMatchFrontEntity == null)
                doubleMatchFrontEntity = parseToDoubleMatchEntityToFront(doubleMatchEntity);
            else
                doubleMatchFrontEntity.setWinRate((long)(((double)doubleMatchEntity.getWinCount()/doubleMatchEntity.getAllCount()) *10000));
            doubleMatchFrontRepository.save(doubleMatchFrontEntity);
        }
        log.info("getDoubleMatchFrontListWhereAllCountIsEnough end !!");
    }
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
                soloMatchDetailEntity = new SoloMatchDetailEntity(soloCombId,itemCombId,runeCombId,spellCombId,1L,1L,((double)1L/1L));
            else
                soloMatchDetailEntity = new SoloMatchDetailEntity(soloCombId,itemCombId,runeCombId,spellCombId,0L,1L,((double)0L/1L));
        }
        else{
            if(win)
                soloMatchDetailEntity.setWinCount(soloMatchDetailEntity.getWinCount()+1);
            soloMatchDetailEntity.setAllCount(soloMatchDetailEntity.getAllCount()+1);
            soloMatchDetailEntity.setWinRate(((double)soloMatchDetailEntity.getWinCount()/soloMatchDetailEntity.getAllCount()));
        }
        log.info("SoloMatchDetailEntity save, soloCombId : {}, winCount : {}, allCount : {}, itemCombId: {}, runeCombId: {}, spellCombId: {}",
                soloMatchDetailEntity.getSoloCombId(),soloMatchDetailEntity.getWinCount(), soloMatchDetailEntity.getAllCount(), soloMatchDetailEntity.getItemCombId(),soloMatchDetailEntity.getRuneCombId(),soloMatchDetailEntity.getSpellCombId() );
        return soloMatchDetailRepository.save(soloMatchDetailEntity);
    }
    public DoubleMatchDetailEntity toDoubleMatchDetailComb(boolean win, Long championCombId1,Long itemCombId1,Long runeCombId1,Long spellCombId1,Long championCombId2,Long itemCombId2,Long runeCombId2,Long spellCombId2){
        DoubleMatchDetailEntity doubleMatchDetailEntity;
        doubleMatchDetailEntity = doubleMatchDetailRepository.findByChampionCombId1AndItemCombId1AndRuneCombId1AndSpellCombId1AndChampionCombId2AndItemCombId2AndRuneCombId2AndSpellCombId2(championCombId1,itemCombId1,runeCombId1,spellCombId1,championCombId2,itemCombId2,runeCombId2,spellCombId2).orElse(null);
        if(doubleMatchDetailEntity ==null){
            if(win)
                doubleMatchDetailEntity = new DoubleMatchDetailEntity(championCombId1,itemCombId1,runeCombId1,spellCombId1,championCombId2,itemCombId2,runeCombId2,spellCombId2,1L,1L,((double)1L/1L));
            else
                doubleMatchDetailEntity = new DoubleMatchDetailEntity(championCombId1,itemCombId1,runeCombId1,spellCombId1,championCombId2,itemCombId2,runeCombId2,spellCombId2,0L,1L,((double)0L/1L));
        }
        else{
            if(win)
                doubleMatchDetailEntity.setWinCount(doubleMatchDetailEntity.getWinCount()+1);
            doubleMatchDetailEntity.setAllCount(doubleMatchDetailEntity.getAllCount()+1);
            doubleMatchDetailEntity.setWinRate( ((double)doubleMatchDetailEntity.getWinCount()/doubleMatchDetailEntity.getAllCount()) );
        }
        log.info("DoubleMatchDetailEntity save, championCombId1 : {},itemCombId1 : {}, runeCombId1 : {}, spellCombId1 : {} \n" +
                "championCombId2 : {}, itemCombId2 : {}, runeCombId2 : {}, spellCombId2 : {}, winCount : {}, allCount : {}, winRate : {} ",
                doubleMatchDetailEntity.getChampionCombId1(),doubleMatchDetailEntity.getItemCombId1(),doubleMatchDetailEntity.getRuneCombId1(),doubleMatchDetailEntity.getSpellCombId1(),
                doubleMatchDetailEntity.getChampionCombId2(),doubleMatchDetailEntity.getItemCombId2(),doubleMatchDetailEntity.getRuneCombId2(),doubleMatchDetailEntity.getSpellCombId2(),
                doubleMatchDetailEntity.getWinCount(),doubleMatchDetailEntity.getAllCount(),doubleMatchDetailEntity.getWinRate());
        return doubleMatchDetailRepository.save(doubleMatchDetailEntity);
    }
    public SoloMatchEntity toSoloMatch(boolean win, Long soloCombId, Participant participant,Long creationTimeStamp){
        LocalDate matchDate = LocalDate.ofInstant(Instant.ofEpochMilli(creationTimeStamp), ZoneId.of("Asia/Seoul"));
        String position = participant.getTeamPosition();
        Long championId = participant.getChampionId();
        Long mainRune = getMainRune(participant);
        SoloMatchEntity soloMatchEntity;
        soloMatchEntity = soloMatchRepository.findByPositionAndChampionIdAndMainRune(position,championId,mainRune).orElse(null);
        if(soloMatchEntity == null) {
            soloMatchEntity = new SoloMatchEntity(matchDate,position,championId,mainRune,1L,win ? 1L : 0L,win ? ((double)1L/1L) : ((double)0L/1L),soloCombId );
        }
        else{
            if(win){
                soloMatchEntity.setWinCount(soloMatchEntity.getWinCount()+1);
            }
            soloMatchEntity.setAllCount(soloMatchEntity.getAllCount()+1);
            soloMatchEntity.setWinRate(((double)soloMatchEntity.getWinCount()/soloMatchEntity.getAllCount()));
        }
        return soloMatchRepository.save(soloMatchEntity);
    }
    public DoubleMatchEntity toDoubleMatch(boolean win,Long[] doubleCombIdArr,List<Participant> participantList ,Long creationTimeStamp){
        LocalDate matchDate = LocalDate.ofInstant(Instant.ofEpochMilli(creationTimeStamp), ZoneId.of("Asia/Seoul"));
        String[] positionArr = new String[2];
        Long[] championIdArr = new Long[2];
        Long[] mainRuneArr = new Long[2];
        for(int i = 0 ; i < 2;i++){
            positionArr[i] = participantList.get(i).getTeamPosition();
            championIdArr[i] = participantList.get(i).getChampionId();
            mainRuneArr[i] = getMainRune(participantList.get(i));
        }
        if(championIdArr[0] > championIdArr[1]){
            swapChampionInfo(positionArr,championIdArr,mainRuneArr,doubleCombIdArr);
        }
        DoubleMatchEntity doubleMatchEntity = doubleMatchRepository.findByPosition1AndPosition2AndChampionId1AndChampionId2AndMainRune1AndMainRune2(positionArr[0],positionArr[1],championIdArr[0],championIdArr[1],mainRuneArr[0],mainRuneArr[1]).orElse(null);
        if(doubleMatchEntity == null){
            doubleMatchEntity = new DoubleMatchEntity(matchDate,positionArr[0],positionArr[1],championIdArr[0],championIdArr[1],mainRuneArr[0],mainRuneArr[1],1L,win ? 1L : 0L, win ? ((double)1L/1L) : ((double)0L/1L), doubleCombIdArr[0],doubleCombIdArr[1]);
        }
        else{
            if(win){
                doubleMatchEntity.setWinCount(doubleMatchEntity.getWinCount()+1);
            }
            doubleMatchEntity.setAllCount(doubleMatchEntity.getAllCount()+1);
            doubleMatchEntity.setWinRate(((double)doubleMatchEntity.getWinCount()/doubleMatchEntity.getAllCount()) );
        }
        return doubleMatchRepository.save(doubleMatchEntity);
    }
    public Long getMainRune(Participant participant){
        Long MainRune = 0L;
        for (PerkStyle perkStyle : participant.getPerks().getStyles()) {
            if (perkStyle.getDescription().equals("primaryStyle")) {
                MainRune = perkStyle.getSelections().get(0).getPerk();
                break;
            }
        }
        return MainRune;
    }
    private void swapChampionInfo(String[] positionArr,Long[] championIdArr, Long[] mainRuneArr,Long[] doubleCombIdArr){
        if(championIdArr[0] > championIdArr[1]){
            String tempPo = positionArr[0];
            positionArr[0] = positionArr[1];
            positionArr[1] = tempPo;

            Long tempId = championIdArr[0];
            championIdArr[0] = championIdArr[1];
            championIdArr[1] = tempId;

            Long tempRu = mainRuneArr[0];
            mainRuneArr[0] = mainRuneArr[1];
            mainRuneArr[1] = tempRu;

            Long tempCo = doubleCombIdArr[0];
            doubleCombIdArr[0] = doubleCombIdArr[1];
            doubleCombIdArr[1] =tempCo;
        }
    }

    private String getRuneImgUrlByRuneId(Long runeId,boolean isActive){
        String perkImgUrl ="";
        PerkEntity perkEntity = perkRepository.findById(runeId).orElse(null);
        if(perkEntity ==null)
            log.info("perk_id 테이블에서 스펠을 찾을 수 없습니다. perk_id 테이블을 확인해주세요.  요청 id: {}",runeId);
        else {
            if(isActive)
                perkImgUrl = cloudFrontBaseUrl + "/Rune/" + perkEntity.getImgUrl() + FILE_EXTENSION;
            else
                perkImgUrl = cloudFrontBaseUrl + "/Rune/" + perkEntity.getImgUrl() +"Disabled"+ FILE_EXTENSION;
        }
        return perkImgUrl;
    }
    private String[] getChampionInfoUrlByChampionId(Long championId){
        String[] championInfoArr = new String[2];
        championInfoArr[0] = "NoName";
        championInfoArr[1] = "NoImg";
        ChampionEntity championEntity = championRepository.findById(championId).orElse(null);
        if (championEntity == null)
            log.info("챔피언 테이블에서 챔피언을 찾을 수 없습니다. champion 테이블을 확인해주세요.  championId: {}", championId);
        else {
            championInfoArr[0] = championEntity.getName();
            championInfoArr[1] = cloudFrontBaseUrl + "/champion/" + championEntity.getImgUrl() + FILE_EXTENSION;

        }

        return championInfoArr;
    }
}
