package com.blm.takeout;

import com.blm.takeout.entity.Comment;
import com.blm.takeout.service.impl.CommentServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestCommentService {

    private JdbcTemplate jdbcTemplate;
    private CommentServiceImpl commentService;

    @BeforeEach
    void setUp() {
        jdbcTemplate = mock(JdbcTemplate.class);
        commentService = new CommentServiceImpl(jdbcTemplate);
    }

    @Test
    void getCommentsByShopId_success() {
        Comment comment = new Comment();
        comment.setId(1);
        comment.setShopId(2);
        comment.setUsername("用户A");
        comment.setContent("很好");
        comment.setRating(5);
        comment.setCreateTime(LocalDateTime.now());
        comment.setStatus("已通过");

        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(2)))
                .thenReturn(List.of(comment));

        List<Comment> result = commentService.getCommentsByShopId(2);
        assertEquals(1, result.size());
        assertEquals("用户A", result.get(0).getUsername());
    }

    @Test
    void getCommentsByShopId_empty() {
        when(jdbcTemplate.query(anyString(), any(RowMapper.class), eq(99)))
                .thenReturn(List.of());
        List<Comment> result = commentService.getCommentsByShopId(99);
        assertTrue(result.isEmpty());
    }

    @Test
    void approveComment_success() {
        when(jdbcTemplate.update(anyString(), eq(Comment.Status.已通过.name()), eq(1))).thenReturn(1);
        assertDoesNotThrow(() -> commentService.approveComment(1));
        verify(jdbcTemplate).update(anyString(), eq(Comment.Status.已通过.name()), eq(1));
    }

    @Test
    void approveComment_dbError() {
        when(jdbcTemplate.update(anyString(), eq(Comment.Status.已通过.name()), eq(1)))
                .thenThrow(new RuntimeException("数据库错误"));
        Exception ex = assertThrows(RuntimeException.class, () -> commentService.approveComment(1));
        assertEquals("数据库错误", ex.getMessage());
    }

    @Test
    void rejectComment_success() {
        when(jdbcTemplate.update(anyString(), eq(Comment.Status.已拒绝.name()), eq(2))).thenReturn(1);
        assertDoesNotThrow(() -> commentService.rejectComment(2));
        verify(jdbcTemplate).update(anyString(), eq(Comment.Status.已拒绝.name()), eq(2));
    }

    @Test
    void rejectComment_dbError() {
        when(jdbcTemplate.update(anyString(), eq(Comment.Status.已拒绝.name()), eq(2)))
                .thenThrow(new RuntimeException("数据库错误"));
        Exception ex = assertThrows(RuntimeException.class, () -> commentService.rejectComment(2));
        assertEquals("数据库错误", ex.getMessage());
    }
}
