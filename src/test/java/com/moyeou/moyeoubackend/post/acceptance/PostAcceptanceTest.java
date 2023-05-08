package com.moyeou.moyeoubackend.post.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.domain.Member;
import com.moyeou.moyeoubackend.member.repository.MemberRepository;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.domain.Hashtag;
import com.moyeou.moyeoubackend.post.domain.Post;
import com.moyeou.moyeoubackend.post.repository.HashtagRepository;
import com.moyeou.moyeoubackend.post.repository.PostRepository;
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
    private MemberRepository memberRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private HashtagRepository hashtagRepository;

    @BeforeEach
    void setUp() {
        hashtagRepository.save(new Hashtag("Java"));
        hashtagRepository.save(new Hashtag("JPA"));
        hashtagRepository.save(new Hashtag("Spring"));
    }


    @DisplayName("게시물을 생성한다")
    @Test
    void create() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        String accessToken = login("example@o.cnu.ac.kr", "pw");
        var post = new CreateRequest("JPA 스터디", 4, "대면", "06-01", "3개월", Arrays.asList("Java", "JPA", "Spring"));

        String location = mockMvc.perform(post("/posts")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(post))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("location");

        Post savedPost = postRepository.findById(Long.parseLong(location.substring(7))).get();
        Member member = memberRepository.findByEmail("example@o.cnu.ac.kr").get();

        assertAll(
                () -> assertThat(location).startsWith("/posts/"),
                () -> assertThat(savedPost.getPostHashtags().size()).isEqualTo(3),
                () -> assertThat(savedPost.getHost()).isEqualTo(member)
        );
    }

    private String signUp(String email, String password) throws Exception {
        var request = new SignUpRequest(email, "컴퓨터융합학부", 202000000, "nick", password);
        return mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String login(String email, String password) throws Exception {
        var request = new LoginRequest(email, password);
        var response =  mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        return loginResponse.getAccessToken();
    }
}
