package com.personal.shoppingmall.seller.controller;

import com.personal.shoppingmall.seller.dto.SellerSignupRequestDto;
import com.personal.shoppingmall.seller.dto.SellerSignupResponseDto;
import com.personal.shoppingmall.seller.service.SellerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/sellers")
public class SellerController {

    private final SellerService sellerService;

    public SellerController(SellerService sellerService) {
        this.sellerService = sellerService;

    }

    @PostMapping("/signup")
    public ResponseEntity<SellerSignupResponseDto> signupSeller(@RequestBody SellerSignupRequestDto sellerRequestDto) throws Exception {
        SellerSignupResponseDto responseDto = sellerService.signupSeller(sellerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

}
