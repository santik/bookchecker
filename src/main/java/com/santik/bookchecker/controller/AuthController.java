package com.santik.bookchecker.controller;

import com.santik.bookchecker.producer.login.api.DefaultApi;
import com.santik.bookchecker.producer.login.model.AuthToken;
import com.santik.bookchecker.producer.login.model.User;
import com.santik.bookchecker.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements DefaultApi {

    private final AuthService authService;

    @Override
    public ResponseEntity<AuthToken> login(User user) {
        AuthToken token = authService.getToken(user.getUsername(), user.getPassword());
        return ResponseEntity.ok(token);
    }
}
