package com.backend.devx.domain.board.controller;

import com.backend.devx.domain.board.dto.CommentRequest;
import com.backend.devx.domain.board.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentController {


    private final CommentService commentService;

    // 댓글 달기
    @PostMapping("/comment/{id}")
    public void addComment(@AuthenticationPrincipal UserDetails userName, @PathVariable Long id, @RequestBody CommentRequest request) {
        commentService.addComment(userName.getUsername(), id, request);
    }

    // todo: 댓글 삭제하기


    // todo :댓글 수정하기



}
