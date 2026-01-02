package com.abiodunelijah.role.services;

import com.abiodunelijah.response.Response;
import com.abiodunelijah.role.dtos.RoleDto;


import java.util.List;

public interface RoleService {
    Response<RoleDto> createRole(RoleDto roleDTO);

    Response<RoleDto> updateRole(RoleDto roleDto);
    Response<List<RoleDto>> getAllALLRoles();

    Response<?> deleteRole(Long id);
}
