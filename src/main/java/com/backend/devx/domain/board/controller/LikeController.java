package com.backend.devx.domain.board.controller;


import com.backend.devx.domain.board.service.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class LikeController {
    private final LikeService likeService;

    //좋아요 누름, 취소
    @PostMapping("/like/{id}")
    @ResponseStatus(HttpStatus.OK)
    public String likeEvent(@AuthenticationPrincipal UserDetails userDetails, @PathVariable Long id) {

        return likeService.likeClickEvent(userDetails.getUsername(), id);
    }
}
