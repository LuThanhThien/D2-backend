package com.dainam.D2.mapper.warehouse;


import com.dainam.D2.annotation.dto.mapper.DtoMapper;
import com.dainam.D2.dto.warehouse.ItemDto;
import com.dainam.D2.models.warehouse.Item;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ItemMapper extends DtoMapper<ItemDto, Item> {

    ItemMapper INSTANCE = Mappers.getMapper(ItemMapper.class);

}
