package com.personal.shoppingmall.wishlist.repository;


import com.personal.shoppingmall.user.entity.User;
import com.personal.shoppingmall.wishlist.entity.WishList;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface WishListRepository extends JpaRepository<WishList, Long> {
    Optional<WishList> findByUser(User user);
}
