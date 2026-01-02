package com.abiodunelijah.auth_users.services;


import com.abiodunelijah.auth_users.dtos.LoginRequest;
import com.abiodunelijah.auth_users.dtos.LoginResponse;
import com.abiodunelijah.auth_users.dtos.RegistrationRequest;
import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.auth_users.repository.UserRepository;
import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.response.Response;
import com.abiodunelijah.role.entities.Role;
import com.abiodunelijah.role.repository.RoleRepository;
import com.abiodunelijah.security.JwtUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;
    private final RoleRepository roleRepository;


    @Override
    public Response<?> register(RegistrationRequest registrationRequest) {

        log.info("Inside register()");

        if (userRepository.existsByEmail(registrationRequest.getEmail())) {
            throw new BadRequestException("Email already exist.");
        }

        //collect all roles from the request
        List<Role> userRoles;

        if (registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty()) {
            userRoles = registrationRequest.getRoles().stream()
                    .map(roleName -> roleRepository.findByName(roleName.toUpperCase())
                            .orElseThrow(() -> new NotFoundException("Role with name " + roleName + " not found")))
                    .toList();

        } else {
            // if no role is provided, default to CUSTOMER
            Role defaultRole = roleRepository.findByName("CUSTOMER")
                    .orElseThrow(() -> new NotFoundException("Default customer role not found."));

            userRoles = List.of(defaultRole);
        }

        User userToSave = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .phoneNumber(registrationRequest.getPhoneNumber())
                .address(registrationRequest.getAddress())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(userRoles)
                .isActive(true)
                .createdAt(LocalDateTime.now())
                .build();

        userRepository.save(userToSave);

        log.info("user registered successfully.");

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("User registered successfully")
                .build();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        log.info("Inside login()");

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new NotFoundException("Email not found."));

        //checking if user account is active.
        if (!user.isActive()){
            throw new NotFoundException("Account not active, please, contact customer support.");
        }

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())){
            throw new BadRequestException("Invalid password.");
        }

        //generate token
        String toekn = jwtUtils.generateToken(user.getEmail());

        //Extract roles name a list
        List<String> roleNames = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setToken(toekn);
        loginResponse.setRoles(roleNames);

        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User logged in successfully.")
                .data(loginResponse)
                .build();
    }
}
