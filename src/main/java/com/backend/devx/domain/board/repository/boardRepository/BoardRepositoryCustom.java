package com.backend.devx.domain.board.repository.boardRepository;

import com.backend.devx.domain.board.dto.RequestBoard;
import java.util.List;

public interface BoardRepositoryCustom {
    List<RequestBoard> getAll();
}
