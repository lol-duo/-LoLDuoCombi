package com.lolduo.duo.service;


import com.lolduo.duo.dto.RiotAPI.match_v5.MatchDto;
import com.lolduo.duo.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.dto.RiotAPI.match_v5.PerkStyle;
import com.lolduo.duo.entity.MatchDetailEntity;
import com.lolduo.duo.entity.PositionNullMatchIdEntity;
import com.lolduo.duo.entity.gameInfo.DoubleMatchEntity;
import com.lolduo.duo.entity.gameInfo.SoloMatchEntity;
import com.lolduo.duo.repository.MatchDetailRepository;
import com.lolduo.duo.repository.PositionNullMatchIdRepository;
import com.lolduo.duo.repository.gameInfo.DoubleMatchRepository;
import com.lolduo.duo.repository.gameInfo.SoloMatchRepository;
import com.lolduo.duo.service.slack.SlackNotifyService;
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
    private final SlackNotifyService slackNotifyService;
    private final PositionNullMatchIdRepository positionNullMatchIdRepository;
    private final TimeCheckComponent timeCheckComponent;
    @Override
    public void run(ApplicationArguments args) throws Exception{
        All();
    }
    private void All(){
        LocalDate localDate = LocalDate.parse("2022-09-19");
        slackNotifyService.sendMessage(slackNotifyService.nowTime() + "에 시작하는 + " + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "일자 SoloInfo 만들기 Start");
        makeMatchDetail(1,localDate);
        makeMatchDetail(2,localDate);
        slackNotifyService.sendMessage(slackNotifyService.nowTime() + "에 " + localDate.format(DateTimeFormatter.ISO_LOCAL_DATE) + "일자 SoloInfo 만들기 End");

    }
    private void savePositionNullMatch(MatchDetailEntity matchDetailEntity){
        PositionNullMatchIdEntity positionNullMatchIdEntity = new PositionNullMatchIdEntity(matchDetailEntity);
        positionNullMatchIdRepository.save(positionNullMatchIdEntity);
    }
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
                combination(matchInfo, new ArrayList<>(), visitedWin, true, number, 0);
                combination(matchInfo, new ArrayList<>(), visitedLose, false, number, 0);
                log.info("makeMatchDetail Spent Time : " +  (System.currentTimeMillis() - startTime));
            });
            start +=100;
            if(start % 1000 == 0){
                slackNotifyService.sendMessage( "number = "+number + " makeMatchDetail processing, " + start+ " / " + matchSize );
            }
            log.info(start + " time check end : " + (System.currentTimeMillis() - time) );
        }

    }
    private void combination(MatchDto matchInfo, List<Participant> participantList,Map<String,Boolean> visited,Boolean win,int number,int start){
        if(participantList.size()==number){
            timeCheckComponent.checkStart();
            saveMatch(participantList,win,number, matchInfo.getInfo().getGameCreation());
            log.info("saveMatch : " + timeCheckComponent.checkEnd());
            return;
        }

        Long startTime = System.currentTimeMillis();
        for(int i = start; i< matchInfo.getInfo().getParticipants().size(); i++){
            Participant participant = matchInfo.getInfo().getParticipants().get(i);
            if (visited.containsKey(participant.getPuuid()) && !visited.get(participant.getPuuid())) {
                participantList.add(participant);
                visited.put(participant.getPuuid(),true);
                combination(matchInfo,participantList,visited,win,number,i+1);
                participantList.remove(participant);
                visited.put(participant.getPuuid(),false);
            }
        }
        log.info("combination Logic Spent Time : " + (System.currentTimeMillis() - startTime) );
    }
    private void saveMatch(List<Participant> participantList, Boolean win, int number, Long creationTimeStamp){
        if(number==1){
            saveSoloMatch(participantList,win,creationTimeStamp);
        }
        else if(number==2){
            saveDoubleMatch(participantList,win,creationTimeStamp);
        }
    }
    private Long getMainRune(Participant participant){
        Long MainRune = 0L;
        for (PerkStyle perkStyle : participant.getPerks().getStyles()) {
            if (perkStyle.getDescription().equals("primaryStyle")) {
                MainRune = perkStyle.getSelections().get(0).getPerk();
                break;
            }
        }
        return MainRune;
    }
    private void saveSoloMatch(List<Participant> participantList, Boolean win, Long creationTimeStamp){
        LocalDate matchDate = LocalDate.ofInstant(Instant.ofEpochMilli(creationTimeStamp), ZoneId.of("Asia/Seoul"));
        participantList.forEach(participant -> {
            String position = participant.getTeamPosition();
            Long championId = participant.getChampionId();
            Long mainRune = getMainRune(participant);

            SoloMatchEntity soloMatchEntity = soloMatchRepository.findByPositionAndChampionIdAndMainRune(position,championId,mainRune).orElse(null);

            if(soloMatchEntity == null) {
                soloMatchEntity = new SoloMatchEntity(matchDate,position,championId,mainRune,1L,win ? 1L : 0L);
            }
            else{
                soloMatchEntity.setAllCount(soloMatchEntity.getAllCount()+1);
                if(win){
                   soloMatchEntity.setWinCount(soloMatchEntity.getWinCount()+1);
                }
            }
            soloMatchRepository.save(soloMatchEntity);
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
            mainRuneArr[i] = getMainRune(participantList.get(i));
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
