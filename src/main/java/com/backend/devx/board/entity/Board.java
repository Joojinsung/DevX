package com.backend.devx.board.entity;

import jakarta.persistence.*;

@Entity
public class Board {
    @Id
    @Column(name = "board_no")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long boardNo;


    private String title;

    private String content;

    private String comments;






}
