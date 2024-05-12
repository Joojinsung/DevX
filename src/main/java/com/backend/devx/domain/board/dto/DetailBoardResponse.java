package com.backend.devx.domain.board.dto;

import java.util.List;

public record DetailBoardResponse(
        String userName,
        String content,
        String title,
        Long like,
        Long viewCount,
        List<detailComment> comment,
        List<String> imageUlr
) {
    public record detailComment(
        String userName,
        String userComment
    ) {}
}
