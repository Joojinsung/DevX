package com.backend.devx.domain.board.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "board_entity")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BoardEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_id")
    private Long id;

    @Column(name = "board_title")
    private String title;

    @Lob
    @Column(name = "board_content")
    private String content;

    @Column(name = "like_count")
    private Long likeCount = 0L;

    @Column(name = "view_count")
    private Long viewCount = 0L;

    @Column(name = "user_id_idx")
    private Long userId;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL)
    private List<ImageS3UploadEntity> images;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments;

    @OneToMany(mappedBy = "board")
    private List<Likes> likes;

    @Builder
    public BoardEntity(String title, String content, Long userId, List<ImageS3UploadEntity> images) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        this.images = images;
    }

    public void LikeEvent(Long likeCount) {
        this.likeCount = likeCount;
    }

    public void updateBoard(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public void CountUpdate() {
        this.viewCount++;
    }

}
