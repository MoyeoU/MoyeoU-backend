package com.moyeou.moyeoubackend.post.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequiredArgsConstructor
public class PostController {
    private final PostService postService;

    @PostMapping("/posts")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateRequest request, @LoginMember Long memberId) {
        Long postId = postService.create(request, memberId);
        return ResponseEntity.created(URI.create("/posts/" + postId)).build();
    }

    @GetMapping("/posts/{postId}")
    public PostResponse find(@PathVariable Long postId, @LoginMember Long memberId) {
        return postService.find(postId, memberId);
    }

    @PutMapping("/posts/{postId}")
    public void update(@PathVariable Long postId, @LoginMember Long memberId, @Valid @RequestBody UpdateRequest request) {
        postService.update(postId, memberId, request);
    }

    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.delete(postId, memberId);
    }

    @PostMapping("/posts/{postId}/attend")
    public ResponseEntity<Void> attend(@PathVariable Long postId, @LoginMember Long memberId) {
        Long participationId = postService.attend(postId, memberId);
        return ResponseEntity.created(URI.create(String.format("/posts/%d/participations/%d", postId, participationId))).build();
    }

    @PostMapping("/posts/{postId}/cancel")
    public void cancel(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.cancel(postId, memberId);
    }

    @PostMapping("/posts/{postId}/complete")
    public void complete(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.complete(postId, memberId);
    }

    @PostMapping("/posts/{postId}/end")
    public void end(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.end(postId, memberId);
    }
}
