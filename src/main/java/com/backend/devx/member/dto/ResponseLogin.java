package com.backend.devx.member.dto;

import lombok.Builder;

@Builder
public record ResponseLogin(
        String accessToken
) {
}
