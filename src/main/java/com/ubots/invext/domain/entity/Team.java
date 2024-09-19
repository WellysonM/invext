package com.ubots.invext.domain.entity;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Slf4j
@Data
public class Team {
    private String name;
    private Queue<String> requestQueue;
    private List<Attendant> attendants;

    public Team(String name, List<Attendant> attendants) {
        this.name = name;
        this.attendants = attendants;
        this.requestQueue = new LinkedList<>();
    }
}
