package com.backend.devx.domain.board.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentUpdateResponseDto(
        @NotBlank
        String content
) {
}
