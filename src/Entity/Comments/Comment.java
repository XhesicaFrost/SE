package Entity.Comments;

import Users.User;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class Comment {
    private final String comment;
    private final User author;
    private final LocalDateTime commentDate;
    private final ArrayList<Comment>replies;
    public Comment(String comment, User author) {
        this.comment = comment;
        this.author = author;
        this.commentDate = LocalDateTime.now();
        replies = new ArrayList<Comment>();
    }
    public String getComment() {
        return comment;
    }
    public User getAuthor() {
        return author;
    }
    public LocalDateTime getCommentDate() {
        return commentDate;
    }
    public ArrayList<Comment> getReplies() {
        return replies;
    }
}
