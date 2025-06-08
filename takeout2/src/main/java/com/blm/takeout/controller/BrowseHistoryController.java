package com.blm.takeout.controller;

import com.blm.takeout.dto.BrowseHistoryDTO;
import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.service.BrowseHistoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/browse-history")
public class BrowseHistoryController {
    private final BrowseHistoryService browseHistoryService;

    public BrowseHistoryController(BrowseHistoryService browseHistoryService) {
        this.browseHistoryService = browseHistoryService;
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> addBrowseHistory(
            @RequestParam Integer userId,
            @RequestParam BrowseHistory.TargetType targetType,
            @RequestParam Integer targetId) {
        browseHistoryService.addBrowseHistory(userId, targetType, targetId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<BrowseHistoryDTO>> getUserBrowseHistory(
            @PathVariable Integer userId,
            Pageable pageable) {
        Page<BrowseHistoryDTO> histories = browseHistoryService.getUserBrowseHistory(userId, pageable);
        return ResponseEntity.ok(histories);
    }

    @GetMapping("/user/{userId}/type/{targetType}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Page<BrowseHistoryDTO>> getUserBrowseHistoryByType(
            @PathVariable Integer userId,
            @PathVariable BrowseHistory.TargetType targetType,
            Pageable pageable) {
        Page<BrowseHistoryDTO> histories = browseHistoryService.getUserBrowseHistoryByType(userId, targetType, pageable);
        return ResponseEntity.ok(histories);
    }

    @DeleteMapping("/user/{userId}/type/{targetType}/target/{targetId}")
    @PreAuthorize("hasRole('ADMIN') or #userId == authentication.principal.id")
    public ResponseEntity<Void> deleteBrowseHistory(
            @PathVariable Integer userId,
            @PathVariable BrowseHistory.TargetType targetType,
            @PathVariable Integer targetId) {
        browseHistoryService.deleteBrowseHistory(userId, targetType, targetId);
        return ResponseEntity.ok().build();
    }
} 