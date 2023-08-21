package com.moyeou.moyeoubackend.post.repository;

import com.moyeou.moyeoubackend.post.domain.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class PostQueryRepository {
    private final PostRepository postRepository;

    public List<Post> findAllByCondition(String title, Long categoryId, Long hashTagId, PostStatus status, Pageable pageable) {
        List<Post> posts = postRepository.findAll()
                .stream()
                .filter(post -> matches(post, title, categoryId, hashTagId, status))
                .collect(Collectors.toList());

        List<Post> results = new ArrayList<>();
        int start = pageable.getPageNumber() * pageable.getPageSize();
        int end = pageable.getPageNumber() * pageable.getPageSize() + pageable.getPageSize();
        int totalSize = posts.size();

        for (int index = start; index < end && index < totalSize; index++) {
            results.add(posts.get(index));
        }
        return results;
    }

    private boolean matches(Post post, String title, Long categoryId, Long hashTagId, PostStatus status) {
        return post.getTitle().contains(title)
                && post.getStatus() == status
                && 해시태그_일치(post, hashTagId)
                && 카테고리_일치(post, categoryId);
    }

    private boolean 카테고리_일치(Post post, Long categoryId) {
        if (categoryId == null) {
            return true;
        }
        return post.getPostHashtags()
                .stream()
                .map(PostHashtag::getHashtag)
                .map(Hashtag::getCategory)
                .map(Category::getId)
                .anyMatch(id -> id.equals(categoryId));
    }

    private boolean 해시태그_일치(Post post, Long hashTagId) {
        if (hashTagId == null) {
            return true;
        }
        return post.getPostHashtags()
                .stream()
                .map(PostHashtag::getHashtag)
                .map(Hashtag::getId)
                .anyMatch(id -> id.equals(hashTagId));
    }
}
