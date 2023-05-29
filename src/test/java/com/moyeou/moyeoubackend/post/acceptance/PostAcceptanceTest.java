package com.moyeou.moyeoubackend.post.acceptance;

import com.jayway.jsonpath.JsonPath;
import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.post.controller.request.AnswerRequest;
import com.moyeou.moyeoubackend.post.controller.request.AttendRequest;
import com.moyeou.moyeoubackend.post.controller.request.CommentRequest;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.domain.Item;
import com.moyeou.moyeoubackend.post.repository.CommentRepository;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.moyeou.moyeoubackend.TestUtils.id;
import static com.moyeou.moyeoubackend.TestUtils.uri;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostAcceptanceTest extends AcceptanceTest {
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CommentRepository commentRepository;

    @BeforeEach
    void setUp() {
        hashtagRepository.save(new Hashtag("Java"));
        hashtagRepository.save(new Hashtag("JPA"));
        hashtagRepository.save(new Hashtag("Spring"));
    }

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
                .andExpect(jsonPath("$.hashtags", containsInAnyOrder("Java", "JPA", "Spring")));
    }

    @DisplayName("스터디에 참여한다.")
    @Test
    void attend() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");
        var member = signUpLogin("ex@o.cnu.ac.kr", "password", "회원2");

        // host가 게시물 생성
        var response = createPost(host);
        var postUri = uri(response);

        // member가 신청
        attend(postUri, member)
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(postUri + "/participations/")));

        // 현재 참여 인원 : 2
        findPost(member, postUri)
                .andExpect(jsonPath("$.currentCount").value(2));
    }

    @DisplayName("작성자가 스터디에 참여한다.")
    @Test
    void attendByHost() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "회원1");

        // host가 게시물 생성
        var response = createPost(host);
        var postUri = uri(response);

        // host가 신청 -> exception
        attend(postUri, host)
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4007"));
    }

    @DisplayName("작성자가 스터디를 종료하고, 내가 평가해야 할 스터디원 목록을 조회한다.")
    @Test
    void end() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw", "작성자");
        var member1 = signUpLogin("member1@o.cnu.ac.kr", "password", "회원1");
        var member2 = signUpLogin("member2@o.cnu.ac.kr", "password", "회원2");

        var response = createPost(host);
        var postUri = uri(response);

        attend(postUri, member1);
        attend(postUri, member2);

        // 스터디 종료
        mockMvc.perform(post(postUri + "/end")
                        .header("Authorization", "Bearer " + host))
                .andExpect(status().isOk());

        // 평가해야 할 스터디원 목록 조회
        mockMvc.perform(get(postUri + "/evaluations")
                        .header("Authorization", "Bearer " + member1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].member.nickname").value("작성자"))
                .andExpect(jsonPath("$[1].member.nickname").value("회원2"));
    }

    @DisplayName("댓글을 작성한다")
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

    private String signUpLogin(String email, String password, String nickname) throws Exception {
        var request = SignUpRequest.builder()
                .email(email).department("컴퓨터융합학부").studentNumber(202000000)
                .nickname(nickname).password(password).build();
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        var response =  mockMvc.perform(post("/login")
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
                .hashtags(Arrays.asList("Java", "JPA", "Spring"))
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
        entityManager.flush();
        entityManager.clear();
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
}
