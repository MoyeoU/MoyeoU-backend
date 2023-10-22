package com.moyeou.moyeoubackend.member.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.controller.request.MemberUpdateRequest;
import com.moyeou.moyeoubackend.member.controller.response.MemberResponse;
import com.moyeou.moyeoubackend.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
@Tag(name = "Member", description = "회원 관련 API")
public class MemberController {
    private final MemberService memberService;

    @Operation(summary = "회원 가입")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        Long memberId = memberService.save(request);
        return ResponseEntity.created(URI.create("/members/" + memberId)).build();
    }

    @Operation(summary = "내 정보 조회")
    @GetMapping("/members/me")
    public MemberResponse findMe(@LoginMember Long memberId) {
        return memberService.find(memberId);
    }

    @Operation(summary = "회원 정보 조회")
    @GetMapping("/members/{memberId}")
    public MemberResponse findMember(@PathVariable Long memberId) {
        return memberService.find(memberId);
    }

    @Operation(summary = "정보 수정")
    @PutMapping("/members/me")
    public void update(@LoginMember Long memberId, @Valid @RequestBody MemberUpdateRequest request) {
        memberService.update(memberId, request);
    }

    @Operation(summary = "회원 탈퇴")
    @DeleteMapping("/members/me")
    public void delete(@LoginMember Long memberId) {
        memberService.delete(memberId);
    }
}
