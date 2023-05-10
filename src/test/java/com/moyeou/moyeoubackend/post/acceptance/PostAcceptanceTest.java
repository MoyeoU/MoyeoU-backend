package com.moyeou.moyeoubackend.post.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class PostAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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
        String member1 = signUpLogin("example@o.cnu.ac.kr", "pw");
        String member2 = signUpLogin("ex@o.cnu.ac.kr", "password");

        // member1 : 게시물 생성
        var post = CreateRequest.builder()
                .title("JPA 스터디")
                .headCount(4)
                .operationWay("대면")
                .expectedDate("06-01")
                .estimatedDuration("3개월")
                .content("<h1>같이 공부해요!</h1>")
                .hashtags(Arrays.asList("Java", "JPA", "Spring"))
                .build();
        String location = mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + member1)
                        .content(objectMapper.writeValueAsString(post))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("location");

        // member2 : 게시물 조회
        String response = mockMvc.perform(get(location)
                        .header("Authorization", "Bearer " + member2))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        PostResponse postResponse = objectMapper.readValue(response, PostResponse.class);

        assertAll(
                () -> assertThat(location).startsWith("/posts/"),
                () -> assertThat(postResponse.getContent()).isEqualTo("<h1>같이 공부해요!</h1>"),
                () -> assertThat(postResponse.getIsHost()).isFalse(),
                () -> assertThat(postResponse.getHashtags()).containsExactly("Java", "JPA", "Spring")
        );
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
}
