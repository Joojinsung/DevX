package com.backend.devx.domain.member.service;

import com.backend.devx.domain.member.Role;
import com.backend.devx.domain.member.dto.UserSignUpDto;
import com.backend.devx.domain.member.entity.User;
import com.backend.devx.domain.member.repository.UserRepository;
import com.backend.devx.global.exception.BusinessException;
import com.backend.devx.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.integration.IntegrationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(UserSignUpDto userSignUpDto) {

        if (userRepository.findByEmail(userSignUpDto.getEmail()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_EMAIL);
        }

        if (userRepository.findByNickname(userSignUpDto.getNickname()).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATED_NICKNAME);
        }

        User user = User.builder()
                .email(userSignUpDto.getEmail())
                .password(userSignUpDto.getPassword())
                .nickname(userSignUpDto.getNickname())
                .age(userSignUpDto.getAge())
                .city(userSignUpDto.getCity())
                .role(Role.USER)
                .build();

        user.passwordEncode(passwordEncoder);
        userRepository.save(user);
    }
}
