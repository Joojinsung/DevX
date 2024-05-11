package com.backend.devx.domain.board.entity;

import com.backend.devx.domain.member.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Likes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "like_id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "board_like_id")
    private BoardEntity board;

    @ManyToOne
    @JoinColumn(name = "user_like_id")
    private User user;

    @Builder
    public Likes(BoardEntity board, User user) {
        this.board = board;
        this.user = user;
    }
}
