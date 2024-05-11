package com.backend.devx.domain.board.repository;

import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.Likes;
import com.backend.devx.domain.member.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LikeRepository extends JpaRepository<Likes, Long> {
    Likes findByUserAndBoard(User user, BoardEntity board);

    List<Likes> findByBoard(BoardEntity board);
}
