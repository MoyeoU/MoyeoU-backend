package com.moyeou.moyeoubackend.auth.acceptance;

import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.auth.controller.response.LoginResponse;
import com.moyeou.moyeoubackend.common.exception.ErrorResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthAcceptanceTest extends AcceptanceTest {
    @DisplayName("로그인한다")
    @Test
    void login() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        postApiCall("/login", new LoginRequest("example@o.cnu.ac.kr", "pw"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists())
                .andExpect(jsonPath("$.nickname").exists());
    }

    @DisplayName("가입하지 않은 사람이 로그인한다")
    @Test
    void loginByNonMember() throws Exception {
        String response = postApiCall("/login", new LoginRequest("example@o.cnu.ac.kr", "pw"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo("4002");
    }

    @DisplayName("틀린 비밀번호로 로그인한다")
    @Test
    void loginWithWrongPassword() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        String response = postApiCall("/login", new LoginRequest("example@o.cnu.ac.kr", "pwpw"))
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo("4003");
    }

    @DisplayName("토큰 재발급 요청")
    @Test
    void refresh() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var request = new LoginRequest("example@o.cnu.ac.kr", "pw");
        String response = postApiCall("/login", request)
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);
        LoginResponse loginResponse = objectMapper.readValue(response, LoginResponse.class);
        String refreshToken = loginResponse.getRefreshToken();

        mockMvc.perform(post("/refresh")
                        .content("{\"refreshToken\" :\"" + refreshToken +"\"}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andExpect(jsonPath("$.refreshToken").exists());
    }

    private void signUp(String email, String password) throws Exception {
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
    }

    private ResultActions postApiCall(String url, Object request) throws Exception {
        return mockMvc.perform(post(url)
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON));
    }
}
