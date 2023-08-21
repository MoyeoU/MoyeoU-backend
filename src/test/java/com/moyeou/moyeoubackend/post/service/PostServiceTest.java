package com.moyeou.moyeoubackend.post.service;

import com.moyeou.moyeoubackend.IntegrationTest;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.post.controller.response.ItemResponse;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

public class PostServiceTest extends IntegrationTest {
    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PostRepository postRepository;
    @Autowired
    HashtagRepository hashtagRepository;
    @Autowired
    PostService postService;

    @DisplayName("게시물을 수정한다")
    @Test
    void updatePost() {
        Member member = saveMember("example@o.cnu.ac.kr");
        Post post = savePost(member);
        UpdateRequest updateRequest = UpdateRequest.builder()
                .title("JPA 스터디").headCount(3).operationWay("온라인")
                .expectedDate("06-01").estimatedDuration("3개월").content("<h1>같이 공부해요!</h1>")
                .hashtags(Arrays.asList("백엔드", "코딩테스트")).items(Arrays.asList("거주지", "들은 강의"))
                .build();

        postService.update(post.getId(), member.getId(), updateRequest);

        List<String> hashtags = post.getPostHashtags().stream()
                .map(PostHashtag -> PostHashtag.getHashtag().getName())
                .collect(Collectors.toList());
        List<String> items = post.getItems().stream()
                .map(item -> item.getName())
                .collect(Collectors.toList());

        assertThat(post.getOperationWay()).isEqualTo("온라인");
        assertThat(hashtags).containsExactly("백엔드", "코딩테스트");
        assertThat(items).containsExactly("거주지", "들은 강의");
    }

    @DisplayName("게시물을 삭제한다")
    @Test
    void deletePost() {
        Member member = saveMember("example@o.cnu.ac.kr");
        Post post = savePost(member);

        postService.delete(post.getId(), member.getId());
        assertThat(postRepository.findById(post.getId()).isEmpty()).isTrue();
    }

    @DisplayName("신청폼을 조회한다")
    @Test
    void 신청폼_조회() {
        Member member1 = saveMember("member1@o.cnu.ac.kr");
        Post post = savePost(member1);

        List<ItemResponse> form = postService.findForm(post.getId());

        assertThat(form.size()).isEqualTo(2);
        assertThat(form.stream().map(ItemResponse::getItemName)).contains("거주지", "성별");
    }

    private Member saveMember(String email) {
        return memberRepository.save(new Member(email, "컴퓨터융합학부", "nickname", "pw"));
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
                .items(Arrays.asList("거주지", "성별"))
                .build());
    }
}
