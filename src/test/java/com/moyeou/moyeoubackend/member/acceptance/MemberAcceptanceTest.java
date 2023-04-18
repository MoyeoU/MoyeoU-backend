package com.moyeou.moyeoubackend.member.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.moyeou.moyeoubackend.common.exception.ErrorResponse;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
class MemberAcceptanceTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("회원가입한다")
    @Test
    void save() throws Exception {
        var request = new SignUpRequest("leejh0799@o.cnu.ac.kr", "컴퓨터융합학부", 202002535, "주희", "pw");
        String response = mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getHeader("Location");

        assertThat(response).startsWith("/members/");
    }

    @DisplayName("유효하지 않은 파라미터로 회원가입한다")
    @Test
    void signUpWithInvalidParameter() throws Exception {
        var request = new SignUpRequest("", "", 202002535, "주희", "pw");
        String response = postApiCall("/sign-up", request)
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
        signUp("leejh0799@o.cnu.ac.kr");
        var request = new SignUpRequest("leejh0799@o.cnu.ac.kr", "컴퓨터융합학부", 202002535, "주희", "pw");
        String response = postApiCall("/sign-up", request)
                .andExpect(status().isBadRequest())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        ErrorResponse errorResponse = objectMapper.readValue(response, ErrorResponse.class);
        assertThat(errorResponse.getCode()).isEqualTo("4001");
    }

    private void signUp(String email) throws Exception {
        var request = new SignUpRequest(email, "컴퓨터융합학부", 202002535, "주희", "pw");
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
