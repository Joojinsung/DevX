package com.backend.devx.global.controller;

import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class MyHandler extends TextWebSocketHandler {


    //최초 연결 시
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

    }

    //양방향 데이터 통신할 떄 해당 메서드가 call 된다.
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        //do something
    }

    //웹소켓 종료
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

    }

    //통신 에러 발생 시
    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {


    }
}