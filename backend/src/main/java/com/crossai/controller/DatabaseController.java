package com.crossai.controller;

import com.crossai.dto.FlexibleInputDTO;
import com.crossai.dto.MultimodalContentDTO;
import com.crossai.service.EnhancedDatabaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {
    
    private final EnhancedDatabaseService databaseService;
    
    @PostMapping("/inputs")
    public ResponseEntity<FlexibleInputDTO> saveFlexibleInput(@RequestBody FlexibleInputDTO inputDTO) {
        FlexibleInputDTO saved = databaseService.saveFlexibleInput(inputDTO);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/inputs/user/{userId}")
    public ResponseEntity<Page<FlexibleInputDTO>> getFlexibleInputsByUser(
            @PathVariable Long userId) {
        Page<FlexibleInputDTO> result = databaseService.getFlexibleInputsByUser(userId, null);
        return ResponseEntity.ok(result);
    }
    
    @PostMapping("/content")
    public ResponseEntity<MultimodalContentDTO> saveMultimodalContent(@RequestBody MultimodalContentDTO contentDTO) {
        MultimodalContentDTO saved = databaseService.saveMultimodalContent(contentDTO);
        return ResponseEntity.ok(saved);
    }
    
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> getDatabaseHealth() {
        boolean healthy = databaseService.isDatabaseHealthy();
        return ResponseEntity.ok(Map.of("healthy", healthy));
    }
}