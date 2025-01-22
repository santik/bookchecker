package com.santik.bookchecker.service.auth;

import com.santik.bookchecker.exception.UnauthorizedException;
import com.santik.bookchecker.producer.login.model.AuthToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtTokenUtil jwtTokenUtil;

    @Value("${http.username}")
    String username;

    @Value("${http.password}")
    String password;

    public AuthToken getToken(String username, String password) {

        if (
            BCrypt.checkpw(password, this.password) &&
                    BCrypt.checkpw(username, this.username)) {
            String token = jwtTokenUtil.generateToken(username);
            AuthToken authToken = new AuthToken();
            authToken.setToken(token);
            return authToken;
        }

        throw new UnauthorizedException("Invalid credentials");
    }
}
