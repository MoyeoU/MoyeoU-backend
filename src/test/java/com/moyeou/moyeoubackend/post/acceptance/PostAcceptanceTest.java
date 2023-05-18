package com.moyeou.moyeoubackend.post.acceptance;

import com.jayway.jsonpath.JsonPath;
import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PostAcceptanceTest extends AcceptanceTest {
    @Autowired
    private static HashtagRepository hashtagRepository;

    @BeforeAll
    static void setUp() {
        hashtagRepository.save(new Hashtag("Java"));
        hashtagRepository.save(new Hashtag("JPA"));
        hashtagRepository.save(new Hashtag("Spring"));
    }

    @DisplayName("게시물을 생성하고, 조회한다.")
    @Test
    void createAndFind() throws Exception {
        // member1, member2 회원가입, 로그인
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password");

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
        var host = signUpLogin("example@o.cnu.ac.kr", "pw");
        var member = signUpLogin("ex@o.cnu.ac.kr", "password");

        // host가 게시물 생성
        var post = createPost(host)
                .andReturn().getResponse().getHeader("Location");

        // member가 신청
        mockMvc.perform(post(post + "/attend")
                        .header("Authorization", "Bearer " + member))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith(post + "/participations/")));

        // 현재 참여 인원 : 2
        findPost(member, post)
                .andExpect(jsonPath("$.currentCount").value(2));
    }

    @DisplayName("작성자가 스터디에 참여한다.")
    @Test
    void attendByHost() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw");

        // host가 게시물 생성
        var post = createPost(host)
                .andReturn().getResponse().getHeader("Location");

        // host가 신청 -> exception
        mockMvc.perform(post(post + "/attend")
                        .header("Authorization", "Bearer " + host))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4007"));
    }

    @DisplayName("작성자가 스터디를 종료하고, 내가 평가해야 할 스터디원 목록을 조회한다.")
    @Test
    void end() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw");
        var member1 = signUpLogin("member1@o.cnu.ac.kr", "password");
        var member2 = signUpLogin("member2@o.cnu.ac.kr", "password");

        var post = createPost(host)
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(post(post + "/attend")
                .header("Authorization", "Bearer " + member1));
        mockMvc.perform(post(post + "/attend")
                .header("Authorization", "Bearer " + member2));

        // 스터디 종료
        mockMvc.perform(post(post + "/end")
                        .header("Authorization", "Bearer " + host))
                .andExpect(status().isOk());

        // 평가해야 할 스터디원 목록 조회
        mockMvc.perform(get(post + "/evaluations")
                        .header("Authorization", "Bearer " + member1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].member.email").value("example@o.cnu.ac.kr"))
                .andExpect(jsonPath("$[1].member.email").value("member2@o.cnu.ac.kr"));
    }

    private String signUpLogin(String email, String password) throws Exception {
        var request = SignUpRequest.builder()
                .email(email)
                .department("컴퓨터융합학부")
                .studentNumber(202000000)
                .nickname("nick")
                .password(password)
                .build();
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
                .title("JPA 스터디")
                .headCount(4)
                .operationWay("대면")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .hashtags(Arrays.asList("Java", "JPA", "Spring"))
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
}
