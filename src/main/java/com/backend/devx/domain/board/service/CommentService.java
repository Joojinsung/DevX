package com.backend.devx.domain.board.service;


import com.backend.devx.domain.board.dto.CommentRequest;
import com.backend.devx.domain.board.dto.CommentUpdateResponseDto;
import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.Comment;
import com.backend.devx.domain.board.repository.CommentRepository;
import com.backend.devx.domain.board.repository.boardRepository.BoardRepository;
import com.backend.devx.domain.member.entity.User;
import com.backend.devx.domain.member.repository.UserRepository;
import com.backend.devx.global.exception.BusinessException;
import com.backend.devx.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final BoardRepository boardRepository;

    // 댓글 추가
    public void addComment(String userMember, Long boardId, CommentRequest commentRequest) {
        User user = userRepository.findByEmail(userMember).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        BoardEntity board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));


        Comment comment = Comment.builder()
                .content(commentRequest.content())
                .user(user)
                .board(board)
                .build();

        commentRepository.save(comment);

    }

    //댓글 수정
    public void updateComment(UserDetails user, Long id, CommentUpdateResponseDto request) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));

        if (user.equals(comment.getUser())) {
            comment.updateContent(request.content());
        }

    }

    //댓글 삭제
    public void deleteComment(UserDetails user, Long id) {
        Comment comment = commentRepository.findById(id).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));
        if (user.equals(comment.getUser())) {
            commentRepository.delete(comment);
        }
    }


}
