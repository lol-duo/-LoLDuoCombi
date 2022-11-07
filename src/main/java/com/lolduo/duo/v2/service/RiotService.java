package com.lolduo.duo.v2.service;


import com.lolduo.duo.v2.dto.RiotAPI.match_v5.MatchDto;
import com.lolduo.duo.v2.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.v2.dto.RiotAPI.match_v5.PerkStyle;
import com.lolduo.duo.v2.dto.RiotAPI.timeline.MatchTimeLineDto;
import com.lolduo.duo.v2.entity.MatchDetailEntity;
import com.lolduo.duo.v2.entity.PositionNullMatchIdEntity;
import com.lolduo.duo.v2.entity.detail.ItemCombEntity;
import com.lolduo.duo.v2.entity.detail.RuneCombEntity;
import com.lolduo.duo.v2.entity.detail.SoloChampionCombEntity;
import com.lolduo.duo.v2.entity.detail.SpellCombEntity;
import com.lolduo.duo.v2.entity.gameInfo.DoubleMatchEntity;
import com.lolduo.duo.v2.entity.gameInfo.SoloMatchEntity;
import com.lolduo.duo.v2.entity.initialInfo.ItemFullEntity;
import com.lolduo.duo.v2.repository.MatchDetailRepository;
import com.lolduo.duo.v2.repository.PositionNullMatchIdRepository;
import com.lolduo.duo.v2.repository.detail.ItemCombRepository;
import com.lolduo.duo.v2.repository.detail.RuneCombRepository;
import com.lolduo.duo.v2.repository.detail.SoloChampionCombRepository;
import com.lolduo.duo.v2.repository.detail.SpellCombRepository;
import com.lolduo.duo.v2.repository.gameInfo.DoubleMatchRepository;
import com.lolduo.duo.v2.repository.gameInfo.SoloMatchRepository;
import com.lolduo.duo.v2.repository.initialInfo.ItemFullRepository;
import com.lolduo.duo.v2.service.slack.SlackNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RiotService implements ApplicationRunner{

    private final SoloMatchRepository soloMatchRepository;
    private final DoubleMatchRepository doubleMatchRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final ItemFullRepository itemFullRepository;
    private final SlackNotifyService slackNotifyService;
    private final PositionNullMatchIdRepository positionNullMatchIdRepository;
    private final TimeCheckComponent timeCheckComponent;
    private final SoloParser soloParser;
    @Override
    public void run(ApplicationArguments args) throws Exception{
        //All();
        localTest();
    }
    private void localTest(){
        LocalDate localDate = LocalDate.parse("2022-11-05");
        makeMatchDetailV2(1,localDate);
        log.info("test end !!");
    }
    private void All(){
        LocalDate localDate = LocalDate.parse("2022-09-19");
        slackNotifyService.sendMessage(slackNotifyService.nowTime() + "에 시작하는 + " + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "일자 SoloInfo 만들기 Start");
        //makeMatchDetailV2(1,localDate);
        //makeMatchDetail(2,localDate);
        slackNotifyService.sendMessage(slackNotifyService.nowTime() + "에 " + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "일자 SoloInfo 만들기 End");
    }
    private void savePositionNullMatch(MatchDetailEntity matchDetailEntity){
        PositionNullMatchIdEntity positionNullMatchIdEntity = new PositionNullMatchIdEntity(matchDetailEntity);
        positionNullMatchIdRepository.save(positionNullMatchIdEntity);
    }
    private Map<String,List<Long>> getParticipantsGreaterItem3(MatchTimeLineDto matchTimeLineDto){
        Map<String,List<Long>> participantsItemMap = new HashMap<>();
        List<com.lolduo.duo.v2.dto.RiotAPI.timeline.Participant> participantList = matchTimeLineDto.getInfo().getParticipants();
        List<ItemFullEntity> itemFullEntityList ;
        itemFullEntityList = itemFullRepository.findAll();
        List<Long> itemFullList = new ArrayList<>();
        itemFullEntityList.forEach(itemFullEntity -> {
            itemFullList.add(itemFullEntity.getId());
        });
        for(int i = 0; i<10;i++){
            participantsItemMap.put(participantList.get(i).getPuuid(),new ArrayList<>());
        }
        matchTimeLineDto.getInfo().getFrames().forEach(frame -> {

            frame.getEvents().forEach(event -> {
                if(event.getType().equals("ITEM_PURCHASED") &&itemFullList.contains(event.getItemId())){
                    participantsItemMap.get(matchTimeLineDto.getInfo().getParticipants().get(Math.toIntExact(event.getParticipantId())-1).getPuuid()).add(event.getItemId());
                }
            });
        });
        for(int i = 0; i<10;i++){
            if(participantsItemMap.get(participantList.get(i).getPuuid()).size() < 3){
                participantsItemMap.remove(participantList.get(i).getPuuid());
            }
        }
        return participantsItemMap;
    }
    private boolean isParticipantHaveItem3(Participant participant,Map<String,List<Long>> ParticipantsItemMap){
        return ParticipantsItemMap.containsKey(participant.getPuuid());
    }
    private void makeMatchDetailV2(int number,LocalDate date){
        log.info("parameter date : " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) );
        String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        long matchSize = matchDetailRepository.findSizeByDate(dateString).orElse(0L);
        long start = 0L;
        log.info("makeMatchDetailV2 function matchSize : " + matchSize );
        long makeMatchDetailV2Time = System.currentTimeMillis();
        while(start < matchSize) {
            Long time = System.currentTimeMillis();
            List<MatchDetailEntity> matchDetailEntityList = matchDetailRepository.findAllByDate(dateString,start);
            log.info( "makeMatchDetailV2 - processing, " + start+ " / " + matchSize);

            for (MatchDetailEntity matchDetailEntity : matchDetailEntityList) {
                final Long startTime = System.currentTimeMillis();
                MatchDto matchInfo = matchDetailEntity.getMatchInfo();
                MatchTimeLineDto matchTimeLineDto = matchDetailEntity.getMatchTimeLineDto();
                Map<String, Boolean> visitedWin = new HashMap<>();
                Map<String, Boolean> visitedLose = new HashMap<>();

                Long itemGreaterThan3StartTime = System.currentTimeMillis();
                Map<String, List<Long>> participantsItemMap = getParticipantsGreaterItem3(matchTimeLineDto);
                List<Participant> participants = new ArrayList<>();
                for (Participant participant : matchInfo.getInfo().getParticipants()) {
                    if (isParticipantHaveItem3(participant, participantsItemMap)) {
                        participants.add(participant);
                    }
                }
                log.info(start +"번째 아이템 3개 이상 participant 만들기   spent time : " + (System.currentTimeMillis() - itemGreaterThan3StartTime));
                Long visitedStartTime = System.currentTimeMillis();
                participants.forEach(participant -> {
                    if (participant.getTeamPosition().equals("")) {
                        savePositionNullMatch(matchDetailEntity);
                    } else {
                        if (participant.getWin()) {
                            visitedWin.put(participant.getPuuid(), false);
                        } else {
                            visitedLose.put(participant.getPuuid(), false);
                        }
                    }
                });
                log.info(start +"번째 visied Arr 만들기   spent time : " + (System.currentTimeMillis() - visitedStartTime));
                if (number == 1) {
                    Long iterationStartTime = System.currentTimeMillis();
                    iteration(matchInfo, participantsItemMap, new ArrayList<>(), visitedWin, true);
                    log.info(start +"번째 win iteration   spent time : " + (System.currentTimeMillis() - iterationStartTime));
                    iterationStartTime = System.currentTimeMillis();
                    iteration(matchInfo, participantsItemMap, new ArrayList<>(), visitedLose, false);
                    log.info(start +"번째 lose iteration   spent time : " + (System.currentTimeMillis() - iterationStartTime));
                } else if (number == 2) {
                    combination(matchInfo, participantsItemMap, new ArrayList<>(), visitedWin, true, number, 0);
                    combination(matchInfo, participantsItemMap, new ArrayList<>(), visitedLose, false, number, 0);
                }
                log.info(start +"번째 makeMatchDetail  spent time : " + (System.currentTimeMillis() - startTime));
                start++;
            }
            if(start % 1000 == 0){
                slackNotifyService.sendMessage( "number = "+number + " makeMatchDetail processing, " + start+ " / " + matchSize );
            }
            if(start == 60000){
                break;
            }
            log.info(start + " time check end : " + (System.currentTimeMillis() - time) );
        }
        log.info("makeMatchDetailV2Time spent Time : " + (System.currentTimeMillis()-makeMatchDetailV2Time) +"ms" );
    }
    /*
    private void makeMatchDetail(int number,LocalDate date){
        log.info("parameter date : " + date.format(DateTimeFormatter.ISO_LOCAL_DATE) );
        String dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE);
        long matchSize = matchDetailRepository.findSizeByDate(dateString).orElse(0L);
        long start = 0L;
        log.info("makeMatchDetail function matchSize : " + matchSize );
        while(start < matchSize) {
            Long time = System.currentTimeMillis();
            log.info(start + " time check start: " );
            List<MatchDetailEntity> matchDetailEntityList = matchDetailRepository.findAllByDate(dateString,start);
            log.info( "makeMatchDetail - processing, " + start+ " / " + matchSize);
            matchDetailEntityList.forEach(matchDetailEntity -> {
                final Long startTime = System.currentTimeMillis();

                MatchDto matchInfo = matchDetailEntity.getMatchInfo();

                Map<String, Boolean> visitedWin = new HashMap<>();
                Map<String, Boolean> visitedLose = new HashMap<>();

                matchInfo.getInfo().getParticipants().forEach(participant -> {
                    if(participant.getTeamPosition().equals("")){
                        savePositionNullMatch(matchDetailEntity);
                        return;
                    }
                    else {
                        if (participant.getWin()) {
                            visitedWin.put(participant.getPuuid(), false);
                        } else {
                            visitedLose.put(participant.getPuuid(), false);
                        }
                    }
                });
                if(number==1){
                    iteration(matchInfo,new ArrayList<>(),visitedWin,true);
                    iteration(matchInfo,new ArrayList<>(),visitedLose,false);
                }
                else if(number==2) {
                    combination(matchInfo, new ArrayList<>(), visitedWin, true, number, 0);
                    combination(matchInfo, new ArrayList<>(), visitedLose, false, number, 0);
                }
                log.info("makeMatchDetail Spent Time : " +  (System.currentTimeMillis() - startTime));
            });
            start +=100;
            if(start % 1000 == 0){
                slackNotifyService.sendMessage( "number = "+number + " makeMatchDetail processing, " + start+ " / " + matchSize );
            }
            log.info(start + " time check end : " + (System.currentTimeMillis() - time) );
            if(start == 60000){
                break;
            }
        }

    }
     */
    private void iteration(MatchDto matchInfo,Map<String,List<Long>> participantsItemMap, List<Participant> participantList,Map<String,Boolean> visited,Boolean win){
        for(int i = 0 ; i < matchInfo.getInfo().getParticipants().size();i++){
            Participant participant = matchInfo.getInfo().getParticipants().get(i);
            if(visited.containsKey(participant.getPuuid())) {
                participantList.add(participant);
                saveMatch(participantList,participantsItemMap, win, 1, matchInfo.getInfo().getGameCreation());
                participantList.remove(participant);
            }
        }
    }
    private void combination(MatchDto matchInfo,Map<String,List<Long>> participantsItemMap, List<Participant> participantList,Map<String,Boolean> visited,Boolean win,int number,int start){
        if(participantList.size()==number){
            timeCheckComponent.checkStart();
            saveMatch(participantList,participantsItemMap,win,number, matchInfo.getInfo().getGameCreation());
            log.info("saveMatch : " + timeCheckComponent.checkEnd());
            return;
        }

        Long startTime = System.currentTimeMillis();
        for(int i = start; i< matchInfo.getInfo().getParticipants().size(); i++){
            Participant participant = matchInfo.getInfo().getParticipants().get(i);
            if (visited.containsKey(participant.getPuuid()) && !visited.get(participant.getPuuid())) {
                participantList.add(participant);
                visited.put(participant.getPuuid(),true);
                combination(matchInfo,participantsItemMap,participantList,visited,win,number,i+1);
                participantList.remove(participant);
                visited.put(participant.getPuuid(),false);
            }
        }
        log.info("combination Logic Spent Time : " + (System.currentTimeMillis() - startTime) );
    }
    private void saveMatch(List<Participant> participantList, Map<String,List<Long>> ParticipantsItemMap,Boolean win, int number, Long creationTimeStamp){
        if(number==1){
            //Long time = System.currentTimeMillis();
            saveSoloMatch(participantList,ParticipantsItemMap,win,creationTimeStamp);
            //log.info("saveMatch time check : " +(System.currentTimeMillis() - time) + "ms");
        }
        else if(number==2){
            saveDoubleMatch(participantList,win,creationTimeStamp);
        }
    }

    private void saveSoloMatch(List<Participant> participantList,Map<String,List<Long>> participantsItemMap, Boolean win, Long creationTimeStamp){
        participantList.forEach(participant -> {

            SoloChampionCombEntity soloChampionCombEntity = soloParser.toSoloChampionComb(participant);
            ItemCombEntity itemCombEntity = soloParser.toItemComb(participant,participantsItemMap);
            RuneCombEntity runeCombEntity = soloParser.toRuneComb(participant);
            SpellCombEntity spellCombEntity = soloParser.toSpellComb(participant);

            soloParser.toSoloMatch(win,soloChampionCombEntity.getId(),participant,creationTimeStamp);

            soloParser.toSoloMatchDetailComb(win,soloChampionCombEntity.getId(),itemCombEntity.getId(),runeCombEntity.getId(),spellCombEntity.getId());

        });
    }
    private void saveDoubleMatch(List<Participant> participantList, Boolean win, Long creationTimeStamp){
        LocalDate matchDate = LocalDate.ofInstant(Instant.ofEpochMilli(creationTimeStamp), ZoneId.of("Asia/Seoul"));
        String[] positionArr = new String[2];
        Long[] championIdArr = new Long[2];
        Long[] mainRuneArr = new Long[2];
        for(int i = 0 ; i < 2;i++){
            positionArr[i] = participantList.get(i).getTeamPosition();
            championIdArr[i] = participantList.get(i).getChampionId();
            mainRuneArr[i] = soloParser.getMainRune(participantList.get(i));
        }
        if(championIdArr[0] > championIdArr[1]){
            swapChampionInfo(positionArr,championIdArr,mainRuneArr);
        }
        DoubleMatchEntity doubleMatchEntity = doubleMatchRepository.findByPosition1AndPosition2AndChampionId1AndChampionId2AndMainRune1AndMainRune2(positionArr[0],positionArr[1],championIdArr[0],championIdArr[1],mainRuneArr[0],mainRuneArr[1]).orElse(null);
        if(doubleMatchEntity == null){
            doubleMatchEntity = new DoubleMatchEntity(matchDate,positionArr[0],positionArr[1],championIdArr[0],championIdArr[1],mainRuneArr[0],mainRuneArr[1],1L,win ? 1L : 0L);
        }
        else{
            doubleMatchEntity.setAllCount(doubleMatchEntity.getAllCount()+1);
            if(win){
                doubleMatchEntity.setWinCount(doubleMatchEntity.getWinCount()+1);
            }
        }
        doubleMatchRepository.save(doubleMatchEntity);
    }
    private void swapChampionInfo(String[] positionArr,Long[] championIdArr, Long[] mainRuneArr){
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
        }
    }
}
