package com.moyeou.moyeoubackend.member.acceptance;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.moyeou.moyeoubackend.AcceptanceTest;
import com.moyeou.moyeoubackend.auth.controller.request.LoginRequest;
import com.moyeou.moyeoubackend.member.controller.request.MemberUpdateRequest;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class MemberAcceptanceTest extends AcceptanceTest {
    @DisplayName("회원가입한다")
    @Test
    void save() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw")
                .andExpect(header().string("Location", startsWith("/members")));
    }

    @DisplayName("유효하지 않은 파라미터로 회원가입한다")
    @Test
    void signUpWithInvalidParameter() throws Exception {
        var request = SignUpRequest.builder()
                .email("").department("")
                .nickname("nick").password("pw").build();
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4000"))
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.department").exists());
    }

    @DisplayName("이미 가입한 회원이 가입한다")
    @Test
    void duplicateSignUp() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var request = SignUpRequest.builder()
                .email("example@o.cnu.ac.kr").department("컴퓨터융합학부")
                .nickname("nick").password("pw").build();
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("4001"));
    }

    @DisplayName("내 정보를 조회한다")
    @Test
    void findMe() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var accessToken = login("example@o.cnu.ac.kr", "pw");

        mockMvc.perform(get("/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("nick"))
                .andExpect(jsonPath("$.email").value("example@o.cnu.ac.kr"));
    }

    @DisplayName("내 정보를 수정한다")
    @Test
    void updateMe() throws Exception {
        signUp("example@o.cnu.ac.kr", "pw");
        var accessToken = login("example@o.cnu.ac.kr", "pw");

        // 내 정보 수정 전
        mockMvc.perform(get("/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("nick"))
                .andExpect(jsonPath("$.introduction").doesNotExist())
                .andExpect(jsonPath("$.hashtags", hasSize(0)));

        // 이미지 저장 요청
        MockMultipartFile image = new MockMultipartFile(
                "image", "test.png", "image/png", "File content".getBytes());

        String path = mockMvc.perform(multipart(HttpMethod.POST, "/images")
                        .file(image)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        // 내 정보 수정 요청
        var request = new MemberUpdateRequest("안녕하세요!", "nickname", path, List.of("백엔드", "코딩테스트"));
        mockMvc.perform(put("/members/me")
                        .header("Authorization", "Bearer " + accessToken)
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        // 내 정보 수정 후 검증
        mockMvc.perform(get("/members/me")
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("nickname"))
                .andExpect(jsonPath("$.introduction").value("안녕하세요!"))
                .andExpect(jsonPath("$.hashtags", contains("백엔드", "코딩테스트")))
                .andExpect(jsonPath("$.imagePath").value(path));
    }

    @DisplayName("다른 회원의 정보를 조회한다")
    @Test
    void findMember() throws Exception {
        var uri = signUp("example@o.cnu.ac.kr", "pw")
                .andReturn().getResponse().getHeader("Location");

        signUp("ex@o.cnu.ac.kr", "ex");
        var accessToken = login("ex@o.cnu.ac.kr", "ex");

        mockMvc.perform(get(uri)
                        .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("example@o.cnu.ac.kr"));
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

    private ResultActions signUp(String email, String password) throws Exception {
        var request = SignUpRequest.builder()
                .email(email).department("컴퓨터융합학부")
                .nickname("nick").password(password).build();
        return mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(request))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
    }

    private String login(String email, String password) throws Exception {
        var response =  mockMvc.perform(post("/login")
                        .content(objectMapper.writeValueAsString(new LoginRequest(email, password)))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(UTF_8);

        return JsonPath.read(response, "$.accessToken");
    }
}
