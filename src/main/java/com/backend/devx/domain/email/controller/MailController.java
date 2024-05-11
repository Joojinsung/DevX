package com.backend.devx.domain.email.controller;

import com.backend.devx.domain.email.Service.MailService;
import com.backend.devx.domain.email.dto.MailRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ResponseEntity<String> sendCertificationMail(@RequestBody MailRequestDto mailRequestDto) {
        String email = mailRequestDto.getEmail();
        String code = mailService.sendCertificationMail(email);
        return ResponseEntity.ok("인증 코드가 " + email + "로 전송되었습니다. 인증코드: " + code);
    }
}