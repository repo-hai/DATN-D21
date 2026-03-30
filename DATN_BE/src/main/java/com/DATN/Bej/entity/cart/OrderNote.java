package com.DATN.Bej.entity.cart;

import com.DATN.Bej.entity.identity.User;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class OrderNote {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    Orders order;

    String note;
    LocalDateTime updateTime;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User updateBy;

}
