package com.moyeou.moyeoubackend.member.acceptance;

import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.common.exception.ErrorResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.controller.response.MemberResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class MemberAcceptanceTest extends AcceptanceTest {
    @DisplayName("회원가입한다")
    @Test
    void save() throws Exception {
        var response = signUp("example@o.cnu.ac.kr", "pw");
        assertThat(response).startsWith("/members/");
    }

    @DisplayName("유효하지 않은 파라미터로 회원가입한다")
    @Test
    void signUpWithInvalidParameter() throws Exception {
        var request = SignUpRequest.builder()
                .email("")
                .department("")
                .studentNumber(202000000)
                .nickname("nick")
                .password("pw")
                .build();
        String response = mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        Map<String, String> errors = errorResponse.getErrors();
        assertAll(
                () -> assertThat(errorResponse.getCode()).isEqualTo("4000"),
                () -> assertThat(errors).containsKeys("email", "department")
        );
    }

    @DisplayName("이미 가입한 회원이 가입한다")
    @Test
    void duplicateSignUp() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var request = SignUpRequest.builder()
                .email("example@o.cnu.ac.kr")
                .department("컴퓨터융합학부")
                .studentNumber(202000000)
                .nickname("nick")
                .password("pw")
                .build();
        String response = mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo("4001");
    }

    @DisplayName("내 정보를 조회한다")
    @Test
    void findMe() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var accessToken = login("example@o.cnu.ac.kr", "pw");

        var myInfo = mockMvc.perform(get("/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        MemberResponse memberResponse = objectMapper.readValue(myInfo, MemberResponse.class);
        assertAll(
                () -> assertThat(memberResponse.getNickname()).isEqualTo("nick"),
                () -> assertThat(memberResponse.getEmail()).isEqualTo("example@o.cnu.ac.kr")
        );
    }

    @DisplayName("다른 회원의 정보를 조회한다")
    @Test
    void findMember() throws Exception {
        var uri = signUp("example@o.cnu.ac.kr", "pw");

        signUp("ex@o.cnu.ac.kr", "ex");
        var accessToken = login("ex@o.cnu.ac.kr", "ex");

        var memberInfo = mockMvc.perform(get(uri)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        MemberResponse memberResponse = objectMapper.readValue(memberInfo, MemberResponse.class);
        assertThat(memberResponse.getEmail()).isEqualTo("example@o.cnu.ac.kr");
    }

    @DisplayName("회원을 탈퇴한다")
    @Test
    void delete() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var accessToken = login("example@o.cnu.ac.kr", "pw");

        mockMvc.perform(MockMvcRequestBuilders.delete("/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk());
    }

    private String signUp(String email, String password) throws Exception {
        var request = SignUpRequest.builder()
                .email(email)
                .department("컴퓨터융합학부")
                .studentNumber(202000000)
                .nickname("nick")
                .password(password)
                .build();
        return mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");
    }

    private String login(String email, String password) throws Exception {
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
