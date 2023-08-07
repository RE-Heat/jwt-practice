package jwtpractice.jwtpractice.service;

import jwtpractice.jwtpractice.domain.User;
import jwtpractice.jwtpractice.exception.AppException;
import jwtpractice.jwtpractice.exception.ErrorCode;
import jwtpractice.jwtpractice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public String join(String userName, String password) {
        //username 중복 check
        userRepository.findByUserName(userName)
                .ifPresent(user -> {
                    throw new AppException(ErrorCode.USERNAME_DUPLICATED, userName + "는 이미 있습니다.");
                });

        //저장
        User user = User.builder()
                .userName(userName)
                .password(password)
                .build();
        userRepository.save(user);

        return "성공";
    }
}
