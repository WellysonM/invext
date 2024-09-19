package com.ubots.invext.domain.service;

import com.ubots.invext.domain.entity.Attendant;
import com.ubots.invext.domain.entity.Team;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Component
public class RequestDistributorService {

    private final String CARDS = "Cards";
    private final String LOANS = "Loans";
    private final String OTHER_ISSUES = "Other Issues";
    private final Map<String, Team> teams;

    public RequestDistributorService() {
        teams = new HashMap<>();
        teams.put(CARDS, new Team(CARDS, List.of(new Attendant("Attendant 1"), new Attendant("Attendant 2"))));
        teams.put(LOANS, new Team(LOANS, List.of(new Attendant("Attendant 3"), new Attendant("Attendant 4"))));
        teams.put(OTHER_ISSUES, new Team(OTHER_ISSUES, List.of(new Attendant("Attendant 5"), new Attendant("Attendant 6"))));
    }

    public String distributeRequest(String requestType) {
        String team = getRequestMap().getOrDefault(requestType.toUpperCase(), OTHER_ISSUES);
        startHandlingRequest(requestType, teams.get(team));
        return "Request has been sent to the appropriate team.";
    }

    public String finishRequest(String attendantName, String requestType) {
        AtomicBoolean requestFinished = new AtomicBoolean(false);
        String queueType = getRequestMap().getOrDefault(requestType.toUpperCase(), OTHER_ISSUES);
        Optional<Attendant> attendantOptional = findAttendant(attendantName, queueType);
        attendantOptional.ifPresent(attendant -> requestFinished.set(finishHandlingRequest(attendant)));
        if (attendantOptional.isEmpty()) {
            return attendantName + " was not found for request type " + requestType + ".";
        }
        checkAllQueues();
        return requestFinished.get() ? "Request finished and queue checked for team " + queueType + "." : "All requests have been closed for team " + queueType + ".";
    }

    private boolean canHandleRequest(Attendant attendant) {
        return attendant.getCurrentRequests() < 3;
    }

    private void checkAllQueues() {
        teams.values().forEach(this::checkQueue);
    }

    private void checkQueue(Team team) {
        if (!team.getRequestQueue().isEmpty()) {
            startHandlingRequest(team.getRequestQueue().poll(), team);
        }
    }

    private Optional<Attendant> findAttendant(String attendantName, String team) {
        return teams.get(team).getAttendants().stream()
                .filter(attendant -> attendant.getName().equalsIgnoreCase(attendantName))
                .findFirst();
    }

    private boolean finishHandlingRequest(Attendant attendant) {
        if (attendant.getCurrentRequests() > 0) {
            attendant.setCurrentRequests(attendant.getCurrentRequests() - 1);
            log.info("Service request closed from {}.", attendant.getName());
            return true;
        } else {
            log.info("The request queue for {} is empty.", attendant.getName());
            return false;
        }
    }

    private Map<String, String> getRequestMap() {
        String CARD_ISSUES = "CARD ISSUES";
        String LOAN_APPLICATION = "LOAN APPLICATION";
        return Map.of(
                CARD_ISSUES, CARDS,
                LOAN_APPLICATION, LOANS,
                OTHER_ISSUES.toUpperCase(), OTHER_ISSUES
        );
    }

    private void startHandlingRequest(String request, Team team) {
        team.getAttendants().stream()
                .filter(this::canHandleRequest)
                .findFirst()
                .ifPresentOrElse(attendant -> {
                    if (canHandleRequest(attendant)) {
                        attendant.setCurrentRequests(attendant.getCurrentRequests() + 1);
                        log.info("Request '{}' assigned to {} of team {}", request, attendant.getName(), team.getName());
                    }
                }, () -> {
                    team.getRequestQueue().add(request);
                    log.info("All attendants of team {} are busy. Request '{}' has been queued.", team.getName(), request);
                });
    }
}
