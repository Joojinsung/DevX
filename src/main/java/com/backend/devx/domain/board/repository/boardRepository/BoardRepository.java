package com.backend.devx.domain.board.repository.boardRepository;


import com.backend.devx.domain.board.entity.BoardEntity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<BoardEntity, Long>, BoardRepositoryCustom {

}
