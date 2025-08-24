package com.ps.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.ps.dto.request.AdminProfileRequest;
import com.ps.dto.response.AdminProfileResponse;
import com.ps.entity.Admin;

@Mapper(componentModel = "spring")
public interface AdminMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "profilePicPath", ignore = true)
    @Mapping(target = "roles", ignore = true)
    @Mapping(target = "isActive", ignore = true)
    Admin toAdmin(AdminProfileRequest request);
    
    AdminProfileResponse toAdminProfileResponse(Admin admin);
}