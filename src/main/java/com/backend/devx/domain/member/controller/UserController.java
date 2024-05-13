package com.backend.devx.domain.member.controller;

import com.backend.devx.domain.member.dto.UserSignUpDto;
import com.backend.devx.domain.member.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/sign-up")
    @ResponseStatus(HttpStatus.CREATED)
    public String signUp(@RequestBody UserSignUpDto userSignUpDto) throws Exception {
        userService.signup(userSignUpDto);
        return "회원가입 성공";
    }


}
