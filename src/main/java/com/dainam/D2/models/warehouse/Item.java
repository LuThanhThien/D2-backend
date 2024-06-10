package com.dainam.D2.models.warehouse;

import com.dainam.D2.models.global.AuditEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@SuperBuilder
@RequiredArgsConstructor
@AllArgsConstructor
@Table(name = "item")
public class Item extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    @Column(name = "item_name")
    private String name;

    @Builder.Default
    @Column(name = "item_quantity")
    private int quantity = 0;

    @Builder.Default
    @Column(name = "item_price")
    private long price = 0;

}
