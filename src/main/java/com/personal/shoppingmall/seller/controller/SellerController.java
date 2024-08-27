package com.personal.shoppingmall.seller.controller;

import com.personal.shoppingmall.security.jwt.JwtTokenProvider;
import com.personal.shoppingmall.seller.dto.*;
import com.personal.shoppingmall.seller.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;
    private final JwtTokenProvider jwtTokenProvider;

    public SellerController(SellerService sellerService, JwtTokenProvider jwtTokenProvider) {
        this.sellerService = sellerService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @PostMapping("/signup")
    public ResponseEntity<SellerSignupResponseDto> signupSeller(@RequestBody SellerSignupRequestDto sellerRequestDto) {
        SellerSignupResponseDto responseDto = sellerService.signupSeller(sellerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/verify/{token}")
    public ResponseEntity<String> verifyEmail(@PathVariable("token") String token) {
        sellerService.verifyEmail(token);
        return ResponseEntity.ok("Email verified successfully.");
    }

    @PostMapping("/login")
    public ResponseEntity<SellerLoginResponseDto> loginSeller(@RequestBody SellerLoginRequestDto loginRequestDto) {
        return sellerService.loginSeller(loginRequestDto);
    }

    @PutMapping("/update")
    public ResponseEntity<SellerResponseDto> updateSeller(@RequestHeader("Authorization") String token,
                                                          @RequestBody SellerUpdateRequestDto sellerRequestDto) {
        // JWT 토큰에서 이메일 추출
        String email = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));

        // 판매자 정보 업데이트
        SellerResponseDto updatedSeller = sellerService.updateSeller(email, sellerRequestDto);

        return ResponseEntity.ok(updatedSeller);
    }

    @GetMapping("/info")
    public ResponseEntity<SellerResponseDto> getSeller(@RequestHeader("Authorization") String token) {
        String email = jwtTokenProvider.getUsernameFromToken(token.replace("Bearer ", ""));

        SellerResponseDto sellerResponseDto = sellerService.getSellerByEmail(email);

        return ResponseEntity.ok(sellerResponseDto);
    }
}