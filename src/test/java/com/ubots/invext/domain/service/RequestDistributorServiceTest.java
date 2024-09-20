package com.ubots.invext.domain.service;

import com.ubots.invext.domain.entity.Attendant;
import com.ubots.invext.domain.entity.Team;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestDistributorServiceTest {

    @InjectMocks
    private RequestDistributorService requestDistributorService;

    @Mock
    private Map<String, Team> teams;

    private LogCaptor logCaptor;
    String requestType = "CARD ISSUES";
    String CARDS = "Cards";
    String ATTENDANT_NAME_1 = "Attendant 1";
    String ATTENDANT_NAME_2 = "Attendant 2";

    @BeforeEach
    void setUp() {
        logCaptor = LogCaptor.forClass(RequestDistributorService.class);
    }

    @Test
    void testDistributeRequest() {
        Attendant mockedAttendant = spy(new Attendant(ATTENDANT_NAME_1));
        Team cardsTeam = new Team(CARDS, List.of(mockedAttendant, new Attendant(ATTENDANT_NAME_2)));

        when(teams.get(CARDS)).thenReturn(cardsTeam);

        String result = requestDistributorService.distributeRequest(requestType);

        assertEquals("Request has been sent to the appropriate team.", result);
        verify(mockedAttendant).setCurrentRequests(1);
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Request 'CARD ISSUES' assigned to " + ATTENDANT_NAME_1 + " of team Cards")));
    }

    @Test
    void testDistributeRequestInQueue() {
        Attendant mockedAttendant = spy(new Attendant(ATTENDANT_NAME_1));
        mockedAttendant.setCurrentRequests(3);

        Attendant mockedAttendantSecond = new Attendant(ATTENDANT_NAME_2);
        mockedAttendantSecond.setCurrentRequests(3);

        Queue<String> requestQueue = spy(new LinkedList<>());
        Team cardsTeam = spy(new Team(CARDS, List.of(mockedAttendant, mockedAttendantSecond)));

        when(teams.get(CARDS)).thenReturn(cardsTeam);
        when(teams.get(CARDS).getRequestQueue()).thenReturn(requestQueue);

        String result = requestDistributorService.distributeRequest(requestType);

        assertEquals("Request has been sent to the appropriate team.", result);
        verify(mockedAttendant).setCurrentRequests(3);
        verify(requestQueue).add(requestType);
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("All attendants of team " + CARDS + " are busy. Request '" + requestType + "' has been queued.")), "");
    }

    @Test
    void testFinishRequestSuccess() {
        Attendant mockedAttendant = spy(new Attendant(ATTENDANT_NAME_1));
        mockedAttendant.setCurrentRequests(1);

        Team cardsTeam = new Team(CARDS, List.of(mockedAttendant, new Attendant(ATTENDANT_NAME_2)));
        when(teams.get(CARDS)).thenReturn(cardsTeam);

        String result = requestDistributorService.finishRequest(ATTENDANT_NAME_1, requestType);

        verify(mockedAttendant).setCurrentRequests(0);
        assertEquals("Request finished and queue checked for team Cards.", result);
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Service request closed from " + ATTENDANT_NAME_1 + ".")));
    }

    @Test
    void testFinishRequestAttendantNotFound() {
        String attendantName = "Unknown Attendant";

        Attendant mockedAttendant = spy(new Attendant(ATTENDANT_NAME_1));
        mockedAttendant.setCurrentRequests(1);

        Team cardsTeam = new Team(CARDS, List.of(mockedAttendant, new Attendant(ATTENDANT_NAME_2)));
        when(teams.get(CARDS)).thenReturn(cardsTeam);

        String result = requestDistributorService.finishRequest(attendantName, requestType);

        assertEquals(attendantName + " was not found for request type " + requestType + ".", result);
    }

    @Test
    void testFinishRequestAttendantNotFoundForRequestType() {
        Team cardsTeam = new Team(CARDS, List.of(new Attendant(ATTENDANT_NAME_1), new Attendant(ATTENDANT_NAME_2)));
        when(teams.get(CARDS)).thenReturn(cardsTeam);

        String result = requestDistributorService.finishRequest(ATTENDANT_NAME_1, requestType);

        assertEquals("All requests have been closed for team " + CARDS + ".", result);
    }

    @Test
    void testStartHandlingRequestCalledWithPolledRequest() {
        Attendant mockedAttendant = spy(new Attendant(ATTENDANT_NAME_1));
        mockedAttendant.setCurrentRequests(3);

        Attendant mockedAttendantSecond = new Attendant(ATTENDANT_NAME_2);
        mockedAttendantSecond.setCurrentRequests(3);

        Queue<String> requestQueue = spy(new LinkedList<>());
        requestQueue.add(requestType);

        Team cardsTeam = spy(new Team(CARDS, List.of(mockedAttendant, mockedAttendantSecond)));
        cardsTeam.setRequestQueue(requestQueue);

        requestDistributorService = new RequestDistributorService(Map.of(CARDS, cardsTeam));

        String result = requestDistributorService.finishRequest(ATTENDANT_NAME_1, requestType);

        verify(mockedAttendant, times(1)).setCurrentRequests(2);
        verify(mockedAttendant, times(2)).setCurrentRequests(3);
        assertEquals("Request finished and queue checked for team Cards.", result);
        assertTrue(logCaptor.getInfoLogs().stream()
                .anyMatch(log -> log.contains("Service request closed from " + ATTENDANT_NAME_1 + ".")));
    }
}
