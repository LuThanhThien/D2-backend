package com.dainam.D2.controller;

import com.dainam.D2.dto.DtoProvider;
import com.dainam.D2.dto.warehouse.ItemDto;
import com.dainam.D2.models.warehouse.Item;
import com.dainam.D2.service.warehouse.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/item")
@RequiredArgsConstructor
@CrossOrigin("*")
@Slf4j
public class ItemController {

    @Autowired
    private ItemService itemService;

    @GetMapping("hello-world")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello World");
    }

    @GetMapping
    public ResponseEntity<List<ItemDto>> getAll() {
        return ResponseEntity.ok(DtoProvider.build(ItemDto.class).map(itemService.getAll()));
    }

    @PostMapping
    public ResponseEntity<ItemDto> createNewItem(@RequestBody ItemDto itemDto) {
        return ResponseEntity.ok(
                DtoProvider.build(ItemDto.class).map(itemService.create(itemDto))
        );
    }
}
