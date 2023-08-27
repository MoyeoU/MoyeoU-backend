package com.moyeou.moyeoubackend.auth.acceptance;

import com.jayway.jsonpath.JsonPath;
import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthAcceptanceTest extends AcceptanceTest {
    @DisplayName("로그인한다")
    @Test
    void login() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        login("example@o.cnu.ac.kr", "pw")
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.nickname").exists());
    }

    @DisplayName("가입하지 않은 사람이 로그인한다")
    @Test
    void loginByNonMember() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        login("ex@o.cnu.ac.kr", "pwpw")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4002"));
    }

    @DisplayName("틀린 비밀번호로 로그인한다")
    @Test
    void loginWithWrongPassword() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        login("example@o.cnu.ac.kr", "pwpw")
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4003"));
    }

    @DisplayName("토큰 재발급 요청")
    @Test
    void refresh() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var response = login("example@o.cnu.ac.kr", "pw")
                .andReturn().getResponse().getContentAsString();
        String refreshToken = JsonPath.read(response, "$.refreshToken");

        mockMvc.perform(post("/refresh")
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    @DisplayName("로그아웃 한다")
    @Test
    void logout() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var response = login("example@o.cnu.ac.kr", "pw")
                .andReturn().getResponse().getContentAsString();
        String refreshToken = JsonPath.read(response, "$.refreshToken");
        String accessToken = JsonPath.read(response, "$.accessToken");

        mockMvc.perform(post("/logout")
                        .header("Authorization", "Bearer " + accessToken)
                        .param("refreshToken", refreshToken))
                .andExpect(status().isOk());
    }

    private void signUp(String email, String password) throws Exception {
        var request = SignUpRequest.builder()
                .email(email).department("컴퓨터융합학부")
                .nickname("nick").password(password).build();
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private ResultActions login(String email, String password) throws Exception {
        return mockMvc.perform(post("/login")
                .content(objectMapper.writeValueAsString(new LoginRequest(email, password)))
                .contentType(MediaType.APPLICATION_JSON));
    }
}
