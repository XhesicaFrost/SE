package com.blm.takeout.controller;

import com.blm.takeout.common.ApiResponse;
import com.blm.takeout.entity.Comment;
import com.blm.takeout.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/admin")
public class AdminCommentController {
    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @GetMapping("/shop/comments/{shopId}")
    public ApiResponse<?> getShopComments(@PathVariable Integer shopId) {
        try {
            List<Comment> comments = commentService.getCommentsByShopId(shopId);
            List<Map<String, Object>> commentList = comments.stream()
                .map(comment -> {
                    Map<String, Object> commentMap = new HashMap<>();
                    commentMap.put("id", comment.getId());
                    commentMap.put("username", comment.getUsername());
                    commentMap.put("content", comment.getContent());
                    commentMap.put("rating", comment.getRating());
                    commentMap.put("createTime", comment.getCreateTime());
                    commentMap.put("status", comment.getStatus());
                    return commentMap;
                })
                .collect(java.util.stream.Collectors.toList());
            
            return ApiResponse.success(commentList);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/comment/approve")
    public ApiResponse<?> approveComment(@RequestBody Map<String, Integer> request) {
        try {
            Integer commentId = request.get("commentId");
            if (commentId == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "评论ID不能为空");
            }
            
            commentService.approveComment(commentId);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }

    @PostMapping("/comment/reject")
    public ApiResponse<?> rejectComment(@RequestBody Map<String, Integer> request) {
        try {
            Integer commentId = request.get("commentId");
            if (commentId == null) {
                return ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "评论ID不能为空");
            }
            
            commentService.rejectComment(commentId);
            return ApiResponse.success(true);
        } catch (Exception e) {
            return ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage());
        }
    }
} 