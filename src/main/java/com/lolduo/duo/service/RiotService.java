package com.lolduo.duo.service;


import com.lolduo.duo.dto.RiotAPI.league_v4.summoner_tier.LeagueEntryDTO;
import com.lolduo.duo.dto.ddr.champion.ChampionDto;
import com.lolduo.duo.dto.ddr.item.ItemDto;
import com.lolduo.duo.dto.RiotAPI.league_v4.LeagueListDTO;
import com.lolduo.duo.dto.ddr.perk.PerkDto;
import com.lolduo.duo.dto.ddr.perk.PerkRune;
import com.lolduo.duo.dto.ddr.spell.SpellDto;
import com.lolduo.duo.dto.RiotAPI.summoner_v4.SummonerDTO;
import com.lolduo.duo.dto.RiotAPI.timeline.MatchTimeLineDto;
import com.lolduo.duo.entity.*;
import com.lolduo.duo.entity.gameInfo.DoubleMatchEntity;
import com.lolduo.duo.entity.gameInfo.PentaMatchEntity;
import com.lolduo.duo.entity.gameInfo.TripleMatchEntity;
import com.lolduo.duo.entity.initialInfo.*;
import com.lolduo.duo.dto.RiotAPI.match_v5.MatchDto;
import com.lolduo.duo.dto.RiotAPI.match_v5.Participant;
import com.lolduo.duo.entity.gameInfo.SoloMatchEntity;
import com.lolduo.duo.repository.*;
import com.lolduo.duo.repository.gameInfo.DoubleMatchRepository;
import com.lolduo.duo.repository.gameInfo.PentaMatchRepository;
import com.lolduo.duo.repository.gameInfo.SoloMatchRepository;
import com.lolduo.duo.repository.gameInfo.TripleMatchRepository;
import com.lolduo.duo.repository.initialInfo.*;
import com.lolduo.duo.service.slack.SlackNotifyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Value("${riot.key}")
    private String key;
    private String version;
    private final ItemRepository itemRepository;
    private final ItemFullRepository itemFullRepository;
    private final PerkRepository perkRepository;
    private final ChampionRepository championRepository;
    private final SpellRepository spellRepository;
    private final SoloMatchRepository soloMatchRepository;
    private final DoubleMatchRepository doubleMatchRepository;
    private final TripleMatchRepository tripleMatchRepository;
    private final PentaMatchRepository pentaMatchRepository;
    private final LoLUserRepository lolUserRepository;
    private final MatchDetailRepository matchDetailRepository;
    private final SlackNotifyService slackNotifyService;
    private final CombiService combiService;

    public void setVersion(String version) {
        this.version = version;
    }
    public void setKey(String key) {
        this.key = key;
    }
    @Override
    public void run(ApplicationArguments args) throws Exception{
        setVersion("12.14.1");
        //setItem();
        //setChampion();
        //setSpell();
        //setPerk();
        //All();
        //test();
        log.info("ready");
    }
    public void test(){
        String[] st = {"KR_6048833978",
                "KR_6048791185",
                "KR_6047665628",
                "KR_6047556921",
                "KR_6045011909",
                "KR_6044866927",
                "KR_6044652920",
                "KR_6044599971",
                "KR_6044558095",
                "KR_6044545900",
                "KR_6043272985",
                "KR_6043196788",
                "KR_6043163990",
                "KR_6042147114",
                "KR_6041967954",
                "KR_6041961802",
                "KR_6032993563",
                "KR_6032944098",
                "KR_6032809258",
                "KR_6031458458"};
        getMatchInfo(new HashSet<>(Arrays.asList(st)));
        log.info("solo~team 정보 저장완료 ");
        log.info("2차 가공 start");
        Long endTime = System.currentTimeMillis() / 1000;
        Long startTime = endTime - 86400;

        LocalDate yesterday = LocalDate.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.of("Asia/Seoul"));
        combiService.makeCombiInfo(1,yesterday);
        combiService.makeCombiInfo(2,yesterday);
        combiService.makeCombiInfo(3,yesterday);
        combiService.makeCombiInfo(5,yesterday);
        log.info("2차 가공 end");
    }
    //@Scheduled(cron = "1 0 0 * * *", zone = "Asia/Seoul")
    private void All(){
        Long endTime = System.currentTimeMillis() / 1000;
        Long startTime = endTime - 86400;
        Set<String> matchIdList = new HashSet<>();
        LocalDate yesterday = LocalDate.ofInstant(Instant.ofEpochSecond(startTime), ZoneId.of("Asia/Seoul"));
        log.info("All - yesterday : {}", yesterday);

        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date d = null;
        LocalDate localDate = null;
        try {
            d = dateFormat.parse("2022-08-16");
            localDate = d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        /*
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "challenger list 가져오기 start");
        log.info("get challenger start");
        getPuuIdList("challenger");

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "challenger matchId 만들기 start");
        log.info("make challenger matchIList start");
        matchIdList.addAll(getMatchId(startTime,endTime,"challenger"));
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "matchId 만들기 start");
        log.info("getMatch Info start : matchListSize : " +matchIdList.size());
        getMatchInfo(matchIdList);

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "grandmaster list 가져오기 start");
        log.info("get grandmaster start");
        getPuuIdList("grandmaster");

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "grandmaster matchId 만들기 start");
        log.info("make grandmaster matchIList start");
        matchIdList = new HashSet<>();
        matchIdList.addAll(getMatchId(startTime,endTime,"grandmaster"));
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "matchId 만들기 start");
        log.info("getMatch Info start : matchListSize : " +matchIdList.size());
        getMatchInfo(matchIdList);

        
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "master list 가져오기 start");
        log.info("get master start");
        getPuuIdList("master");
        
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "master matchId 만들기 start");
        log.info("make master matchIList start");
        matchIdList = new HashSet<>();
        matchIdList.addAll(getMatchId(startTime,endTime,"master"));
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "matchId 만들기 start");
        log.info("getMatch Info start : matchListSize : " +matchIdList.size());
        getMatchInfo(matchIdList);



        log.info("matchDetail 저장완료 ");
        log.info("1차 가공 start");
        setMatchInfo(1);
        setMatchInfo(2);
        setMatchInfo(3);
        setMatchInfo(5);
        */;
        log.info("1차 가공 end\n 2차 가공 start");
        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "SoloInfo 만들기 start");

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "TrioInfo 만들기 start");
        log.info("CombiInfo : Triple make");
        combiService.makeCombiInfo(3,yesterday);

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "DuoInfo 만들기 start");
        log.info("CombiInfo : Double make");
        combiService.makeCombiInfo(2,yesterday);

        log.info("CombiInfo : Solo make");
        combiService.makeCombiInfo(1,yesterday);

        //slackNotifyService.sendMessage(slackNotifyService.nowTime() + "QuintetInfo 만들기 start");
        log.info("CombiInfo : Penta make");
        combiService.makeCombiInfo(5,yesterday);
        log.info("2차 가공 end");
    }

    private void makeFullItem(ItemDto item){
        Set<String> itemIdList = item.getData().keySet();
        for(String itemId : itemIdList){
            if(item.getData().get(itemId).getInto() == null && item.getData().get(itemId).getGold().getTotal() > 1500L){
                itemFullRepository.save(new ItemFullEntity(Long.parseLong(itemId)));
            }
        }
        //신화 추가 부분, 신화는 오른때문에 진화트리가 있어버려서, ddr api만으로는 신화구분이 어려움.따라서 직접 추가.
        itemFullRepository.save(new ItemFullEntity(6630L));
        itemFullRepository.save(new ItemFullEntity(6631L));
        itemFullRepository.save(new ItemFullEntity(6632L));
        itemFullRepository.save(new ItemFullEntity(3078L));
        itemFullRepository.save(new ItemFullEntity(6671L));
        itemFullRepository.save(new ItemFullEntity(6672L));
        itemFullRepository.save(new ItemFullEntity(6673L));
        itemFullRepository.save(new ItemFullEntity(6691L));
        itemFullRepository.save(new ItemFullEntity(6692L));
        itemFullRepository.save(new ItemFullEntity(6693L));
        itemFullRepository.save(new ItemFullEntity(6653L));
        itemFullRepository.save(new ItemFullEntity(6655L));
        itemFullRepository.save(new ItemFullEntity(6656L));
        itemFullRepository.save(new ItemFullEntity(4644L));
        itemFullRepository.save(new ItemFullEntity(3152L));
        itemFullRepository.save(new ItemFullEntity(4633L));
        itemFullRepository.save(new ItemFullEntity(4636L));
        itemFullRepository.save(new ItemFullEntity(6662L));
        itemFullRepository.save(new ItemFullEntity(6664L));
        itemFullRepository.save(new ItemFullEntity(3068L));
        itemFullRepository.save(new ItemFullEntity(2065L));
        itemFullRepository.save(new ItemFullEntity(3190L));
        itemFullRepository.save(new ItemFullEntity(3001L));
        itemFullRepository.save(new ItemFullEntity(4005L));
        itemFullRepository.save(new ItemFullEntity(6617L));
    }
    private void getMatchInfo(Set<String> matchIdList){
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        matchIdList.forEach(matchId -> {
            final int PlayerNum = 10;
            ResponseEntity<MatchTimeLineDto> time_match = null;
            try{
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                time_match = restTemplate.exchange(url + matchId + "/timeline", HttpMethod.GET, requestEntity, MatchTimeLineDto.class);
            }catch (Exception e){
                log.info("matchId : " + matchId + " 에러");
                return;
            }
            List<List<Long>> playerItemList = new ArrayList<List<Long>>();
            for(int i =0 ; i<= PlayerNum; i++){
                playerItemList.add(new ArrayList<>());
            }

            Map<String, Long> puuIdMap = new HashMap<>();
            time_match.getBody().getInfo().getParticipants().forEach(participantDto -> {
                puuIdMap.put(participantDto.getPuuid(), participantDto.getParticipantId());
            });
            time_match.getBody().getInfo().getFrames().forEach(frameDto -> {
                frameDto.getEvents().forEach(eventDto -> {
                    if(eventDto.getType().equals("ITEM_PURCHASED") && itemFullRepository.findById(eventDto.getItemId()).orElse(null) != null){
                        playerItemList.get(eventDto.getParticipantId().intValue()).add(eventDto.getItemId());
                    }
                });
            });
            for(int i =0 ; i<= PlayerNum; i++){
                playerItemList.get(i).add(0L);
                playerItemList.get(i).add(0L);
                playerItemList.get(i).add(0L);
            }
            ResponseEntity<MatchDto> response_match = null;
            try {
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                response_match = restTemplate.exchange(url + matchId, HttpMethod.GET, requestEntity, MatchDto.class);
            }catch (Exception e){
                log.info("matchId : " + matchId + " 에러");
                return;
            }
            for (Participant participant : response_match.getBody().getInfo().getParticipants()) {
                if (participant.getTeamPosition() == null || participant.getTeamPosition().isEmpty())
                    return;
            }
            String tier = getTier(response_match.getBody());
            matchDetailRepository.save(new MatchDetailEntity(LocalDate.now(ZoneId.of("Asia/Seoul")),response_match.getBody(),  playerItemList, puuIdMap,tier));
        });
    }
    private String getTierBySummonerId(String summonerId){
        String url_tier = "https://kr.api.riotgames.com/lol/league/v4/entries/by-summoner/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<LeagueEntryDTO[]> response = null;
        try{
            try{
                Thread.sleep(1500);
            } catch(InterruptedException e){
                throw new RuntimeException(e);
            }
            response = restTemplate.exchange(url_tier+summonerId, HttpMethod.GET, requestEntity, LeagueEntryDTO[].class);
        }catch (Exception e){
            log.info("getTierBySummonerId 에러발생 : {}",e.getMessage());
            return "default";
        }
        for(int i = 0 ; i< response.getBody().length;i++){
            if(response.getBody()[i].getQueueType().equals("RANKED_SOLO_5x5"))
                return response.getBody()[i].getTier().toLowerCase();
        }
        return "default";
    }
    private String getTier(MatchDto matchDto){
        Map<String, Long> tierNumList = new HashMap<>();
        tierNumList.put("challenger", 1L);
        tierNumList.put("grandmaster", 2L);
        tierNumList.put("master", 3L);
        tierNumList.put("diamond", 4L);
        tierNumList.put("platinum",5L);
        tierNumList.put("gold",6L);
        tierNumList.put("silver",7L);
        tierNumList.put("bronze",8L);
        tierNumList.put("iron",9L);
        tierNumList.put("default", 4L);
        Map<Integer, String> tierNameList =  new HashMap<>();
        tierNameList.put(1, "challenger");
        tierNameList.put(2, "grandmaster");
        tierNameList.put(3, "master");
        tierNameList.put(4,"diamond");
        tierNameList.put(5,"platinum");
        tierNameList.put(6,"gold");
        tierNameList.put(7,"silver");
        tierNameList.put(8,"bronze");
        tierNameList.put(9,"iron");
        Long tierNum = 0L;
        List<Participant> participantList = matchDto.getInfo().getParticipants();
        LoLUserEntity loLUserEntity = null ;
        for(Participant participant : participantList) {
            loLUserEntity = lolUserRepository.findById(participant.getPuuid()).orElse(null);
            if(loLUserEntity==null){
                String tier = getTierBySummonerId(participant.getSummonerId());
                log.info("getTierBySummonerId 결과 : " + tier);
                Long number = 0L;
                if(tierNumList.get(tier) ==null) {
                    tierNum += 4;
                }
                else{
                    tierNum += tierNumList.get(tier);
                }
                lolUserRepository.save(new LoLUserEntity(participant.getPuuid(),tier));
            }
            else{
                tierNum += tierNumList.get(loLUserEntity.getTier());
            }
        }
        return tierNameList.get(Math.round(tierNum/ 10));
    }
    private void setMatchInfo(int number) {
        List<MatchDetailEntity> matchDetailEntity = matchDetailRepository.findAllByDate(LocalDate.now(ZoneId.of("Asia/Seoul")));
        log.info("setMatchInfo function number = " + number +" matchEntity size : " + matchDetailEntity.size());
        matchDetailEntity.forEach(match -> {
            MatchDto matchDto = match.getMatchInfo();
            List<List<Long>> playerItemList = match.getPlayerItemList();
            Map<String, Long> puuIdMap = match.getPuuIdMap();
            String tier = match.getTier();

            Map<String, Boolean> visitedWin = new HashMap<>();
            Map<String, Boolean> visitedLose = new HashMap<>();
            if(number==5){
                List<Participant> winParticipantList = new ArrayList<>();
                List<Participant> loseParticipantList = new ArrayList<>();
                matchDto.getInfo().getParticipants().forEach(participant -> {
                    if (participant.getWin() == true) {
                        winParticipantList.add(participant);
                    } else {
                        loseParticipantList.add(participant);
                    }
                });
                //5인 정보 save
                saveMatch(winParticipantList, playerItemList, puuIdMap, true, 5, tier, matchDto.getInfo().getGameCreation());
                saveMatch(loseParticipantList, playerItemList, puuIdMap, false, 5, tier,matchDto.getInfo().getGameCreation());
            }
            else {
                matchDto.getInfo().getParticipants().forEach(participant -> {
                    if (participant.getWin() == true) {
                        visitedWin.put(participant.getPuuid(), false);
                    } else {
                        visitedLose.put(participant.getPuuid(), false);
                    }
                });
                //1,2,3일때 정보 save
                combination(matchDto, playerItemList, puuIdMap, new ArrayList<>(), visitedWin, true, number, 0, tier);
                combination(matchDto, playerItemList, puuIdMap, new ArrayList<>(), visitedLose, false, number, 0, tier);
            }
        });
    }
    private void combination(MatchDto matchDto, List<List<Long>> playerItemList, Map<String, Long> puuIdMap,List<Participant> participantList,Map<String,Boolean> visited,Boolean win,int number,int start,String tier){
        if(participantList.size()==number){
            saveMatch(participantList,playerItemList,puuIdMap,win,number, tier, matchDto.getInfo().getGameCreation());
            return;
        }
        for(int i = start; i< matchDto.getInfo().getParticipants().size(); i++){
            Participant participant = matchDto.getInfo().getParticipants().get(i);
            if (visited.containsKey(participant.getPuuid())==true && visited.get(participant.getPuuid())==false) {
                participantList.add(participant);
                visited.put(participant.getPuuid(),true);
                combination(matchDto,playerItemList,puuIdMap,participantList,visited,win,number,i+1, tier);
                participantList.remove(participant);
                visited.put(participant.getPuuid(),false);
            }
        }
    }
    private void saveMatch(List<Participant> participantList, List<List<Long>> playerItemList, Map<String, Long> puuIdMap, Boolean win, int number, String tier,Long creationTimeStamp){
        Map<Long,String> positionMap = new HashMap<>();
        Map<Long,List<Long>> itemListMap = new HashMap<>();
        Map<Long,TreeSet<Long>> spellListMap = new HashMap<>();
        TreeSet<Long> championList = new TreeSet<>();
        Map<Long,List<Long>> perkListMap = new HashMap<>();
        LocalDate matchDate = LocalDate.ofInstant(Instant.ofEpochMilli(creationTimeStamp), ZoneId.of("Asia/Seoul"));

        participantList.forEach(participant -> {
            String position = participant.getTeamPosition();
            List<Long> itemList = playerItemList.get(puuIdMap.get(participant.getPuuid()).intValue());
            TreeSet<Long> spellList = new TreeSet<>();
            spellList.add(participant.getSummoner1Id());
            spellList.add(participant.getSummoner2Id());
            Long championId = participant.getChampionId();
            List<Long> perkList = new ArrayList<>();
            perkList.add(participant.getPerks().getStatPerks().getDefense());
            perkList.add(participant.getPerks().getStatPerks().getOffense());
            perkList.add(participant.getPerks().getStatPerks().getFlex());
            participant.getPerks().getStyles().forEach(perkStyle -> {
                perkStyle.getSelections().forEach(perkStyleSelection -> {
                    perkList.add(perkStyleSelection.getPerk());
                });
                perkList.add(perkStyle.getStyle());
            });
            Collections.sort(perkList);
            positionMap.put(championId,position);
            itemListMap.put(championId,itemList);
            spellListMap.put(championId,spellList);
            championList.add(championId);
            perkListMap.put(championId,perkList);
        });
        if(number==1){
            soloMatchRepository.save(new SoloMatchEntity(matchDate,tier,win,positionMap,itemListMap,spellListMap,championList,perkListMap));
        }
        else if(number==2){
            doubleMatchRepository.save(new DoubleMatchEntity(matchDate,tier,win,positionMap,itemListMap,spellListMap,championList,perkListMap) );
        }
        else if(number==3){
            tripleMatchRepository.save(new TripleMatchEntity(matchDate,tier,win,positionMap,itemListMap,spellListMap,championList,perkListMap));
        }
        else if(number==5){
            pentaMatchRepository.save(new PentaMatchEntity(matchDate,tier,win,positionMap,itemListMap,spellListMap,championList,perkListMap));
        }
    }
    private void getPuuIdList(String league) {
        String url = "https://kr.api.riotgames.com/lol/league/v4/"+league+"leagues/by-queue/RANKED_SOLO_5x5";
        String url_summoner = "https://kr.api.riotgames.com/lol/summoner/v4/summoners/";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<LeagueListDTO> response = null;
        try{
            response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, LeagueListDTO.class);
        }catch (Exception e){
            log.info("getPuuIdList 에러발생 : {}",e.getMessage());
            return;
        }
        response.getBody().getEntries().forEach(leagueItemDTO -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                log.info("getSummonerId thread Error!");
                throw new RuntimeException(e);
            }
            String puuid = null;
            try{
                puuid = restTemplate.exchange(url_summoner + leagueItemDTO.getSummonerId(), HttpMethod.GET, requestEntity, SummonerDTO.class).getBody().getPuuid();
            }catch (Exception e){
                log.info("getPuuIdList 에러발생 summuner: {}",e.getMessage());
                return;
            }

            LoLUserEntity lolUserEntity = lolUserRepository.findById(puuid).orElse(null);
            if(lolUserEntity == null)
                lolUserRepository.save(new LoLUserEntity(puuid,league));
            else if(lolUserEntity.getTier() != league){
                lolUserEntity.setTier(league);
                lolUserRepository.save(lolUserEntity);
            }
        });
        return;
    }

    private Set<String> getMatchId(Long startTime, Long endTime, String league) {
        String url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/";
        List<String> puuidList = lolUserRepository.findPuuidsByLeague(league);
        Set<String> matchList = new HashSet<>();

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        puuidList.forEach(puuid -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            ResponseEntity<List> response = null;
            try {
                response = restTemplate.exchange(url + puuid + "/ids?startTime=" + startTime + "&endTime=" + endTime + "&type=ranked&start=0&count=100", HttpMethod.GET, requestEntity, List.class);
            }catch (Exception e) {
                log.info("getMatchId 에러발생 : {}",e.getMessage());
                return;
            }
            log.info("getMatchId : matchId added Success . MatchList size() :"+ response.getBody().size());
            matchList.addAll(response.getBody());
        });
        return matchList;
    }

    //  패치 별 아이템, 챔피언 ,스펠, 룬 정보 정보들 DB에 세팅해주는 부분들
    private void setItem(){
        String url = "https://ddragon.leagueoflegends.com/cdn/"+version+"/data/ko_KR/item.json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ItemDto> item = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ItemDto.class);
        //추후에 map iter 로 변경하여서 n번
        Set<String> itemIdList = item.getBody().getData().keySet();
        for(String itemId : itemIdList){
            itemRepository.save(new ItemEntity(Long.parseLong(itemId), item.getBody().getData().get(itemId).getName(),itemId + ".png"));
        }
        makeFullItem(item.getBody());
    }
    private void setChampion(){
        String url = "https://ddragon.leagueoflegends.com/cdn/"+version+"/data/ko_KR/champion.json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<ChampionDto> championList = restTemplate.exchange(url, HttpMethod.GET, requestEntity, ChampionDto.class);

        Set<String> championIdList = championList.getBody().getData().keySet();
        for(String championId : championIdList){
            championRepository.save(new ChampionEntity(Long.parseLong(championList.getBody().getData().get(championId).getKey()), championList.getBody().getData().get(championId).getName(),championId + ".png"));
        }
        championRepository.save(new ChampionEntity(0L,"ALL","ALL.png"));
    }
    private void setSpell(){
        String url = "https://ddragon.leagueoflegends.com/cdn/"+version+"/data/ko_KR/summoner.json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<SpellDto> spellList = restTemplate.exchange(url, HttpMethod.GET, requestEntity, SpellDto.class);

        Set<String> spellIdList = spellList.getBody().getData().keySet();
        for(String spellId : spellIdList){
            if(Integer.parseInt(spellList.getBody().getData().get(spellId).getKey()) > 21) {
                continue;
            }
            spellRepository.save(new SpellEntity(Long.parseLong(spellList.getBody().getData().get(spellId).getKey()), spellList.getBody().getData().get(spellId).getName(),spellId + ".png"));
        }
    }
    private void setPerk(){
        String url = "https://ddragon.leagueoflegends.com/cdn/"+version+"/data/ko_KR/runesReforged.json";

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Riot-Token", key);
        HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<PerkDto[]> response_perkDtoList = restTemplate.exchange(url, HttpMethod.GET, requestEntity, PerkDto[].class);
        List<PerkDto> perkDtoList = Arrays.asList(response_perkDtoList.getBody());
        perkDtoList.forEach(perkDto -> {
            for(int i = 0; i < perkDto.getSlots().size(); i++){
                List<PerkRune> perkRuneList = perkDto.getSlots().get(i).getRunes();
                for(int j = 0; j < perkRuneList.size(); j++){
                    perkRepository.save(new PerkEntity(perkRuneList.get(j).getId(),perkRuneList.get(j).getName(), perkRuneList.get(j).getIcon()));
                }
            }
            perkRepository.save(new PerkEntity(perkDto.getId(), perkDto.getName(), perkDto.getIcon()));
        });

    }
}
