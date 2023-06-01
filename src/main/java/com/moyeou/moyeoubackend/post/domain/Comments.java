package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.common.exception.UnAuthorizedException;
import com.moyeou.moyeoubackend.member.domain.Member;

import javax.persistence.Embeddable;
import javax.persistence.EntityNotFoundException;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static javax.persistence.CascadeType.ALL;

@Embeddable
public class Comments {
    @OneToMany(mappedBy = "post", cascade = ALL, orphanRemoval = true)
    private List<Comment> comments = new ArrayList<>();

    public void addComment(Post post, Member member, String comment) {
        this.comments.add(new Comment(post, member, comment));
    }

    public void updateComment(Member member, Long commentId, String content) {
        Comment comment = findComment(commentId);
        comment.update(member, content);
    }

    public void removeComment(Member member, Long commentId){
        Comment comment = findComment(commentId);
        if (!comment.isAuthor(member)) {
            throw new UnAuthorizedException("작성자만 삭제할 수 있습니다.");
        }
        comments.remove(comment);
    }

    public List<Comment> getComments() {
        return Collections.unmodifiableList(comments);
    }

    private Comment findComment(Long commentId) {
        return comments.stream()
                .filter(it -> it.getId().equals(commentId))
                .findAny()
                .orElseThrow(() -> new EntityNotFoundException("댓글(commentId: " + commentId + ")이 존재하지 않습니다."));
    }
}
