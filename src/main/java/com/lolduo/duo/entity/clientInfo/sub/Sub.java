package com.lolduo.duo.entity.clientInfo.sub;

import lombok.Getter;

@Getter
public abstract class Sub {
    private Long win;
    private Long allCount;

    public Sub(Long win, Long allCount) {
        this.win = win;
        this.allCount = allCount;
    }
    public void setWin(Long win) {
        this.win = win;
    }

    public void setAllCount(Long allCount) {
        this.allCount = allCount;
    }
}
