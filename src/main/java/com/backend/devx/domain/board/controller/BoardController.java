package com.backend.devx.domain.board.controller;

import com.backend.devx.domain.board.dto.CreateBoard;
import com.backend.devx.domain.board.dto.RequestBoard;
import com.backend.devx.domain.board.dto.UpdateBoard;
import com.backend.devx.domain.board.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    // 게시글 등록
    @PostMapping(value = "/createBoard", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String addBoard(@AuthenticationPrincipal UserDetails user,
                           @RequestPart(value = "dto") CreateBoard board,
                           @RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles) {

        boardService.create(board, user.getUsername(), multipartFiles);
        return "생성 완료";
    }

    // 게시글 업데이트
    @PutMapping("/update/{boardId}")
    public void updateItem(@PathVariable Long boardId,
                           @RequestPart(value = "dto") UpdateBoard updateBoard,
                           @RequestPart(value = "file", required = false) List<MultipartFile> multipartFiles,
                           @AuthenticationPrincipal UserDetails user
    ) {
        boardService.updateItem(boardId, user.getUsername(), updateBoard, multipartFiles);
    }

    // 게시글 삭제
    @DeleteMapping("/delete/{boardId}")
    public void delete(@PathVariable Long boardId, @AuthenticationPrincipal UserDetails user) {
        boardService.delete(boardId, user.getUsername());
    }

    // 게시글 전체 조회
    // todo : 페이지네이션 추가
    @GetMapping("/getAllBoald")
    public List<RequestBoard> getData() {
        return boardService.getAll();
    }
}

