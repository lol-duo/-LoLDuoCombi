package com.lolduo.duo.service.temp;

import java.util.ArrayList;
import java.util.List;

public class MythItemList {
    private static List<Long> mythItemList;

    public static List<Long> getMythItemList() {
        if (mythItemList == null) {
            mythItemList = new ArrayList<>();

            mythItemList.add(6630L);
            mythItemList.add(6631L);
            mythItemList.add(6632L);
            mythItemList.add(3078L);

            mythItemList.add(6671L);
            mythItemList.add(6672L);
            mythItemList.add(6673L);

            mythItemList.add(6691L);
            mythItemList.add(6692L);
            mythItemList.add(6693L);

            mythItemList.add(6653L);
            mythItemList.add(6655L);
            mythItemList.add(6656L);
            mythItemList.add(4644L);
            mythItemList.add(3152L);
            mythItemList.add(4633L);
            mythItemList.add(4636L);

            mythItemList.add(6662L);
            mythItemList.add(6664L);
            mythItemList.add(3068L);

            mythItemList.add(2065L);
            mythItemList.add(3190L);
            mythItemList.add(3001L);
            mythItemList.add(4005L);
            mythItemList.add(6617L);
        }
        return mythItemList;
    }

    public static void freeList() {
        mythItemList = null;
    }
}