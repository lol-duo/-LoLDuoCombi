package com.lolduo.duo.service;


import com.lolduo.duo.dto.RiotAPI.match_v5.MatchDto;
import com.lolduo.duo.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.dto.RiotAPI.match_v5.PerkStyle;
import com.lolduo.duo.entity.MatchDetailEntity;
import com.lolduo.duo.entity.gameInfo.DoubleMatchEntity;
import com.lolduo.duo.entity.gameInfo.SoloMatchEntity;
import com.lolduo.duo.repository.MatchDetailRepository;
import com.lolduo.duo.repository.gameInfo.DoubleMatchRepository;
import com.lolduo.duo.repository.gameInfo.SoloMatchRepository;
import com.lolduo.duo.service.slack.SlackNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Service
@EnableScheduling
@RequiredArgsConstructor
@Slf4j
public class RiotService implements ApplicationRunner{

    private final SoloMatchRepository soloMatchRepository;
    private final DoubleMatchRepository doubleMatchRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final SlackNotifyService slackNotifyService;
    @Override
    public void run(ApplicationArguments args) throws Exception{
        All();
    }
    private void All(){
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d ;
        LocalDate localDate ;
        try {
            d = dateFormat.parse("2022-09-19");
            localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        makeMatchDetail(1,localDate);
        makeMatchDetail(2,localDate);
        slackNotifyService.sendMessage(slackNotifyService.nowTime() + "SoloInfo 만들기 start");

    }
    private void makeMatchDetail(int number,LocalDate date){
        long matchSize = matchDetailRepository.findSizeByDate(date).orElse(0L);
        long start = 0L;
        log.info("makeMatchDetail function matchSize : " + matchSize );
        while(start < matchSize) {
            List<MatchDetailEntity> matchDetailEntityList = matchDetailRepository.findAllByDate(date,start);
            log.info( "makeMatchDetail - processing, " + start+ " / " + matchSize);
            matchDetailEntityList.forEach(matchDetailEntity -> {
                MatchDto matchInfo = matchDetailEntity.getMatchInfo();

                Map<String, Boolean> visitedWin = new HashMap<>();
                Map<String, Boolean> visitedLose = new HashMap<>();

                matchInfo.getInfo().getParticipants().forEach(participant -> {
                    if (participant.getWin()) {
                        visitedWin.put(participant.getPuuid(), false);
                    } else {
                        visitedLose.put(participant.getPuuid(), false);
                    }
                });
                combination(matchInfo, new ArrayList<>(), visitedWin, true, number, 0);
                combination(matchInfo, new ArrayList<>(), visitedLose, false, number, 0);
            });
            start +=1000;
            //test
            if(start > 2000) break;
        }
    }
    private void combination(MatchDto matchInfo, List<Participant> participantList,Map<String,Boolean> visited,Boolean win,int number,int start){
        if(participantList.size()==number){
            saveMatch(participantList,win,number, matchInfo.getInfo().getGameCreation());
            return;
        }
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
        DoubleMatchEntity doubleMatchEntity = doubleMatchRepository.findByPosition1AndPosition2AndChampionId1AndPosition2AndMainRune1AndMainRune2(positionArr[0],positionArr[1],championIdArr[0],championIdArr[1],mainRuneArr[0],mainRuneArr[1]).orElse(null);
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
