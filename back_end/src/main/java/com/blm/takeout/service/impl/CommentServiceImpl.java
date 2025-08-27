package com.blm.takeout.service.impl;

import com.blm.takeout.entity.Comment;
import com.blm.takeout.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public CommentServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Comment> getCommentsByShopId(Integer shopId) {
        String sql = "SELECT * FROM comment WHERE shop_id = ? ORDER BY create_time DESC";
        return jdbcTemplate.query(sql, new CommentRowMapper(), shopId);
    }

    @Override
    public void approveComment(Integer commentId) {
        String sql = "UPDATE comment SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, Comment.Status.已通过.name(), commentId);
    }

    @Override
    public void rejectComment(Integer commentId) {
        String sql = "UPDATE comment SET status = ? WHERE id = ?";
        jdbcTemplate.update(sql, Comment.Status.已拒绝.name(), commentId);
    }

    private static class CommentRowMapper implements RowMapper<Comment> {
        @Override
        public Comment mapRow(ResultSet rs, int rowNum) throws SQLException {
            Comment comment = new Comment();
            comment.setId(rs.getInt("id"));
            comment.setShopId(rs.getInt("shop_id"));
            comment.setUsername(rs.getString("username"));
            comment.setContent(rs.getString("content"));
            comment.setRating(rs.getInt("rating"));
            comment.setCreateTime(rs.getTimestamp("create_time").toLocalDateTime());
            comment.setStatus(rs.getString("status"));
            return comment;
        }
    }
} 