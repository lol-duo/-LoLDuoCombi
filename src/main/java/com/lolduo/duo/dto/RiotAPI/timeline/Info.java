package com.lolduo.duo.dto.RiotAPI.timeline;

import lombok.Getter;

import java.util.List;

@Getter
public class Info {
    List<Frame> frames;
    List<Participant> participants;
}
