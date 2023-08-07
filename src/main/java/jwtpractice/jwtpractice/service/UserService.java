package jwtpractice.jwtpractice.service;

import jwtpractice.jwtpractice.domain.User;
import jwtpractice.jwtpractice.exception.AppException;
import jwtpractice.jwtpractice.exception.ErrorCode;
import jwtpractice.jwtpractice.repository.UserRepository;
import jwtpractice.jwtpractice.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    @Value("${jwt.token.secret}")
    private String key;
    private Long expireTimeMs = 1000 * 60 * 60L; //1시간

    public String join(String userName, String password) {
        //username 중복 check
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        //저장
        User user = User.builder()
                .userName(userName)
                .password(encoder.encode(password))
                .build();
        userRepository.save(user);

        return "성공";
    }

    public String login(String userName, String password) {
        //UserName 없음
        User selectedUser = userRepository.findByUserName(userName)
                .orElseThrow(() -> new AppException(ErrorCode.USERNAME_NOT_FOUND, userName + "이 없습니다."));

        //password 틀림
        log.info("selectedPw:{} pw:{}", selectedUser.getPassword(), password);
        if(!encoder.matches(password, selectedUser.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, "패스워드를 잘못 입력했습니다.");
        }

        String token = JwtUtil.createToken(selectedUser.getUserName(), key, expireTimeMs);

        // Exception 나지 않으면 토큰 발행
        return token;
    }
}
