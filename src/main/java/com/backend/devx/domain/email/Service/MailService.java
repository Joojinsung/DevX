package com.backend.devx.domain.email.Service;

import com.backend.devx.global.config.redis.RedisUtil;
import jakarta.mail.Message;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender javaMailSender;
    private final RedisUtil redisUtil; //redis 관련


    private MimeMessage createMessage(String code, String email) throws Exception{
        MimeMessage message = javaMailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, email);
        message.setSubject("Planet 인증 번호입니다.");
        message.setText("이메일 인증코드: "+code);

        message.setFrom("dev.jinsung1017@gmail.com"); //보내는사람.

        return  message;
    }

    public void sendMail(String code, String email) throws Exception{
        try{
            MimeMessage mimeMessage = createMessage(code, email);
            javaMailSender.send(mimeMessage);
        }catch (MailException mailException){
            mailException.printStackTrace();
            throw   new IllegalAccessException();
        }
    }

    public String sendCertificationMail(String email)   {
        try{
            String code = UUID.randomUUID().toString().substring(0, 6); //랜덤 인증번호 uuid를 이용!
            sendMail(code,email);

            redisUtil.setDataExpire(code,email,60*5L); // {key,value} 5분동안 저장.

            return  code;
        }catch (Exception exception){
            exception.printStackTrace();

        }
        return email;
    }

    // todo : 인증번호 확인 후 삭제로직 + 아이디 보여주기 + 비밀번호 변경로직



}