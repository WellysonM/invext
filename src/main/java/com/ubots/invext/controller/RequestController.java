package com.ubots.invext.controller;

import com.ubots.invext.domain.service.RequestDistributorService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/api/requests")
public class RequestController {

    private final RequestDistributorService requestDistributorService;

    @PostMapping("/distribute")
    public ResponseEntity<String> distributeRequest(@RequestParam String requestType) {
       return ResponseEntity.ok(requestDistributorService.distributeRequest(requestType));
    }

    @PostMapping("/finish")
    public ResponseEntity<String> finishRequest(@RequestParam String attendantName, @RequestParam String requestType) {
        return ResponseEntity.ok(requestDistributorService.finishRequest(attendantName, requestType));
    }
}
