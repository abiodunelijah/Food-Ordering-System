package com.abiodunelijah.auth_users.services;


import com.abiodunelijah.auth_users.dtos.LoginRequest;
import com.abiodunelijah.auth_users.dtos.LoginResponse;
import com.abiodunelijah.auth_users.dtos.RegistrationRequest;
import com.abiodunelijah.response.Response;

public interface AuthService {

    Response<?> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);
}
