package com.backend.devx.domain.board.controller;

import com.backend.devx.domain.board.dto.CommentRequest;
import com.backend.devx.domain.board.dto.CommentUpdateResponseDto;
import com.backend.devx.domain.board.service.CommentService;
import jakarta.validation.Valid;
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

    //댓글 삭제
    @DeleteMapping("/comment/delete/{id}")
    public void deleteComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long id) {
        commentService.deleteComment(user, id);
    }

    //댓글 수정
    @PutMapping("/comment/update/{id}")
    public void updateComment(@AuthenticationPrincipal UserDetails user, @PathVariable Long id, @Valid @RequestBody CommentUpdateResponseDto request) {
        commentService.updateComment(user, id, request);
    }


}
