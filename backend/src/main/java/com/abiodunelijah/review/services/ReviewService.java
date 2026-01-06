package com.abiodunelijah.review.services;


import com.abiodunelijah.response.Response;
import com.abiodunelijah.review.dtos.ReviewDto;

import java.util.List;

public interface ReviewService {
    Response<ReviewDto> createReview(ReviewDto reviewDTO);
    Response<List<ReviewDto>> getReviewsForMenu(Long menuId);
    Response<Double> getAverageRating(Long menuId);
}
