package com.abiodunelijah.auth_users.services;


import com.abiodunelijah.auth_users.dtos.UserDto;
import com.abiodunelijah.auth_users.entities.User;
import com.abiodunelijah.auth_users.repository.UserRepository;
import com.abiodunelijah.aws.AwsS3Service;
import com.abiodunelijah.email_notification.dtos.NotificationDto;
import com.abiodunelijah.email_notification.services.NotificationService;
import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.response.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AwsS3Service awsS3Service;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    @Override
    public User getCurrentLoggedInUser() {

        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("user not found."));
    }

    @Override
    public Response<List<UserDto>> getAllUsers() {

        log.info("Inside getAllUSers()");

        List<User> usersList = userRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));

        List<UserDto> userDTOList = modelMapper.map(usersList, new TypeToken<List<UserDto>>() {
        }.getType());

        return Response.<List<UserDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("All users retrieved successfully.")
                .data(userDTOList)
                .build();
    }

    @Override
    public Response<UserDto> getOwnAccountDetails() {

        log.info("Inside getOwnAccountDetails()");

        User currentLoggedInUser = getCurrentLoggedInUser();

        UserDto userDTO = modelMapper.map(currentLoggedInUser, UserDto.class);

        return Response.<UserDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("success.")
                .data(userDTO)
                .build();
    }

    @Override
    public Response<?> updateOwnAccount(UserDto userDto) {

        log.info("Inside updateOwnAccount()");

        //fetch current logged in user
        User currentLoggedInUser = getCurrentLoggedInUser();

        String profileUrl = currentLoggedInUser.getProfileUrl();

        MultipartFile imageFile = userDto.getImageFile();

        //check if new imageFile is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            //delete old image in cloud if exists.
            if (profileUrl != null && !profileUrl.isEmpty()) {
                String keyName = profileUrl.substring(profileUrl.lastIndexOf("/") + 1);
                awsS3Service.deleteFile("profile/" + keyName);

                log.info("Deleted old profile image from s3.");
            }

            //upload new image
            String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            URL uploadFile = awsS3Service.uploadFile("profile/" + imageName, imageFile);
            currentLoggedInUser.setProfileUrl(uploadFile.toString());
        }

        //update other stuff

        if (userDto.getName() != null){
            currentLoggedInUser.setName(userDto.getName());
        }
        if (userDto.getAddress() != null){
            currentLoggedInUser.setAddress(userDto.getAddress());
        }

        if (userDto.getPhoneNumber() != null){
            currentLoggedInUser.setPhoneNumber(userDto.getPhoneNumber());
        }
        if (userDto.getPassword() != null){
            currentLoggedInUser.setPassword(passwordEncoder.encode(userDto.getPassword()));
        }

        if (userDto.getEmail() != null && !userDto.getEmail().equals(currentLoggedInUser.getEmail())){
            if (userRepository.existsByEmail(userDto.getEmail())){
                throw new BadRequestException("Email already exists.");
            }
        }

        currentLoggedInUser.setEmail(userDto.getEmail());

        userRepository.save(currentLoggedInUser);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Account updated successfully.")
                .build();
    }

    @Override
    public Response<?> deactivateOwnAccount() {

        log.info("Inside deactivateOwnAccount()");

        User user = getCurrentLoggedInUser();

        //deactivate the user
        user.setActive(false);
        userRepository.save(user);

        NotificationDto notificationDTO = NotificationDto.builder()
                .recipient(user.getEmail())
                .subject("Account Deactivated.")
                .body("Your account has been deactivated. If this was a mistake, please, contact support.")
                .build();

        notificationService.sendEmail(notificationDTO);
        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Your account has been deactivated.")
                .build();
    }
}
