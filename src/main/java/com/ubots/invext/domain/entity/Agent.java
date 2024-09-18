package com.ubots.invext.domain.entity;

import lombok.Data;

@Data
public class Agent {
    private String name;
    private int currentRequests;

    public Agent(String name) {
        this.name = name;
        this.currentRequests = 0;
    }

    public boolean canHandleRequest() {
        return currentRequests < 3;
    }

    public void startHandlingRequest() {
        if (canHandleRequest()) {
            currentRequests++;
        }
    }

    public void finishHandlingRequest() {
        if (currentRequests > 0) {
            currentRequests--;
        }
    }
}
