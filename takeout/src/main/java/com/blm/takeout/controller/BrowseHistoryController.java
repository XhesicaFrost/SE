package com.blm.takeout.controller;

import com.blm.takeout.dto.BrowseHistoryDTO;
import com.blm.takeout.entity.BrowseHistory;
import com.blm.takeout.entity.BrowseHistory.TargetType;
import com.blm.takeout.service.BrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/browse-history")
public class BrowseHistoryController {

    @Autowired
    private BrowseHistoryService browseHistoryService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> addBrowseHistory(@RequestBody BrowseHistoryDTO request) {
        browseHistoryService.addBrowseHistory(
            Integer.parseInt(request.getUserId()),
            request.getTargetType(),
            request.getTargetId(),
            request.getName(),
            request.getImage(),
            request.getDescription()
        );
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "浏览记录添加成功");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<Map<String, Object>> getUserBrowseHistory(
            @PathVariable Integer userId,
            Pageable pageable) {
        Page<BrowseHistory> page = browseHistoryService.getUserBrowseHistory(userId, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/{userId}/type/{targetType}")
    public ResponseEntity<Map<String, Object>> getUserBrowseHistoryByType(
            @PathVariable Integer userId,
            @PathVariable TargetType targetType,
            Pageable pageable) {
        Page<BrowseHistory> page = browseHistoryService.getUserBrowseHistoryByType(userId, targetType, pageable);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("data", page);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/user/{userId}/type/{targetType}/target/{targetId}")
    public ResponseEntity<Map<String, Object>> deleteBrowseHistory(
            @PathVariable Integer userId,
            @PathVariable TargetType targetType,
            @PathVariable String targetId) {
        browseHistoryService.deleteBrowseHistory(userId, targetType, targetId);
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("success", true);
        response.put("message", "浏览记录删除成功");
        return ResponseEntity.ok(response);
    }
} 