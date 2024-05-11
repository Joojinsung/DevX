package com.backend.devx.domain.board.service;

import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.Likes;
import com.backend.devx.domain.board.repository.boardRepository.BoardRepository;
import com.backend.devx.domain.board.repository.LikeRepository;
import com.backend.devx.domain.member.entity.User;
import com.backend.devx.domain.member.repository.UserRepository;
import com.backend.devx.global.exception.BusinessException;
import com.backend.devx.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LikeService {
    private final LikeRepository likeRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    public String likeClickEvent(String userName, Long boardId) {
        //현재 로그인 유저
        User user = userRepository.findByEmail(userName).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        // 게시글 가져옴
        BoardEntity board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));

        Likes existingLike = likeRepository.findByUserAndBoard(user, board);

        if (existingLike == null) {
            Likes newLike = Likes.builder()
                    .user(user)
                    .board(board)
                    .build();

            likeRepository.save(newLike);
            board.LikeEvent(board.getLikeCount() + 1); // 좋아요 수 증가
            boardRepository.save(board); // 변경된 좋아요 수를 데이터베이스에 저장
            return "좋아요 누름";
        } else {
            // 좋아요 취소
            likeRepository.delete(existingLike);
            board.LikeEvent(board.getLikeCount() - 1);
            boardRepository.save(board); // 변경된 좋아요 수를 데이터베이스에 저장
            return "좋아요 취소";
        }

    }

}
