package com.ubots.invext.domain.entity;

import lombok.Data;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

@Data
public class Team {
    private String name;
    private Queue<String> requestQueue;
    private List<Agent> agents;

    public Team(String name, List<Agent> agents) {
        this.name = name;
        this.agents = agents;
        this.requestQueue = new LinkedList<>();
    }

    public void addRequest(String request) {
        Optional<Agent> availableAgent = agents.stream()
                .filter(Agent::canHandleRequest)
                .findFirst();

        if (availableAgent.isPresent()) {
            Agent agent = availableAgent.get();
            agent.startHandlingRequest();
            System.out.println("Request '" + request + "' assigned to " + agent.getName() + " of team " + name);
        } else {
            requestQueue.add(request);
            System.out.println("All agents of team " + name + " are busy. Request '" + request + "' has been queued.");
        }
    }

    public void checkQueue() {
        if (!requestQueue.isEmpty()) {
            addRequest(requestQueue.poll());
        }
    }

    public void finishRequest(String agentName) {
        agents.stream()
                .filter(a -> a.getName().equals(agentName))
                .findFirst()
                .ifPresent(Agent::finishHandlingRequest);
    }
}
