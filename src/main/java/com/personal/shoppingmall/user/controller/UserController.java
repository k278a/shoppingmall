package com.personal.shoppingmall.user.controller;

import com.personal.shoppingmall.user.dto.LoginRequest;
import com.personal.shoppingmall.user.dto.LoginResponse;
import com.personal.shoppingmall.user.dto.SignupRequest;
import com.personal.shoppingmall.user.dto.SignupResponse;
import com.personal.shoppingmall.user.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<SignupResponse> signupUser(@RequestBody SignupRequest request) throws Exception {
        SignupResponse response = userService.signupUser(request);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable String token) {
        String message = userService.verifyEmail(token);
        return ResponseEntity.ok(message);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> loginUser(@RequestBody LoginRequest request) {
        return userService.loginUser(request);
    }
}
