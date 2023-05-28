package com.moyeou.moyeoubackend.post.controller.response;

import com.moyeou.moyeoubackend.post.domain.Comment;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CommentResponse {
    private Long commentId;
    private String nickname;
    private String content;
    private LocalDateTime time;

    public static CommentResponse from(Comment comment) {
        return new CommentResponse(
                comment.getId(), comment.getMember().getNickname(), comment.getContent(), comment.getCreatedAt()
        );
    }
}
