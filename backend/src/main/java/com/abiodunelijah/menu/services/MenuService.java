package com.abiodunelijah.menu.services;


import com.abiodunelijah.menu.dtos.MenuDto;
import com.abiodunelijah.response.Response;

import java.util.List;

public interface MenuService {

    Response<MenuDto> createMenu(MenuDto menuDTO);
    Response<MenuDto> updateMenu(MenuDto menuDTO);
    Response<MenuDto> getMenu(Long id);
    Response<List<MenuDto>> getMenus(Long id, String search);
    Response<?> deleteMenu(Long id);

}
