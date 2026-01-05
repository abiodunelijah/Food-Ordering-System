package com.abiodunelijah.menu.services;


import com.abiodunelijah.aws.AwsS3Service;
import com.abiodunelijah.category.entities.Category;
import com.abiodunelijah.category.repository.CategoryRepository;
import com.abiodunelijah.exceptions.BadRequestException;
import com.abiodunelijah.exceptions.NotFoundException;
import com.abiodunelijah.menu.dtos.MenuDto;
import com.abiodunelijah.menu.entities.Menu;
import com.abiodunelijah.menu.repository.MenuRepository;
import com.abiodunelijah.response.Response;
import com.abiodunelijah.review.entities.Review;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;



@Service
@RequiredArgsConstructor
@Slf4j
public class MenuServiceImpl implements MenuService {

    private final MenuRepository menuRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    private final AwsS3Service awsS3Service;



    @Override
    public Response<MenuDto> createMenu(MenuDto menuDTO) {

        log.info("Inside createMenu()");

        Category category = categoryRepository.findById(menuDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not fount."));

        String imageUrl = null;
        MultipartFile imageFile = menuDTO.getImageFile();

        if (imageFile != null || !imageFile.isEmpty()){
            throw new BadRequestException("menu image is required.");
        }

        String imageName = UUID.randomUUID() + "_" + imageFile.getOriginalFilename();
        URL awsS3url = awsS3Service.uploadFile("menus/" + imageName, imageFile);

        imageUrl = awsS3url.toString();

        Menu menu = Menu.builder()
                .name(menuDTO.getName())
                .description(menuDTO.getDescription())
                .price(menuDTO.getPrice())
                .imageUrl(imageUrl)
                .category(category)
                .build();

        Menu savedMenu = menuRepository.save(menu);


        return Response.<MenuDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu created successfully")
                .data(modelMapper.map(savedMenu, MenuDto.class))
                .build();
    }

    @Override
    public Response<MenuDto> updateMenu(MenuDto menuDTO) {

        log.info("Inside updateMenu()");

       Menu existingMenu = menuRepository.findById(menuDTO.getId())
               .orElseThrow( ()-> new NotFoundException("Menu not found."));

       Category category = categoryRepository.findById(menuDTO.getCategoryId())
                .orElseThrow(() -> new NotFoundException("Category not fount."));

       String imageUrl = null;
       MultipartFile imageFile = menuDTO.getImageFile();

        //check if new imageFile is provided
        if (imageFile != null && !imageFile.isEmpty()) {
            //delete old image in cloud if exists.
            if (imageUrl != null && !imageUrl.isEmpty()) {
                String keyName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
                awsS3Service.deleteFile("menus/" + keyName);

                log.info("Deleted old menu image from s3.");
            }

            //upload new image
            String imageName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
            URL newImageUrl = awsS3Service.uploadFile("menus/" + imageName, imageFile);
            imageUrl = newImageUrl.toString();
        }

        if (menuDTO.getName() != null && !menuDTO.getName().isBlank()){
            existingMenu.setName(menuDTO.getName());
        }
        if (menuDTO.getDescription() != null && !menuDTO.getDescription().isBlank()){
            existingMenu.setDescription(menuDTO.getDescription());
        }
        if (menuDTO.getPrice() != null){
            existingMenu.setPrice(menuDTO.getPrice());
        }

        existingMenu.setImageUrl(imageUrl);
        existingMenu.setCategory(category);

        Menu updatedMenu = menuRepository.save(existingMenu);

        return Response.<MenuDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu successfully updated.")
                .data(modelMapper.map(updatedMenu, MenuDto.class))
                .build();
    }

    @Override
    public Response<MenuDto> getMenu(Long id ) {

        log.info("Inside getMenu()");

        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow( ()-> new NotFoundException("Menu not found."));

        MenuDto menuDTO = modelMapper.map(existingMenu, MenuDto.class);

        //Sort the review in descending order
        if (menuDTO.getReviews() != null){
            menuDTO.getReviews().sort(Comparator.comparing(Review::getId).reversed());
        }

        return Response.<MenuDto>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu successfully retrieved")
                .data(menuDTO)
                .build();
    }

    @Override
    public Response<List<MenuDto>> getMenus(Long id, String search) {
        log.info("Inside getMenus()");

        Specification<Menu> spec = buildSpecification(id, search);
        Sort sort = Sort.by(Sort.Direction.DESC, "id");

        List<Menu> menuList = menuRepository.findAll(spec, sort);
        List<MenuDto> menuDTOS = menuList.stream()
                .map(menu -> modelMapper.map(menu, MenuDto.class))
                .toList();

        return Response.<List<MenuDto>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu retrieved.")
                .data(menuDTOS)
                .build();
    }

    private Specification<Menu> buildSpecification(Long id, String search) {

        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            //Add category filter if category id is provided.
            if (id != null) {
                predicates.add(criteriaBuilder.equal(root.get("category").get("id"), id));
            }

            if (search != null && !search.isEmpty()) {
                String searchTerm = "%" + search.toLowerCase() + "%";

                predicates.add(criteriaBuilder.or(criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), searchTerm), criteriaBuilder.like(criteriaBuilder.lower(root.get("description")), searchTerm)

                ));
            }

            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));

        };
    }

    @Override
    public Response<?> deleteMenu(Long id) {

        log.info("Inside deleteMenu()");

        Menu menuToDelete = menuRepository.findById(id)
                .orElseThrow( ()-> new NotFoundException("Menu not found."));

        //Delete the image from cloud if exists
        String imageUrl = menuToDelete.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()){
            String keyName = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
            awsS3Service.deleteFile("menus/" + keyName);
            log.info("Deleted image from s3: menus/ {}", keyName);
        }

        menuRepository.deleteById(id);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Menu deleted successfully.")
                .build();
    }
}
