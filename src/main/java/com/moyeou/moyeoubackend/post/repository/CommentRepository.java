package com.moyeou.moyeoubackend.post.repository;

import com.moyeou.moyeoubackend.post.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
