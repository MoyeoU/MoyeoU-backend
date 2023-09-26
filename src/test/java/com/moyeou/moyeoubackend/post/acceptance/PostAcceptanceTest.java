package com.moyeou.moyeoubackend.post.acceptance;

import com.jayway.jsonpath.JsonPath;
import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.post.controller.request.*;
import com.moyeou.moyeoubackend.post.domain.Item;
import com.moyeou.moyeoubackend.post.domain.event.AcceptEvent;
import com.moyeou.moyeoubackend.post.domain.event.AttendEvent;
import com.moyeou.moyeoubackend.post.domain.event.CommentEvent;
import com.moyeou.moyeoubackend.post.domain.event.EndEvent;
import com.moyeou.moyeoubackend.post.repository.CommentRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.event.ApplicationEvents;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.moyeou.moyeoubackend.TestUtils.id;
import static com.moyeou.moyeoubackend.TestUtils.uri;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RecordApplicationEvents
public class PostAcceptanceTest extends AcceptanceTest {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ApplicationEvents events;

    @DisplayName("게시물을 생성하고, 조회한다.")
    @Test
    void createAndFind() throws Exception {
        // member1, member2 회원가입, 로그인
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        // member1 : 게시물 생성
        var post = createPost(member1)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/posts")))
                .andReturn().getResponse().getHeader("Location");

        // member2 : 게시물 조회
        findPost(member2, post)
                .andExpect(jsonPath("$.content").value("<h1>같이 공부해요!</h1>"))
                .andExpect(jsonPath("$.isHost").value(false))
                .andExpect(jsonPath("$.hashtags", containsInAnyOrder("백엔드", "프론트엔드", "코딩테스트")));
    }

    @DisplayName("게시물 전체 조회")
    @Test
    void 전체보기() throws Exception {
        createPosts();

        findPosts(null, null, null, "PROGRESS")
                .andExpect(jsonPath("$", hasSize(6)));
    }

    @DisplayName("제목 검색")
    @Test
    void 제목() throws Exception {
        createPosts();

        // "코테" -> post1, post2
        findPosts(null, null, "코테", "PROGRESS")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].title", everyItem(containsString("코테"))));
    }

    @DisplayName("카테고리로 조회")
    @Test
    void 카테고리() throws Exception {
        createPosts();

        // "프로그래밍" 카테고리 -> post1, post2, post4, post5
        findPosts(2L, null, null, "PROGRESS")
                .andExpect(jsonPath("$", hasSize(4)));
    }

    @DisplayName("모집상태로 조회")
    @Test
    void 모집상태() throws Exception {
        createPostsAndUpdateStatus();

        // "모집완료" -> post1, post5
        findPosts(null, null, null, "COMPLETED")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("코테 스터디")))
                .andExpect(jsonPath("$[1].title", is("iOS 공부해요")));
    }

    @DisplayName("카테고리, 해시태그로 조회")
    @Test
    void 카테고리_해시태그() throws Exception {
        createPostsAndUpdateStatus();

        // "어학" 카테고리, "토익" 해시태그 -> post3, post6
        findPosts(1L, 1L, null, "PROGRESS")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].hashtags", everyItem(hasItem("토익"))));
    }

    @DisplayName("카테고리, 제목으로 조회")
    @Test
    void 카테고리_제목() throws Exception {
        createPostsAndUpdateStatus();

        // "프로그래밍" 카테고리, "스터디" -> post4
        findPosts(2L, null, "스터디", "PROGRESS")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("스프링 스터디")));
    }

    @DisplayName("카테고리, 모집상태로 조회")
    @Test
    void 카테고리_모집상태() throws Exception {
        createPostsAndUpdateStatus();

        // "프로그래밍" 카테고리, "모집완료" -> post1, post5
        findPosts(2L, null, null, "COMPLETED")
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("코테 스터디")))
                .andExpect(jsonPath("$[1].title", is("iOS 공부해요")));
    }

    @DisplayName("제목, 모집상태로 조회")
    @Test
    void 제목_모집상태() throws Exception {
        createPostsAndUpdateStatus();

        // "어학", "모집완료" -> 없음
        findPosts(null, null, "어학", "COMPLETED")
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DisplayName("카테고리, 해시태그, 제목으로 조회")
    @Test
    void 카테고리_해시태그_제목() throws Exception {
        createPostsAndUpdateStatus();

        // "프로그래밍" 카테고리, "코딩테스트" 해시태그, "스프링" -> post4
        findPosts(2L, 10L, "스프링", "PROGRESS")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("스프링 스터디")));
    }

    @DisplayName("카테고리, 해시태그, 모집상태로 조회")
    @Test
    void 카테고리_해시태그_모집상태() throws Exception {
        createPostsAndUpdateStatus();

        // "프로그래밍" 카테고리, "코딩테스트" 해시태그, "모집완료" -> post1
        findPosts(2L, 10L, null, "COMPLETED")
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].title", is("코테 스터디")));
    }

    @DisplayName("카테고리, 해시태그, 제목, 모집상태로 조회")
    @Test
    void 카테고리_해시태그_제목_모집상태() throws Exception {
        createPostsAndUpdateStatus();

        // "어학" 카테고리, "토익 스피킹" 해시태그, "토스", "모집완료" -> 없음
        findPosts(1L, 2L, "토스", "COMPLETED")
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @DisplayName("회원이 모집에 신청하고, 작성자는 신청을 수락한다")
    @Test
    void attend() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        // host가 게시물 생성
        var post = createPost(host);

        // member가 신청
        var participation = attend(uri(post), member)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(uri(post) + "/participations/")));

        // host가 신청 수락
        mockMvc.perform(post(uri(participation) + "/accept")
                .header("Authorization", "Bearer " + host));

        // 참가 이벤트 제대로 발행되었는지 확인
        List<Long> attendEvent = events.stream(AttendEvent.class)
                .map(AttendEvent::getPostId)
                .collect(Collectors.toList());
        assertThat(attendEvent).containsExactly(id(post));
    }

    @DisplayName("작성자가 스터디에 참여한다.")
    @Test
    void attendByHost() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");

        // host가 게시물 생성
        var post = createPost(host);

        // host가 신청 -> exception
        attend(uri(post), host)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4007"));
    }

    @DisplayName("작성자가 스터디를 종료하고, 스터디원은 내가 평가해야 할 목록을 조회한다.")
    @Test
    void end() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "작성자");
        var member1 = signUpLogin("member1@o.cnu.ac.kr", "password", "회원1");
        var member2 = signUpLogin("member2@o.cnu.ac.kr", "password", "회원2");

        var response = createPost(host);
        var postUri = uri(response);

        var participation1 = attend(postUri, member1);
        var participation2 = attend(postUri, member2);

        // 스터디 신청 수락
        mockMvc.perform(post(uri(participation1) + "/accept")
                .header("Authorization", "Bearer " + host));
        mockMvc.perform(post(uri(participation2) + "/accept")
                .header("Authorization", "Bearer " + host));

        // 스터디 종료
        mockMvc.perform(post(postUri + "/end")
                        .header("Authorization", "Bearer " + host))
                .andExpect(status().isOk());

        // member1이 평가해야 할 스터디원 목록 조회
        mockMvc.perform(get(postUri + "/evaluations")
                        .header("Authorization", "Bearer " + member1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].member.nickname").value("작성자"))
                .andExpect(jsonPath("$[1].member.nickname").value("회원2"));

        // 수락 이벤트 제대로 발행되었는지 확인
        List<Long> acceptEvent = events.stream(AcceptEvent.class)
                .map(AcceptEvent::getPostId)
                .collect(Collectors.toList());

        // 종료 이벤트 제대로 발행되었는지 확인
        List<Long> endEvent = events.stream(EndEvent.class)
                .map(EndEvent::getPostId)
                .collect(Collectors.toList());

        assertThat(acceptEvent).containsExactly(id(postUri), id(postUri));
        assertThat(acceptEvent.size()).isEqualTo(2);
        assertThat(endEvent).containsExactly(id(postUri));
    }

    @DisplayName("회원이 댓글을 작성한다")
    @Test
    void 댓글_작성() throws Exception {
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        var response = createPost(member1);
        var postUri = uri(response);

        createComment(postUri + "/comments", "하이", member2);

        findPost(member1, postUri)
                .andExpect(jsonPath("$.comments[0].nickname").value("회원2"))
                .andExpect(jsonPath("$.comments[0].content").value("하이"));

        // 댓글 이벤트 제대로 발행되었는지 확인
        List<Long> commentEvent = events.stream(CommentEvent.class)
                .map(CommentEvent::getPostId)
                .collect(Collectors.toList());
        assertThat(commentEvent).containsExactly(id(postUri));
    }

    @DisplayName("댓글을 삭제한다")
    @Test
    void 댓글_삭제() throws Exception {
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        var response = createPost(member1);
        var postUri = uri(response);

        // member2가 댓글 2개 작성
        createComment(postUri + "/comments", "댓글1", member2);
        createComment(postUri + "/comments", "댓글2", member2);

        // member2가 댓글 하나 삭제
        Long id = commentRepository.findAll().get(0).getId();
        mockMvc.perform(MockMvcRequestBuilders.delete(postUri + "/comments/" + id)
                        .header("Authorization", "Bearer " + member2))
                .andExpect(status().isOk());

        // 댓글 하나 남음
        findPost(member1, postUri)
                .andExpect(jsonPath("$.comments.length()").value(1));
    }

    @DisplayName("댓글을 수정한다")
    @Test
    void 댓글_수정() throws Exception {
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        var response = createPost(member1);
        var postUri = uri(response);

        createComment(postUri + "/comments", "댓글1", member2);

        Long id = commentRepository.findAll().get(0).getId();
        mockMvc.perform(put(postUri + "/comments/" + id)
                        .header("Authorization", "Bearer " + member2)
                        .content(objectMapper.writeValueAsString(new CommentUpdateRequest("댓글 수정")))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        findPost(member1, postUri)
                .andExpect(jsonPath("$.comments[0].content").value("댓글 수정"));
    }

    private String signUpLogin(String email, String password, String nickname) throws Exception {
        var request = SignUpRequest.builder()
                .email(email).department("컴퓨터융합학부")
                .nickname(nickname).password(password).build();
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        var response = mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        return JsonPath.read(response, "$.accessToken");
    }

    private ResultActions createPost(String token) throws Exception {
        var post = CreateRequest.builder()
                .title("JPA 스터디").headCount(4).operationWay("대면")
                .expectedDate("06-01").estimatedDuration("3개월").content("<h1>같이 공부해요!</h1>")
                .hashtags(Arrays.asList("백엔드", "프론트엔드", "코딩테스트"))
                .items(Arrays.asList("나이", "성별", "거주지"))
                .build();
        return mockMvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(post))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions createPost(String token, String title, List<String> hashtags) throws Exception {
        var post = CreateRequest.builder()
                .title(title).headCount(4).operationWay("대면")
                .expectedDate("06-01").estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>").hashtags(hashtags)
                .items(Arrays.asList("나이", "성별", "거주지"))
                .build();
        return mockMvc.perform(post("/posts")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(post))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions findPost(String token, String location) throws Exception {
        return mockMvc.perform(get(location)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    private Object createAttendRequest(Long postId) {
        List<AnswerRequest> answers = new ArrayList<>();
        var post = postRepository.findById(postId).get();
        for (Item item : post.getItems()) {
            AnswerRequest answerRequest = new AnswerRequest(item.getId(), "대전");
            answers.add(answerRequest);
        }
        return new AttendRequest(answers);
    }

    private ResultActions attend(String uri, String token) throws Exception {
        Long postId = id(uri);
        return mockMvc.perform(post(uri + "/attend")
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(createAttendRequest(postId)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions createComment(String uri, String content, String token) throws Exception {
        return mockMvc.perform(post(uri)
                .header("Authorization", "Bearer " + token)
                .content(objectMapper.writeValueAsString(new CommentRequest(content)))
                .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions findPosts(Long categoryId, Long hashTagId, String title, String status) throws Exception {
        MockHttpServletRequestBuilder mockHttpServletRequestBuilder = get("/posts?page=0&size=12");
        if (categoryId != null) {
            mockHttpServletRequestBuilder.param("categoryId", String.valueOf(categoryId));
        }
        if (hashTagId != null) {
            mockHttpServletRequestBuilder.param("hashTagId", String.valueOf(hashTagId));
        }
        if (title != null) {
            mockHttpServletRequestBuilder.param("title", title);
        }
        if (status != null) {
            mockHttpServletRequestBuilder.param("status", status);
        }
        return mockMvc.perform(mockHttpServletRequestBuilder)
                .andExpect(status().isOk());
    }

    void createPosts() throws Exception {
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        createPost(member1, "코테 스터디", Arrays.asList("코딩테스트")); // post1
        createPost(member2, "코테준비해요!", Arrays.asList("코딩테스트")); // post2
        createPost(member1, "어학 스터디", Arrays.asList("토익", "토플")); // post3
        createPost(member2, "스프링 스터디", Arrays.asList("백엔드", "코딩테스트")); // post4
        createPost(member1, "iOS 공부해요", Arrays.asList("모바일")); // post5
        createPost(member2, "같이 토스?", Arrays.asList("토익 스피킹", "토익")); // post6
    }

    void createPostsAndUpdateStatus() throws Exception {
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        var post1 = createPost(member1, "코테 스터디", Arrays.asList("코딩테스트")); // post1
        createPost(member2, "코테준비해요!", Arrays.asList("코딩테스트")); // post2
        createPost(member1, "어학 스터디", Arrays.asList("토익", "토플")); // post3
        createPost(member2, "스프링 스터디", Arrays.asList("백엔드", "코딩테스트")); // post4
        var post5 = createPost(member1, "iOS 공부해요", Arrays.asList("모바일")); // post5
        createPost(member2, "같이 토스?", Arrays.asList("토익 스피킹", "토익")); // post6

        // post1, post5의 모집상태를 모집완료로 변경.
        var post1Uri = uri(post1);
        var post5Uri = uri(post5);
        mockMvc.perform(post(post1Uri + "/complete")
                        .header("Authorization", "Bearer " + member1))
                .andExpect(status().isOk());
        mockMvc.perform(post(post5Uri + "/complete")
                        .header("Authorization", "Bearer " + member1))
                .andExpect(status().isOk());
    }
}
