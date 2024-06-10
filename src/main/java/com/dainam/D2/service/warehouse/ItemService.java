package com.dainam.D2.service.warehouse;

import com.dainam.D2.dto.warehouse.ItemDto;
import com.dainam.D2.mapper.warehouse.ItemMapper;
import com.dainam.D2.models.warehouse.Item;
import com.dainam.D2.repository.warehouse.IITemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ItemService implements IItemService {

    @Autowired
    private IITemRepository itemRepository;

    @Override
    public List<Item> getAll() {
        return itemRepository.findAll();
    }

    @Override
    public Item create(ItemDto itemDto) {
        if (checkIfExist(itemDto.getName()))
            throw new RuntimeException("Item with name " + itemDto.getName() + " already exists!");
        return itemRepository.save(
                ItemMapper.INSTANCE.toEntity(itemDto)
        );
    }

    @Override
    public boolean checkIfExist(String itemName) {
        Optional<Item> itemOptional = itemRepository.findByName(itemName);
        return itemOptional.isPresent();
    }

}
