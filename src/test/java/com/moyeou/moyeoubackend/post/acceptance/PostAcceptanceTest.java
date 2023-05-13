package com.moyeou.moyeoubackend.post.acceptance;

import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.common.exception.ErrorResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PostAcceptanceTest extends AcceptanceTest {
    @Autowired
    private HashtagRepository hashtagRepository;

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
        var member1 = signUpLogin("example@o.cnu.ac.kr", "pw");
        var member2 = signUpLogin("ex@o.cnu.ac.kr", "password");

        // member1 : 게시물 생성
        var post = createPost(member1);

        // member2 : 게시물 조회
        var response = findPost(member2, post);

        PostResponse postResponse = objectMapper.readValue(response, PostResponse.class);
        assertAll(
                () -> assertThat(post).startsWith("/posts/"),
                () -> assertThat(postResponse.getContent()).isEqualTo("<h1>같이 공부해요!</h1>"),
                () -> assertThat(postResponse.getIsHost()).isFalse(),
                () -> assertThat(postResponse.getHashtags()).containsExactly("Java", "JPA", "Spring")
        );
    }

    @DisplayName("스터디에 참여한다.")
    @Test
    void attend() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw");
        var member = signUpLogin("ex@o.cnu.ac.kr", "password");

        // host가 게시물 생성
        var post = createPost(host);

        // member가 신청
        var location = mockMvc.perform(post(post + "/attend")
                        .header("Authorization", "Bearer " + member))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("location");

        // 조회
        var response = findPost(member, post);

        PostResponse postResponse = objectMapper.readValue(response, PostResponse.class);
        assertThat(location).startsWith(post + "/participations/");
        assertThat(postResponse.getCurrentCount()).isEqualTo(2);
    }

    @DisplayName("작성자가 스터디에 참여한다.")
    @Test
    void attendByHost() throws Exception {
        var host = signUpLogin("example@o.cnu.ac.kr", "pw");

        // host가 게시물 생성
        var post = createPost(host);

        // host가 신청
        var response = mockMvc.perform(post(post + "/attend")
                        .header("Authorization", "Bearer " + host))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo("4007");
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
                .getContentAsString(UTF_8);

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        return loginResponse.getAccessToken();
    }

    private String createPost(String token) throws Exception {
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
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("location");
    }

    private String findPost(String token, String location) throws Exception {
        return mockMvc.perform(get(location)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
    }
}
