package com.abiodunelijah.menu.controllers;


import com.abiodunelijah.menu.dtos.MenuDto;
import com.abiodunelijah.menu.services.MenuService;
import com.abiodunelijah.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDto>> createMenu(
            @ModelAttribute @Valid MenuDto menuDTO,
            @RequestPart(value = "imageFile", required = true)MultipartFile imageFile){

        menuDTO.setImageFile(imageFile);

        return ResponseEntity.ok(menuService.createMenu(menuDTO));
    }

    @PutMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<MenuDto>> updateMenu(
            @ModelAttribute @Valid MenuDto menuDTO,
            @RequestPart(value = "imageFile", required = false)MultipartFile imageFile){

        menuDTO.setImageFile(imageFile);

        return ResponseEntity.ok(menuService.updateMenu(menuDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Response<MenuDto>> getMenu(@PathVariable Long id){
        return ResponseEntity.ok(menuService.getMenu(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteMenu(@PathVariable Long id){
        return ResponseEntity.ok(menuService.deleteMenu(id));
    }

    @GetMapping
    public ResponseEntity<Response<List<MenuDto>>> getMenus(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String search ){
        return ResponseEntity.ok(menuService.getMenus(categoryId, search));
    }


}
