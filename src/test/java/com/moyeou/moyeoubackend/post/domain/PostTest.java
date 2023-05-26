package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
                () -> assertThat(emails).containsExactly("host@o.cnu.ac.kr", "member1@o.cnu.ac.kr", "member2@o.cnu.ac.kr"),
                () -> assertThat(post.getCurrentCount()).isEqualTo(3),
                () -> assertThat(post.getParticipations().size()).isEqualTo(3)
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

        assertThatThrownBy(() -> post.attend(member3))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("모집 정원이 초과되었습니다.");
    }

    @DisplayName("신청을 취소한다")
    @Test
    void 신청_취소() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");

        var post = createPost(host, 3);
        post.attend(member1);
        post.attend(member2);

        // member2 신청 취소
        post.cancel(member2);

        List<String> emails = post.getParticipations().stream()
                .map(Participation::getMember)
                .map(Member::getEmail)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(emails).containsOnly("host@o.cnu.ac.kr", "member1@o.cnu.ac.kr"),
                () -> assertThat(post.getCurrentCount()).isEqualTo(2),
                () -> assertThat(post.getParticipations().size()).isEqualTo(2)
        );
    }

    @DisplayName("신청하지 않은 사람이 취소한다")
    @Test
    void 신청하지_않은_사람_취소() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");

        var post = createPost(host, 3);
        post.attend(member1);

        assertThatThrownBy(() -> post.cancel(member2))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("신청한 회원만 취소할 수 있습니다.");
    }

    private Member createMember(String email) {
        return new Member(email, "컴퓨터융합학부", 202000000, "nickname", "pw");
    }
    private Post createPost(Member host, Integer headCount) {
        return Post.builder()
                .title("JPA 스터디")
                .headCount(headCount)
                .operationWay("대면")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .host(host)
                .items(new ArrayList<>())
                .build();
    }
}
