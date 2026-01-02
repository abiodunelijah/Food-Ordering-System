package com.abiodunelijah.role.services;

import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.response.Response;
import com.abiodunelijah.role.dtos.RoleDto;
import com.abiodunelijah.role.entities.Role;
import com.abiodunelijah.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final ModelMapper modelMapper;

    @Override
    public Response<RoleDto> createRole(RoleDto roleDto) {

        Role role = modelMapper.map(roleDto, Role.class);

        Role savedRole = roleRepository.save(role);

        return Response.<RoleDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role created successfully.")
                .data(modelMapper.map(savedRole, RoleDto.class))
                .build();
    }

    @Override
    public Response<RoleDto> updateRole(RoleDto roleDTO) {

        Role existingRole = roleRepository.findById(roleDTO.getId()).orElseThrow(() -> new NotFoundException("Role not found."));

       if (roleRepository.findByName(roleDTO.getName()).isPresent()){
           throw new BadRequestException("Role with the name already exists.");
       }

       existingRole.setName(roleDTO.getName());
        Role updatedRole = roleRepository.save(existingRole);

        return Response.<RoleDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully.")
                .data(modelMapper.map(updatedRole, RoleDto.class))
                .build();
    }

    @Override
    public Response<List<RoleDto>> getAllALLRoles() {

        List<Role> roles = roleRepository.findAll();
        List<RoleDto> roleDTOS = roles.stream()
                .map(role -> modelMapper.map(role, RoleDto.class))
                .toList();

        return  Response.<List<RoleDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully.")
                .data(roleDTOS)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {

        if (!roleRepository.existsById(id)){
            throw new NotFoundException("Role does not exist.");
        }

        roleRepository.deleteById(id);

        return Response.<RoleDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully.")
                .build();
    }
}