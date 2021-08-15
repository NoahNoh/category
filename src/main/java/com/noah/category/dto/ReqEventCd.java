package com.noah.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.concurrent.atomic.AtomicBoolean;

@AllArgsConstructor
@Getter
public enum ReqEventCd {
    C("C", "추가"),
    D("D", "제거");

    private final String code;
    private final String description;

    public static boolean isReqEventCd(String code) {
        AtomicBoolean result = new AtomicBoolean(false);
        for(ReqEventCd cd : ReqEventCd.values()) {
            if(cd.getCode().equals(code)) {
                result.set(true);
                break;
            }
        }
        return result.get();
    }
}