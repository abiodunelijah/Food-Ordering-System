package com.abiodunelijah.auth_users.services;


import com.abiodunelijah.auth_users.dtos.UserDto;
import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.response.Response;

import java.util.List;

public interface UserService {

    User getCurrentLoggedInUser();
    Response<List<UserDto>> getAllUsers();
    Response<UserDto> getOwnAccountDetails();
    Response<?> updateOwnAccount(UserDto userDTO);
    Response<?> deactivateOwnAccount();
}
