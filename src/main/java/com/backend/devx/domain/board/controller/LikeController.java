package com.backend.devx.domain.board.controller;


import com.backend.devx.domain.board.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    //좋아요 누름, 취소
    @PostMapping("/like/{id}")
    public String likeEvent(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {

        return likeService.likeClickEvent(userDetails.getUsername(), id);
    }
}
