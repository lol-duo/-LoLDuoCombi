package com.lolduo.duo.v2.dto.RiotAPI.timeline;

import lombok.Getter;

import java.util.List;

@Getter
public class Info {
    List<Frame> frames;
    List<Participant> participants;
}
