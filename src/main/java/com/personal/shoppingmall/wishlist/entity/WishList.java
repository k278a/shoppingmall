package com.personal.shoppingmall.wishlist.entity;

import com.personal.shoppingmall.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "wishlist")
public class WishList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WishListStatus status;

    public WishList(User user) {
        this.user = user;
        this.status = WishListStatus.ACTIVE;
    }

    public enum WishListStatus {
        ACTIVE,
        INACTIVE,
        PURCHASED
    }
}
