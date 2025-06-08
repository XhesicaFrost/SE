package com.blm.takeout.service;

import com.blm.takeout.entity.Comment;
import java.util.List;

public interface CommentService {
    List<Comment> getCommentsByShopId(Integer shopId);
    void approveComment(Integer commentId);
    void rejectComment(Integer commentId);
} 