package com.moyeou.moyeoubackend.member.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.member.controller.request.SignUpRequest;
import com.moyeou.moyeoubackend.member.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class MemberController {
    private final MemberService memberService;

    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody SignUpRequest request) {
        Long memberId = memberService.save(request);
        return ResponseEntity.created(URI.create("/members/" + memberId)).build();
    }

    @PutMapping("/members/me")
    public void update(@LoginMember Long memberId, @Valid UpdateRequest request) {
        memberService.update(memberId, request);
    }
}
