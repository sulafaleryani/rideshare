package com.rideshare.service;

import com.rideshare.dto.response.PageResponse;
import com.rideshare.dto.response.UserResponse;
import org.springframework.data.domain.Pageable;

public interface UserService {
    PageResponse<UserResponse> getAllUsers(Pageable pageable);
    UserResponse getUserById(Long id);
    UserResponse suspendUser(Long id);
    UserResponse activateUser(Long id);
}
