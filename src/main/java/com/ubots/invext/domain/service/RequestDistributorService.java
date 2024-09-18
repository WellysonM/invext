package com.ubots.invext.domain.service;

import com.ubots.invext.domain.entity.Agent;
import com.ubots.invext.domain.entity.Team;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class RequestDistributorService {

    private final Map<String, Team> teams;

    public RequestDistributorService() {
        teams = new HashMap<>();

        teams.put("Cards", new Team("Cards", List.of(new Agent("Agent 1"), new Agent("Agent 2"))));
        teams.put("Loans", new Team("Loans", List.of(new Agent("Agent 3"), new Agent("Agent 4"))));
        teams.put("Other Issues", new Team("Other Issues", List.of(new Agent("Agent 5"), new Agent("Agent 6"))));
    }

    public String distributeRequest(String requestType) {
        if (requestType.equalsIgnoreCase("Card issues")) {
            teams.get("Cards").addRequest(requestType);
        } else if (requestType.equalsIgnoreCase("Loan application")) {
            teams.get("Loans").addRequest(requestType);
        } else {
            teams.get("Other Issues").addRequest(requestType);
        }
        return "Request has been sent to the appropriate team.";
    }

    public String finishRequest(String agentName, String requestType) {
        if (requestType.equalsIgnoreCase("Card issues")) {
            teams.get("Cards").finishRequest(agentName);
        } else if (requestType.equalsIgnoreCase("Loan application")) {
            teams.get("Loans").finishRequest(agentName);
        } else {
            teams.get("Other Issues").finishRequest(agentName);
        }
        return "Request finished and queue checked.";
    }

    public void checkAllQueues() {
        teams.values().forEach(Team::checkQueue);
    }
}
