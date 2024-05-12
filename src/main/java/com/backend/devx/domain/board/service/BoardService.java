package com.backend.devx.domain.board.service;

import com.backend.devx.domain.board.dto.CreateBoard;
import com.backend.devx.domain.board.dto.DetailBoardResponse;
import com.backend.devx.domain.board.dto.RequestBoard;
import com.backend.devx.domain.board.dto.UpdateBoard;
import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.Comment;
import com.backend.devx.domain.board.entity.ImageS3UploadEntity;
import com.backend.devx.domain.board.entity.Likes;
import com.backend.devx.domain.board.repository.CommentRepository;
import com.backend.devx.domain.board.repository.ImageRepository;
import com.backend.devx.domain.board.repository.LikeRepository;
import com.backend.devx.domain.board.repository.boardRepository.BoardRepository;
import com.backend.devx.domain.member.entity.User;
import com.backend.devx.domain.member.repository.UserRepository;
import com.backend.devx.global.exception.BusinessException;
import com.backend.devx.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;


@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final com.backend.devx.domain.board.service.S3Service s3Service;
    private final ImageRepository imageRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;


    // 게시글 + 이미지 파일 업로드
    public void create(CreateBoard board, String userName, List<MultipartFile> multipartFiles) {
        User user = userRepository.findByEmail(userName).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        // s3 업로드
        List<String> imageUrls = s3Service.upload(multipartFiles);

        BoardEntity boards = BoardEntity.builder()
                .title(board.title())
                .content(board.content())
                .userId(user.getId())
                .build();

        // 이미지 저장
        List<ImageS3UploadEntity> imageList = imageUrls.stream()
                .map(url -> ImageS3UploadEntity.builder()
                        .imageUrl(url)
                        .board(boards)
                        .build())
                .toList();

        // BoardEntity 에서 cascade = CascadeType.ALL 설정으로 imageRepository.saveALl 설정 안해도 되도록
        boards.setImages(imageList);
        boardRepository.save(boards);

    }

    // 게시글 업데이트
    public void updateItem(Long boardId, String username, UpdateBoard request, List<MultipartFile> multipartFiles) {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        BoardEntity board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));

        if (!board.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "수정 권한이 없습니다");
        }

        // 기존 이미지 목록
        List<ImageS3UploadEntity> existingImages = imageRepository.findByBoard(board);

        // 새로 업로드할 이미지
        List<String> newImageUrls = s3Service.upload(multipartFiles);

        List<ImageS3UploadEntity> newImages = newImageUrls.stream()
                .map(url -> ImageS3UploadEntity.builder().imageUrl(url).board(board).build())
                .toList();

        // 삭제할 이미지 처리
        List<ImageS3UploadEntity> imagesToDelete = existingImages.stream()
                .filter(image -> !newImages.stream().map(ImageS3UploadEntity::getImageUrl).toList().contains(image.getImageUrl()))
                .toList();

        for (ImageS3UploadEntity image : imagesToDelete) {
            s3Service.deleteFile(image.getImageUrl());
            imageRepository.delete(image);
        }

        // 게시글 업데이트
        board.updateBoard(request.title(), request.content());
        List<ImageS3UploadEntity> updatedImages = new ArrayList<>(existingImages);
        updatedImages.removeAll(imagesToDelete);
        updatedImages.addAll(newImages);
        board.setImages(updatedImages);

        boardRepository.save(board);
    }

    // 상품 삭제
    public void delete(Long boardId, String userName) {
        User user = userRepository.findByEmail(userName).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));
        BoardEntity board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        if (!board.getUserId().equals(user.getId())) {
            throw new BusinessException(ErrorCode.FORBIDDEN_ERROR, "삭제 권한이 없습니다");
        }

        // 댓글 삭제
        List<Comment> comments = commentRepository.findByBoard(board);
        commentRepository.deleteAll(comments);

        // 해당 게시글 좋아요 삭제
        List<Likes> likes = likeRepository.findByBoard(board);
        likeRepository.deleteAll(likes);

        // 이미지 삭제
        List<ImageS3UploadEntity> images = imageRepository.findByBoard(board);

        for (ImageS3UploadEntity image : images) {
            String fileName = image.getImageUrl();
            s3Service.deleteFile(fileName);
            imageRepository.deleteById(image.getId());
        }

        boardRepository.deleteById(boardId);

    }

    // 해당 게시글 조회 + 조회수 +1
    public DetailBoardResponse detailBoard(Long boardId) {
        BoardEntity board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_BOARD));
        User userName = userRepository.findById(board.getUserId()).orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_MEMBER));

        board.CountUpdate();
        boardRepository.save(board);

        List<DetailBoardResponse.detailComment> comments = board.getComments().stream()
                .map(c -> new DetailBoardResponse.detailComment(
                        c.getUser().getNickname(),
                        c.getContent()
                )).toList();


        return new DetailBoardResponse(
                userName.getNickname(),
                board.getContent(),
                board.getTitle(),
                board.getLikeCount(),
                board.getViewCount(),
                comments,
                board.getImages().stream().map(i -> i.getImageUrl()).toList()

        );

    }

    // 전체 게시글 들고오기
    public List<RequestBoard> getAll() {
        return boardRepository.getAll();
    }
}
