package com.laundry.controller;

import com.laundry.dto.InsightResponse;
import com.laundry.service.InsightService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/insights")
@RequiredArgsConstructor
@RestController
public class InsightController {

    private final InsightService insightService;

    @GetMapping
    public ResponseEntity<InsightResponse> getInsights(){
        return ResponseEntity.ok(insightService.getInsights());
    }
}
