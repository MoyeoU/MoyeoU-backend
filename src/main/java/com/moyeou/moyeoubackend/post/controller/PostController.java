package com.moyeou.moyeoubackend.post.controller;

import com.moyeou.moyeoubackend.auth.supports.LoginMember;
import com.moyeou.moyeoubackend.post.controller.request.AttendRequest;
import com.moyeou.moyeoubackend.post.controller.request.CommentRequest;
import com.moyeou.moyeoubackend.post.controller.request.CreateRequest;
import com.moyeou.moyeoubackend.post.controller.request.UpdateRequest;
import com.moyeou.moyeoubackend.post.controller.response.ItemResponse;
import com.moyeou.moyeoubackend.post.controller.response.PostResponse;
import com.moyeou.moyeoubackend.post.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Post", description = "모집 관련 API")
public class PostController {
    private final PostService postService;

    @Operation(summary = "게시물 생성")
    @PostMapping("/posts")
    public ResponseEntity<Void> create(@Valid @RequestBody CreateRequest request, @LoginMember Long memberId) {
        Long postId = postService.create(request, memberId);
        return ResponseEntity.created(URI.create("/posts/" + postId)).build();
    }

    @Operation(summary = "게시물 상세 조회")
    @GetMapping("/posts/{postId}")
    public PostResponse find(@PathVariable Long postId, @LoginMember Long memberId) {
        return postService.find(postId, memberId);
    }

    @Operation(summary = "게시물 수정")
    @PutMapping("/posts/{postId}")
    public void update(@PathVariable Long postId, @LoginMember Long memberId, @Valid @RequestBody UpdateRequest request) {
        postService.update(postId, memberId, request);
    }

    @Operation(summary = "게시물 삭제")
    @DeleteMapping("/posts/{postId}")
    public void delete(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.delete(postId, memberId);
    }

    @Operation(summary = "신청폼 조회")
    @GetMapping("/posts/{postId}/form")
    public List<ItemResponse> findForm(@PathVariable Long postId) {
        return postService.findForm(postId);
    }

    @Operation(summary = "참여 신청")
    @PostMapping("/posts/{postId}/attend")
    public ResponseEntity<Void> attend(@PathVariable Long postId, @LoginMember Long memberId, @RequestBody AttendRequest request) {
        Long participationId = postService.attend(postId, memberId, request);
        return ResponseEntity.created(URI.create(String.format("/posts/%d/participations/%d", postId, participationId))).build();
    }

    @Operation(summary = "신청 취소")
    @PostMapping("/posts/{postId}/cancel")
    public void cancel(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.cancel(postId, memberId);
    }

    @Operation(summary = "모집 완료")
    @PostMapping("/posts/{postId}/complete")
    public void complete(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.complete(postId, memberId);
    }

    @Operation(summary = "스터디 종료")
    @PostMapping("/posts/{postId}/end")
    public void end(@PathVariable Long postId, @LoginMember Long memberId) {
        postService.end(postId, memberId);
    }

    @Operation(summary = "댓글 작성")
    @PostMapping("/posts/{postId}/comments")
    public void createComment(@PathVariable Long postId, @LoginMember Long memberId, @RequestBody CommentRequest request) {
        postService.createComment(postId, memberId, request);
    }

    @Operation(summary = "댓글 삭제")
    @DeleteMapping("/posts/{postId}/comments/{commentId}")
    public void deleteComment(@LoginMember Long memberId, @PathVariable Long postId, @PathVariable Long commentId) {
        postService.deleteComment(memberId, postId, commentId);
    }
}
