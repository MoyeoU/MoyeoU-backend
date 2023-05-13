package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.post.domain.PostStatus.PROGRESS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    @DisplayName("신청한다")
    @Test
    void 모집_신청() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");

        var post = createPost(host, 3);
        post.attend(member1);
        post.attend(member2);

        List<String> emails = post.getParticipations().stream()
                .map(Participation::getMember)
                .map(Member::getEmail)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(emails).containsExactly("member1@o.cnu.ac.kr", "member2@o.cnu.ac.kr"),
                () -> assertThat(post.getCurrentCount()).isEqualTo(3),
                () -> assertThat(post.getParticipations().size()).isEqualTo(2)
        );
    }

    @DisplayName("정원을 초과해서 신청할 수 없다")
    @Test
    void 모집_정원_초과() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");
        var member3 = createMember("member3@o.cnu.ac.kr");

        // 모집 정원 3명
        var post = createPost(host, 3);
        post.attend(member1);
        post.attend(member2);

        Assertions.assertThatThrownBy(
                () -> post.attend(member3)
        ).isInstanceOf(IllegalStateException.class);
    }

    private Member createMember(String email) {
        return new Member(email, "컴퓨터융합학부", 202000000, "nickname", "pw");
    }
    private Post createPost(Member host, Integer headCount) {
        return Post.builder()
                .title("JPA 스터디")
                .headCount(headCount)
                .currentCount(1)
                .operationWay("대면")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .status(PROGRESS)
                .host(host)
                .postHashtags(new ArrayList<>())
                .build();
    }
}