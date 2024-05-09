package com.backend.devx.member.dto;


import com.backend.devx.member.role.Gender;

public record RequestJoin(
        String email,
        String password,
        String nickname,
        String address1,
        String address2,
        String phoneNumber,
        Gender gender


) {
}
