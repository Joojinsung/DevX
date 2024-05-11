package com.backend.devx.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ImageS3")
public class ImageS3UploadEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Image_id")
    private Long id;

    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private BoardEntity board;


    @Builder
    public ImageS3UploadEntity(String imageUrl, BoardEntity board) {
        this.imageUrl = imageUrl;
        this.board = board;
    }
}
