package com.moyeou.moyeoubackend.post.domain;

import com.moyeou.moyeoubackend.member.domain.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.post.domain.ParticipationStatus.ACCEPT;
import static com.moyeou.moyeoubackend.post.domain.ParticipationStatus.WAITING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

public class PostTest {

    @DisplayName("신청하고 작성자가 수락한다")
    @Test
    void 모집_신청_수락() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");

        var post = createPost(host, 3);
        Participation participation1 = post.attend(member1);
        Participation participation2 = post.attend(member2);

        // 작성자가 신청을 수락한다
        post.accept(host, participation1);
        post.accept(host, participation2);

        List<String> emails = post.getParticipations().stream()
                .map(Participation::getMember)
                .map(Member::getEmail)
                .collect(Collectors.toList());

        List<ParticipationStatus> status = post.getParticipations().stream()
                .map(Participation::getStatus)
                .collect(Collectors.toList());

        assertAll(
                () -> assertThat(emails).containsExactly("host@o.cnu.ac.kr", "member1@o.cnu.ac.kr", "member2@o.cnu.ac.kr"),
                () -> assertThat(post.getCurrentCount()).isEqualTo(3),
                () -> assertThat(post.getParticipations().size()).isEqualTo(3),
                () -> assertThat(status).allMatch(s -> s == ACCEPT)
        );
    }

    @DisplayName("신청하고 작성자가 아직 수락하지 않았다")
    @Test
    void 모집_신청_대기() {
        var host = createMember("host@o.cnu.ac.kr");
        var member = createMember("member1@o.cnu.ac.kr");

        var post = createPost(host, 3);
        Participation participation = post.attend(member);

        assertAll(
                () -> assertThat(post.getCurrentCount()).isEqualTo(1),
                () -> assertThat(post.getParticipations().size()).isEqualTo(2),
                () -> assertThat(participation.getStatus()).isEqualTo(WAITING)
        );
    }

    @DisplayName("신청하고 작성자가 거절한다")
    @Test
    void 모집_신청_거절() {
        var host = createMember("host@o.cnu.ac.kr");
        var member = createMember("member1@o.cnu.ac.kr");

        var post = createPost(host, 3);
        Participation participation = post.attend(member);

        post.reject(host, participation);

        assertAll(
                () -> assertThat(post.getCurrentCount()).isEqualTo(1),
                () -> assertThat(post.getParticipations().size()).isEqualTo(1)
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
        Participation participation1 = post.attend(member1);
        Participation participation2 = post.attend(member2);

        // 작성자가 신청 수락
        post.accept(host, participation1);
        post.accept(host, participation2);

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
        Participation participation1 = post.attend(member1);
        Participation participation2 = post.attend(member2);

        post.accept(host, participation1);
        post.accept(host, participation2);

        // member2 신청이 수락된 후 신청 취소
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

    @DisplayName("작성자가 모집을 완료한다")
    @Test
    void 모집_완료() {
        var host = createMember("host@o.cnu.ac.kr");
        var post = createPost(host, 3);

        // 모집 완료
        post.complete(host);

        assertThat(post.getStatus()).isEqualTo(PostStatus.COMPLETED);
    }

    @DisplayName("작성자가 스터디를 종료하고 평가가 생성된다")
    @Test
    void 스터디_종료() {
        var host = createMember("host@o.cnu.ac.kr");
        var member1 = createMember("member1@o.cnu.ac.kr");
        var member2 = createMember("member2@o.cnu.ac.kr");

        var post = createPost(host, 3);
        Participation participation1 = post.attend(member1);
        Participation participation2 = post.attend(member2);

        post.accept(host, participation1);
        post.accept(host, participation2);

        // 스터디 종료
        post.end(host);

        // 종료 후 평가 6개 생성
        List<String> evaluatorEmails = post.getEvaluations().stream()
                .map(evaluation -> evaluation.getEvaluator().getEmail())
                .collect(Collectors.toList());
        assertThat(post.getEvaluations().size()).isEqualTo(6);
        assertThat(evaluatorEmails).contains("host@o.cnu.ac.kr", "member1@o.cnu.ac.kr", "member2@o.cnu.ac.kr");
    }

    private Member createMember(String email) {
        return new Member(email, "컴퓨터융합학부", "nickname", "pw");
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
                .items(Arrays.asList("거주지", "성별"))
                .build();
    }
}
