package com.backend.devx.domain.board.repository;

import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByBoard(BoardEntity board);

}
