package com.lolduo.duo.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TimeCheckComponent {
    private Long currentMillis = System.currentTimeMillis();

    public void checkStart(){
        currentMillis = System.currentTimeMillis();
    }
    public Long checkEnd(){
        return System.currentTimeMillis() - currentMillis;
    }
}
