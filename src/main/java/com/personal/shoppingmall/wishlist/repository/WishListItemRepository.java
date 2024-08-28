package com.personal.shoppingmall.wishlist.repository;

import com.personal.shoppingmall.wishlist.entity.WishListItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WishListItemRepository extends JpaRepository<WishListItem, Long> {

}

