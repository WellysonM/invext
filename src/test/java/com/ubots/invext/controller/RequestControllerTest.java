package com.ubots.invext.controller;


import com.ubots.invext.domain.service.RequestDistributorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@ExtendWith(MockitoExtension.class)
public class RequestControllerTest {

    @Mock
    private RequestDistributorService requestDistributorService;

    @InjectMocks
    private RequestController requestController;

    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(requestController)
                .alwaysDo(print())
                .build();
        Mockito.reset(requestDistributorService);
    }

    @Test
    public void testDistributeRequestSuccess() throws Exception {
        String requestType = "Card issues";
        String expectedResponse = "Request has been sent to the appropriate team.";
        when(requestDistributorService.distributeRequest(requestType)).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/requests/distribute")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("requestType", requestType))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse))
                .andReturn();

        verify(requestDistributorService, times(1)).distributeRequest(requestType);
        verifyNoMoreInteractions(requestDistributorService);
    }

    @Test
    public void testFinishRequestSuccess() throws Exception {
        String attendantName = "Attendant 2";
        String requestType = "Card issues";
        String expectedResponse = "Request finished and queue checked.";
        when(requestDistributorService.finishRequest(attendantName, requestType)).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/requests/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("requestType", requestType)
                        .param("attendantName", attendantName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse))
                .andReturn();

        verify(requestDistributorService, times(1)).finishRequest(attendantName, requestType);
        verifyNoMoreInteractions(requestDistributorService);
    }

    @Test
    public void testFinishRequestFailure() throws Exception {
        String attendantName = "Attendant 3";
        String requestType = "Card issues";
        String expectedResponse = "Attendant 3 was not found for request type Card issues.";
        when(requestDistributorService.finishRequest(attendantName, requestType)).thenReturn(expectedResponse);

        mockMvc.perform(post("/api/requests/finish")
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("requestType", requestType)
                        .param("attendantName", attendantName))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponse))
                .andReturn();

        verify(requestDistributorService, times(1)).finishRequest(attendantName, requestType);
        verifyNoMoreInteractions(requestDistributorService);
    }
}
