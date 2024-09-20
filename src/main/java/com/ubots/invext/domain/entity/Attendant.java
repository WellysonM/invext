package com.ubots.invext.domain.entity;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Attendant {
    private String name;
    private int currentRequests;

    public Attendant(String name) {
        this.name = name;
        this.currentRequests = 0;
    }
}
