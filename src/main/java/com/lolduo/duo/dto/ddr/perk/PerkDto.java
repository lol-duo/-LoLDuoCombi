package com.lolduo.duo.dto.ddr.perk;

import lombok.Getter;

import java.util.List;

@Getter
public class PerkDto {
    private Long id;
    private String key;
    private String icon;
    private String name;
    private List<PerkRuneList> slots;
}
