package com.lolduo.duo.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lolduo.duo.entity.clientInfo.*;
import com.lolduo.duo.entity.clientInfo.sub.Item;
import com.lolduo.duo.entity.clientInfo.sub.Perk;
import com.lolduo.duo.entity.clientInfo.sub.Spell;
import com.lolduo.duo.entity.gameInfo.*;
import com.lolduo.duo.repository.clientInfo.*;
import com.lolduo.duo.repository.gameInfo.*;
import com.lolduo.duo.service.temp.MythItemList;
import com.lolduo.duo.service.temp.PrimaryPerkMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CombiService {
    private final SoloMatchRepository soloMatchRepository;
    private final DoubleMatchRepository doubleMatchRepository;
    private final TripleMatchRepository tripleMatchRepository;
    private final PentaMatchRepository pentaMatchRepository;
    private final SoloCombiRepository soloCombiRepository;
    private final DoubleCombiRepository doubleCombiRepository;
    private final TripleCombiRepository tripleCombiRepository;
    private final PentaCombiRepository pentaCombiRepository;

    private ICombiEntity makeNewCombiEntity(IMatchEntity matchEntity, String perkMythItem, boolean isWin){
        int championCount = matchEntity.getChampionList().size();
        Perk perk = new Perk(matchEntity.getPerkListMap(), matchEntity.getWin()? 1L : 0L, 1L);
        Spell spell = new Spell(matchEntity.getSpellListMap(), matchEntity.getWin()? 1L : 0L, 1L);
        Item item = new Item(matchEntity.getItemListMap(), matchEntity.getWin()? 1L : 0L, 1L);
        List<Perk> perkList = new LinkedList<>();
        List<Spell> spellList = new LinkedList<>();
        List<Item> itemList = new LinkedList<>();

        perkList.add(perk);
        spellList.add(spell);
        itemList.add(item);

        return getCombiEntity(championCount, matchEntity.getChampionList(), matchEntity.getPositionMap(), perkMythItem, 1L, matchEntity.getWin()? 1L : 0L, perkList, spellList, itemList);
    }
    public void makeCombiInfo(int championCount, LocalDate yesterday) {
        log.info("makeCombiInfo - start : " + championCount);
        ICombiRepository combiRepository = getInfoRepository(championCount);
        IMatchRepository matchRepository = getMatchRepository(championCount);
        ObjectMapper objectMapper = new ObjectMapper();

        Long start = 0L ;
        Long matchSize = matchRepository.findSizeByDate(yesterday).orElse(0L);
        log.info(championCount + " MatchSize : " + matchSize);
        while(start < matchSize) {
            List<? extends IMatchEntity> matchEntitiyList = matchRepository.findAllByDate(yesterday, start);
            log.info(championCount + " makeCombiInfo - matchRepository.findAll() , size : " + start+ " / " + matchSize);
            matchEntitiyList.forEach(matchEntity -> {
                ICombiEntity combiEntity = null;
                String perkMythItem = makePerkMythItemData(matchEntity.getItemListMap(), matchEntity.getPerkListMap());
                try {
                    combiEntity = combiRepository.findByChampionIdAndPositionAndPerkMythItem(objectMapper.writeValueAsString(matchEntity.getChampionList()), objectMapper.writeValueAsString(matchEntity.getPositionMap()), perkMythItem).orElse(null);
                } catch (JsonProcessingException e) {
                    log.error("objectMapper writeValue error");
                }
                if (combiEntity == null || combiEntity.getAllCount() == null) {
                    log.info("combiEntity is null ");
                    combiEntity = makeNewCombiEntity(matchEntity, perkMythItem, matchEntity.getWin());
                    saveCombiEntity(championCount, combiEntity);
                } else {
                    combiEntity.setAllCount(combiEntity.getAllCount() + 1);
                    if (matchEntity.getWin()) {
                        combiEntity.setWinCount(combiEntity.getWinCount() + 1);
                        updateItemList(combiEntity.getItemList(), matchEntity.getItemListMap(), true);
                        updatePerkList(combiEntity.getPerkList(), matchEntity.getPerkListMap(), true);
                        updateSpellList(combiEntity.getSpellList(), matchEntity.getSpellListMap(), true);
                    } else {
                        updateItemList(combiEntity.getItemList(), matchEntity.getItemListMap(), false);
                        updatePerkList(combiEntity.getPerkList(), matchEntity.getPerkListMap(), false);
                        updateSpellList(combiEntity.getSpellList(), matchEntity.getSpellListMap(), false);
                    }
                    saveCombiEntity(championCount, combiEntity);
                }
            });
            start +=1000L;
        }
        log.info("makeCombiInfo - end : " + championCount);
    }
    public void saveCombiEntity(int number, ICombiEntity iCombiEntity){
        if(number ==1) {
            log.info(number + " combi : "+ "allCount : " +iCombiEntity.getAllCount() + "winCount : " + "Id and position : " +iCombiEntity.getChampionId() + " " + iCombiEntity.getPosition());
            soloCombiRepository.save((SoloCombiEntity)iCombiEntity);
        }
        else if(number ==2) {
            doubleCombiRepository.save((DoubleCombiEntity) iCombiEntity);
        }
        else if(number ==3) {
            tripleCombiRepository.save( (TripleCombiEntity) iCombiEntity);
        }
        else if(number ==5) {
            pentaCombiRepository.save( (PentaCombiEntity) iCombiEntity);
        }
        else {
            log.info("saveCombiEntity - save faliled!");
            return;
        }
    }
    private void updateItemList(List<Item> infoItemList, Map<Long,List<Long>> itemList,boolean win){
        boolean isUpdated = false;
        for(int i = 0 ; i <infoItemList.size();i++){
            Item item = infoItemList.get(i);
            if(item.getItemMap().values().containsAll(itemList.values())){
                if(win)
                    infoItemList.get(i).setWin(item.getWin()+1);
                infoItemList.get(i).setAllCount(item.getAllCount()+1);
                isUpdated =true;
                break;
            }
        }
        if(!isUpdated){
            if(win)
                infoItemList.add(new Item(itemList,1L,1L));
            else
                infoItemList.add(new Item(itemList,0L,1L));
        }
    }

    private void updatePerkList(List<Perk> infoPerkList, Map<Long,List<Long>> perkList,boolean win){
        boolean isUpdated =false;
        for(int i = 0 ; i < infoPerkList.size();i++){
            Perk perk = infoPerkList.get(i);
            if(perk.getPerkMap().values().containsAll(perkList.values())){
                if(win)
                    infoPerkList.get(i).setWin(perk.getWin()+1);
                infoPerkList.get(i).setAllCount(perk.getAllCount()+1);
                isUpdated=true;
                break;
            }
        }
        if(!isUpdated){
            if(win)
                infoPerkList.add(new Perk(perkList,1L,1L));
            else
                infoPerkList.add(new Perk(perkList,0L,1L));

        }
    }
    private void updateSpellList(List<Spell> infoSpellList, Map<Long, TreeSet<Long>> spellList,boolean win){
        boolean isUpdated =false;
        for(int i = 0 ; i < infoSpellList.size();i++){
            Spell spell = infoSpellList.get(i);
            if(spell.getSpellMap().values().containsAll(spellList.values())){
                if(win)
                    infoSpellList.get(i).setWin(spell.getWin()+1);
                infoSpellList.get(i).setAllCount(spell.getAllCount()+1);
                isUpdated=true;
                break;
            }
        }
        if(!isUpdated){
            if(win)
                infoSpellList.add(new Spell(spellList,1L,1L));
            else
                infoSpellList.add(new Spell(spellList,0L,1L));
        }
    }

    private ICombiRepository getInfoRepository(int championCount) {
        if (championCount == 1) {
            log.info("getInfoRepository() - championCount : {}, 1명", championCount);
            return soloCombiRepository;
        }
        else if (championCount == 2) {
            log.info("getInfoRepository() - championCount : {}, 2명", championCount);
            return doubleCombiRepository;
        }
        else if (championCount == 3) {
            log.info("getInfoRepository() - championCount : {}, 3명", championCount);
            return tripleCombiRepository;
        }
        else if (championCount == 5) {
            log.info("getInfoRepository() - championCount : {}, 5명", championCount);
            return pentaCombiRepository;
        }
        else {
            log.info("getInfoRepository() - 요청 문제 발생");
            return null;
        }
    }
    private IMatchRepository getMatchRepository(int championCount) {
        if (championCount == 1) {
            log.info("getMatchRepository() - championCount : {}, 1명", championCount);
            return soloMatchRepository;
        }
        else if (championCount == 2) {
            log.info("getMatchRepository() - championCount : {}, 2명", championCount);
            return doubleMatchRepository;
        }
        else if (championCount == 3) {
            log.info("getMatchRepository() - championCount : {}, 3명", championCount);
            return tripleMatchRepository;
        }
        else if (championCount == 5) {
            log.info("getMatchRepository() - championCount : {}, 5명", championCount);
            return pentaMatchRepository;
        }
        else {
            log.info("getMatchRepository() - 요청 문제 발생");
            return null;
        }
    }
    private ICombiEntity getCombiEntity(int championCount, TreeSet<Long> championId, Map<Long, String> position, String perkMythItem, Long allCount, Long winCount, List<Perk> perkList, List<Spell> spellList, List<Item> itemList){
        if (championCount == 1) {
            log.info("getMatchRepository() - championCount : {}, 1명", championCount);
            return new SoloCombiEntity(championId, position, perkMythItem, allCount, winCount, perkList, spellList, itemList);
        }
        else if (championCount == 2) {
            log.info("getMatchRepository() - championCount : {}, 2명", championCount);
            return new DoubleCombiEntity(championId, position, perkMythItem, allCount, winCount, perkList, spellList, itemList);
        }
        else if (championCount == 3) {
            log.info("getMatchRepository() - championCount : {}, 3명", championCount);
            return new TripleCombiEntity(championId, position, perkMythItem, allCount, winCount, perkList, spellList, itemList);
        }
        else if (championCount == 5) {
            log.info("getMatchRepository() - championCount : {}, 5명", championCount);
            return new PentaCombiEntity(championId, position, perkMythItem, allCount, winCount, perkList, spellList, itemList);
        }
        else {
            log.info("getMatchRepository() - 요청 문제 발생");
            return null;
        }
    }

    private List<Long> findMajorPerks(List<Long> perkList) {
        log.info("findMajorPerks - perkList: {}", perkList.toString());
        List<Long> majorPerkList = new ArrayList<>();

        Map<Long, Long> primaryPerkMap = PrimaryPerkMap.getPrimaryMap();
        List<Long> secondaryPerk = new LinkedList<>();
        Long primaryPerk = 0L;
        Long keystonePerk = 0L;

        for (Long perk : perkList) {
            if (perk >= 8000L && perk % 100 == 0)
                secondaryPerk.add(perk);
            else if (primaryPerkMap.containsKey(perk))
                keystonePerk = perk;
        }
        primaryPerk = primaryPerkMap.get(keystonePerk);
        if(primaryPerk == null) primaryPerk = 0L; //Exception handling

        secondaryPerk.remove(primaryPerk);
        if(secondaryPerk.size() < 1) secondaryPerk.add(0L); //Exception handling
        log.info("findMajorPerks - primaryPerk: {}, keystonePerk: {}, secondaryPerk: {}", primaryPerk, keystonePerk, secondaryPerk.get(0));

        majorPerkList.add(primaryPerk);
        majorPerkList.add(keystonePerk);
        majorPerkList.add(secondaryPerk.get(0));

        return majorPerkList;
    }

    private String makePerkMythItemData(Map<Long, List<Long>> itemListMap, Map<Long, List<Long>> perkListMap) {
        int championCount = itemListMap.size();
        List<Long> mythItemList = MythItemList.getMythItemList();
        StringBuilder perkMythItemBuilder = new StringBuilder();

        List<List<Long>> itemSequenceList = new ArrayList<>(itemListMap.values());
        List<List<Long>> perkFormationList = new ArrayList<>(perkListMap.values());

        for (int index = 0; index < championCount; index++) {
            for (Long majorPerk : findMajorPerks(perkFormationList.get(index))) {
                perkMythItemBuilder.append(majorPerk);
                perkMythItemBuilder.append("|");
            }

            boolean hasMythItem = false;
            for (Long item : itemSequenceList.get(index)) {
                if (mythItemList.contains(item)) {
                    perkMythItemBuilder.append(item);
                    hasMythItem = true;
                    break;
                }
            }
            if (!hasMythItem)
                perkMythItemBuilder.append(0);

            if (index != championCount - 1)
                perkMythItemBuilder.append("|");
        }
        return perkMythItemBuilder.toString();
    }
}
