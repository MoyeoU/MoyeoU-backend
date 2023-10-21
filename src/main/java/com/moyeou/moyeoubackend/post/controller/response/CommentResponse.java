package com.moyeou.moyeoubackend.post.controller.response;

import com.moyeou.moyeoubackend.member.domain.Member;
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
    private Long authorId;
    private String nickname;
    private String content;
    private LocalDateTime time;
    private Boolean isAuthor;

    public static CommentResponse from(Comment comment, Member viewer) {
        return new CommentResponse(
                comment.getId(),
                comment.getMember().getId(),
                comment.getMember().getNickname(),
                comment.getContent(),
                comment.getCreatedAt(),
                comment.isAuthor(viewer)
        );
    }
}
