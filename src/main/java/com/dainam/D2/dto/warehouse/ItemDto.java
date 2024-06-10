package com.dainam.D2.dto.warehouse;

import com.dainam.D2.annotation.dto.Dto;
import com.dainam.D2.mapper.user.UserMapper;
import com.dainam.D2.mapper.warehouse.ItemMapper;
import com.dainam.D2.models.user.User;
import com.dainam.D2.models.warehouse.Item;
import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.SuperBuilder;

@RequiredArgsConstructor
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Dto(mapper = ItemMapper.class, mappedClass = Item.class)
public class ItemDto {

    @NotNull
    private Long id;

    @NotEmpty
    private String name;

    private int quantity;

    private long price;

}
