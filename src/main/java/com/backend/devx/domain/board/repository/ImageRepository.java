package com.backend.devx.domain.board.repository;

import com.backend.devx.domain.board.entity.BoardEntity;
import com.backend.devx.domain.board.entity.ImageS3UploadEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImageRepository extends JpaRepository<ImageS3UploadEntity, Long> {
    List<ImageS3UploadEntity> findByBoard(BoardEntity board);

}
