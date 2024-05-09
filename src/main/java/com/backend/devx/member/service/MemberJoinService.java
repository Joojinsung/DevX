package com.backend.devx.member.service;

import com.backend.devx.member.dto.RequestJoin;
import com.backend.devx.member.entity.Member;
import com.backend.devx.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberJoinService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    public void save(RequestJoin request) {
        String password = passwordEncoder.encode(request.password());
        Member member = Member.builder()
                .email(request.email())
                .password(password)
                .nickname(request.nickname())
                .phoneNumber(request.phoneNumber())
                .gender(request.gender())
                .build();
        memberRepository.save(member);
    }
}
