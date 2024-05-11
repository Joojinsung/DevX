package com.backend.devx.domain.board.repository.boardRepository;

import com.backend.devx.domain.board.dto.RequestBoard;
import com.backend.devx.domain.board.entity.QBoardEntity;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class BoardRepositoryImpl implements BoardRepositoryCustom {
    private final JPAQueryFactory queryFactory;

    QBoardEntity board = QBoardEntity.boardEntity;

    @Override
    public List<RequestBoard> getAll() {
        return queryFactory
                .selectFrom(board)
                .fetch()
                .stream()
                .map(bord -> new RequestBoard(
                        bord.getId(),
                        bord.getTitle(),
                        bord.getContent(),
                        bord.getLikeCount(),
                        bord.getComments().stream().map(comment -> comment.getContent()).toList(),
                        bord.getImages().stream().map(image -> image.getImageUrl()).toList()
                ))
                .collect(Collectors.toList());
    }
}
