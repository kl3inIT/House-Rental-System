package com.rental.houserental.service;

import com.rental.houserental.dto.request.CreatePropertyRequest;
import com.rental.houserental.entity.RentalProperty;
import com.rental.houserental.entity.User;
import org.springframework.web.multipart.MultipartFile;

public interface PropertyService {

    RentalProperty createProperty(CreatePropertyRequest request, User landlord, MultipartFile[] imageFiles);
}
