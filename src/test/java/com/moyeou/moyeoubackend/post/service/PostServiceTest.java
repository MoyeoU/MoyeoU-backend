package com.moyeou.moyeoubackend.post.service;

import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Transactional
@SpringBootTest
public class PostServiceTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    PostService postService;

    @BeforeEach
    void setUp() {
        hashtagRepository.save(new Hashtag("Java"));
        hashtagRepository.save(new Hashtag("JPA"));
        hashtagRepository.save(new Hashtag("Spring"));
    }

    @DisplayName("게시물을 수정한다")
    @Test
    void updatePost() {
        Member member = saveMember("example@o.cnu.ac.kr");
        Post post = savePost(member);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .title("JPA 스터디")
                .headCount(3)
                .operationWay("온라인")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .hashtags(Arrays.asList("Java", "JPA"))
                .build();

        postService.update(post.getId(), member.getId(), updateRequest);

        List<String> hashtags = post.getPostHashtags().stream()
                .map(PostHashtag -> PostHashtag.getHashtag().getName())
                .collect(Collectors.toList());
        assertThat(post.getOperationWay()).isEqualTo("온라인");
        assertThat(hashtags).containsExactly("Java", "JPA");
    }

    @DisplayName("게시물을 삭제한다")
    @Test
    void deletePost() {
        Member member = saveMember("example@o.cnu.ac.kr");
        Post post = savePost(member);

        postService.delete(post.getId(), member.getId());
        assertThat(postRepository.findById(post.getId()).isEmpty()).isTrue();
    }

    private Member saveMember(String email) {
        return memberRepository.save(new Member(email, "컴퓨터융합학부", 202000000, "nickname", "pw"));
    }

    private Post savePost(Member host) {
        return postRepository.save(Post.builder()
                .title("JPA 스터디")
                .headCount(3)
                .operationWay("대면")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .host(host)
                .items(new ArrayList<>())
                .build());
    }
}
