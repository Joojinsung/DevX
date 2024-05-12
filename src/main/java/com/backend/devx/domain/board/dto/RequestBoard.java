package com.backend.devx.domain.board.dto;

import java.util.List;

public record RequestBoard(
        Long id,
        String title,
        String content,
        Long like,
        List<String> imageUrls
) {

}