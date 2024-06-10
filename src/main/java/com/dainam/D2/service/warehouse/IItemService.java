package com.dainam.D2.service.warehouse;


import com.dainam.D2.dto.warehouse.ItemDto;
import com.dainam.D2.models.warehouse.Item;

import java.util.List;

public interface IItemService {


    List<Item> getAll();

    Item create(ItemDto itemDto);

    boolean checkIfExist(String itemName);
}
