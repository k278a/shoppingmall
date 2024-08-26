package com.personal.shoppingmall.user.controller;

import com.personal.shoppingmall.user.dto.*;
import com.personal.shoppingmall.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<SignupResponse> signupUser(@RequestBody SignupRequest request) {
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

    @PutMapping("/update")
    public ResponseEntity<UserUpdateResponseDto> updateUser(HttpServletRequest request,
                                                            @RequestBody UserUpdateRequestDto profileUpdateRequest) {
        // Authorization 헤더에서 토큰 추출
        String token = request.getHeader("Authorization").substring(7);

        // 서비스 호출하여 프로필 업데이트
        UserUpdateResponseDto response = userService.updateUser(token, profileUpdateRequest);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/profile")
    public ResponseEntity<UserProfileResponseDto> getUserProfile(HttpServletRequest request) {
        // Authorization 헤더에서 토큰 추출
        String token = request.getHeader("Authorization").substring(7);

        // 사용자 프로필 정보 조회
        UserProfileResponseDto response = userService.getUserProfile(token);

        return ResponseEntity.ok(response);
    }
}
