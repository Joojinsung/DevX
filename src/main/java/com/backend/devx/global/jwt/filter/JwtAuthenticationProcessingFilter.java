package com.backend.devx.global.jwt.filter;
/*
 * Jwt 인증 필터
 * "/login" 이외의 URI 요청이 왔을 때 처리하는 필터
 *
 * 기본적으로 사용자는 요청 헤더에 AccessToken 만 담아서 요청
 * AccessToken 만료 시에만 RefreshToken 을 요청 헤더에 AccessToken 과 함께 요청
 *
 * 1. RefreshToken 이 없고, AccessToken 이 유효한 경우 -> 인증 성공처리, RefreshToken 을 재발급하지는 않는다.
 * 2. RefreshToken 이 없고, AccessToken 이 없거나 유효하지 않은 경우 -> 인승 실패 처리, 403 ERROR
 * 3. RefreshToken 이 있는 경우 -> DB의 RefreshToken 과 비교하여 일치하면 AccessToken 재발급, RefreshToken 개발급 ( RTR 발식 )
 *                               인증 성공 처리는 하지 않고 실패처리
 * */

import com.backend.devx.global.jwt.util.PasswordUtil;
import com.backend.devx.global.jwt.service.JwtService;
import com.backend.devx.domain.member.entity.User;
import com.backend.devx.domain.member.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {
    private static final String NO_CHECK_URL = "/login"; // "/login" 으로 들어오는 요청은 Filter 작동 X

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // 사용자에게 부여된 권한을 그대로 유지하는 기본 매퍼를 설정합니다.
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().equals(NO_CHECK_URL)) {
            filterChain.doFilter(request, response); // "/login" 요청이 들어오면, 다음 필터 호출
            return; //return 으로 이후 현재 필터 진행 막기 ( 안해주면 아래로 내려가서 계속 필터가 진행됨 )
        }
        // 사용자 요청 헤더에서 RefreshToken 추출
        // -> RefreshToken 이 없거나 유효하지 않다면 ( DB 에 저장된 RefreshToken 과 다르다면 ) Null 을 반환.
        // 사용자의 요청 헤더에 Refresh 있는 경우는, AccessToken 이 만료되어 요청한 경우밖에 없다.
        // 따라서, 위의 경우를 제외하면 추출한 refreshToken 은 모두 Null
        String refreshToken = jwtService.extractRefreshToken(request)
                .filter(jwtService::isTokenValid)
                .orElse(null);

        // RefreshToken 이 요청 헤더에 존재 했다면, 사용자가 AccessToken 이 만료되어서
        // RefreshToken 까지 보낸 것이므로 리프레시 토큰이 DB의 RefreshToken 과 일치하는지 확인 후,
        // 일치한다면 AccessToken 을 재발급해준다.
        if (refreshToken != null) {
            checkRefreshTokenAndReIssueAccessToken(response, refreshToken);
            return; // RefreshToken 을 보낸 경우에는 AccessToken 을 재발급 하고 인증처리는 하지 않게 하기위해 return 으로 필터 진행 막기
        }

        // RefreshToken 이 없거나 유효하지 않다면, AccessToken 을 검사하고 인증을 처리하는 로직 수행
        // AccessToken 이 없거나 유효하지 않다면, 인증 객체가 담기지 않은 상태로 다음 필토러 넘어가기 때문에 403 에러 발생
        // AccessToken 이 유효하다면, 인증 객체가 담긴 상태로 다음 필터로 넘어가기 때문에 인증 성공
        if (refreshToken == null) {
            checkAccessTokenAndAuthentication(request, response, filterChain);
        }

    }

    // [ RefreshToken 으로 유저 정보 찾기 & AccessToken / RefreshToken 재발급 메소드 ]
    // 파라미터로 들어온 헤더에서 추출한 RefreshToken 으로 DB 에서 유저를 찾고, 해당 유저가 있다면
    // JwtService.createAccessToken() 으로 AccessToken 생성,
    // reissueRefreshToken() 으로 RefreshToken 재발급 & DB 에 RefreshToken 업데이트 메소드 호출
    // 그 후 Jwt.service.sendAccessTokenAndRefreshToken() 으로 응답 헤더에 보내기
    public void checkRefreshTokenAndReIssueAccessToken(HttpServletResponse response, String refreshToken) {
        userRepository.findByRefreshToken(refreshToken)
                .ifPresent(user -> {
                    String reIssuedRefreshToken = reIssueRefreshToken(user);
                    jwtService.sendAccessAndRefreshToken(response, jwtService.createAccessToken(user.getEmail()),
                            reIssuedRefreshToken);
                });
    }

    /*
     * [ RefreshToken 재발급 & DB에 RefreshToken update 메소드 ]
     * jwtService.createRefreshToken() 으로 RefreshToken 재발급 후
     * DB 에 재발급한 RefreshToken update 후 Flush
     * */

    private String reIssueRefreshToken(User user) {
        String reIssuedRefreshToken = jwtService.createRefreshToken();
        user.updateRefreshToken(reIssuedRefreshToken);
        userRepository.saveAndFlush(user);
        return reIssuedRefreshToken;
    }

    /*
     * [ AccessToken 체크 & 인증 처리 메소드 ]
     * request 에서 extractAccessToken() 으로 AccessToken 추출 후, isTokenValid() 로 유효한 토큰인지 검증
     * 유효한 토큰이면, 액세스 토큰에서 extractEmail 로 Email 추출한 후 findByEmail() 로 해당 이메일 사용하는 유저 객체 반환
     * 그 유저 객체를 saveAuthentication() 으로 인증 처리하여
     * 인증 허가 처리된 객체를 SecurityContextHolder 에 담기
     * 그 후 다음 인증 필터로 진행
     * */
    public void checkAccessTokenAndAuthentication(HttpServletRequest request, HttpServletResponse response,
                                                  FilterChain filterChain) throws ServletException, IOException {
        log.info("checkAccessTokenAndAuthentication() 호출");
        jwtService.extractAccessToken(request)
                .filter(jwtService::isTokenValid)
                .ifPresent(accessToken -> jwtService.extractEmail(accessToken)
                        .ifPresent(email -> userRepository.findByEmail(email)
                                .ifPresent(this::saveAuthentication)));
        filterChain.doFilter(request, response);
    }

    /*
     * [ 인증 허가 메소드 ]
     * 파라미터의 유저 : 우리가 만든 회원 객체 / 빌더의 유저 : UserDetail 의 User 객체
     *
     * new UsernamePasswordAuthenticationToken() 으로 인증 객체인 Authentication 객체 생성
     * UsernamePasswordAuthenticationToken 의 파라미터
     * 1. 위에서 만든 UserDetailUser 객체 ( 유저 정보 )
     * 2. credential ( 보통 비밀번호로, 인증 시에는 보통 null 로 제거 )
     * 3. Collection < ? extends GrantedAuthority> 로,
     * UserDetail 의 User 객체 안에 Set<GrantedAuthority> authorities 이 있어서 getter 로 호출한 후에,
     * new NullAuthoritiesMapper() 로 GrantedAuthoritiesMapper 객체를 생성하고, mapAuthorities() 에 담기
     *
     * SecurityContextHolder.getContext() 로 SecurityContext 를 꺼낸 후,
     * setAuthentication() 을 이용하여 위에서 만든 Authentication 객체에 대한 인증 허가 처리
     * */

    public void saveAuthentication(User myUser) {
        String password = myUser.getPassword();
        if (password == null) { // 소셜 로그인 유저의 비밀번호 임의로 설정 하여 소셜 로그인 유저도 인증 되도록 설정
            password = PasswordUtil.generateRandomPassword();
        }
        UserDetails userDetailsUser = org.springframework.security.core.userdetails.User.builder()
                .username(myUser.getEmail())
                .password(password)
                .roles(myUser.getRole().name())
                .build();

        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetailsUser, null,
                        authoritiesMapper.mapAuthorities(userDetailsUser.getAuthorities()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
