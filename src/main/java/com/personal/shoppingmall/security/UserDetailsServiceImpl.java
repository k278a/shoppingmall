package com.personal.shoppingmall.security;

import com.personal.shoppingmall.seller.entity.Seller;
import com.personal.shoppingmall.seller.repository.SellerRepository;
import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Seller 조회
        Seller seller = (Seller) sellerRepository.findByEmail(email).orElse(null);
        if (seller != null) {
            return new UserDetailsImpl(seller);
        }

        // 일반 User 조회
        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new UserDetailsImpl(user);
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
